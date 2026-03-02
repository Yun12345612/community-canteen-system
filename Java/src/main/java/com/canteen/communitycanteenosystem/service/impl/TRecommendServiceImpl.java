package com.canteen.communitycanteenosystem.service.impl;

import com.canteen.communitycanteenosystem.entity.TDishSimilarity;
import com.canteen.communitycanteenosystem.entity.TDish;
import com.canteen.communitycanteenosystem.entity.TUserSimilarity;
import com.canteen.communitycanteenosystem.service.TRecommendService;
import com.canteen.communitycanteenosystem.util.ItemCFAlgorithm;
import com.canteen.communitycanteenosystem.util.UserCFAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled; // ✅ 新增导入：定时任务注解
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 推荐服务实现类
 */
@Service
@RequiredArgsConstructor
public class TRecommendServiceImpl implements TRecommendService {

    /** ====================== 缓存 Key 定义 ====================== */
    private static final String SIMILARITY_CACHE_KEY = "dish:similarity";
    private static final String HOT_DISH_CACHE_KEY = "dish:hot";
    private static final String USER_SIM_CACHE_KEY = "user:similarity";
    private static final String USER_ITEM_MAP_KEY = "user:item:map";

    /** ====================== 依赖注入 ====================== */
    private final JdbcTemplate jdbcTemplate;
    private final ItemCFAlgorithm itemCFAlgorithm;
    private final UserCFAlgorithm userCFAlgorithm;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * ⏰ 定时任务：每分钟执行一次全量计算
     *
     * 【如何修改时间？请看下方的 cron 表达式说明】
     * cron 表达式格式：秒 分 时 日 月 周
     * 当前设置："0 * * * * ?" -> 每分钟的第 0 秒执行一次
     */
    @Scheduled(cron = "0/30 * * * * ?")
    @Override
    public void refreshAllMatrix() {
        System.out.println("[" + new Date() + "] >>> 开始执行定时推荐矩阵计算...");

        // 查询最近 3 个月的订单数据用于计算
        // 使用 JOIN 关联 t_order 表获取 user_id
        String sql = "SELECT o.user_id, oi.dish_id " +
                "FROM t_order_item oi " +
                "JOIN t_order o ON oi.order_id = o.id " +
                "WHERE o.create_time > DATE_SUB(NOW(), INTERVAL 3 MONTH)";
        List<Map<String, Object>> orderItems = jdbcTemplate.queryForList(sql);

        if(CollectionUtils.isEmpty(orderItems)){
            System.out.println("⚠️ 无订单数据，跳过相似度计算");
            return;
        }

        // ================= ItemCF 计算 =================
        System.out.println("   - 正在计算 ItemCF 矩阵...");
        Map<Long, Set<Long>> itemUserMap = itemCFAlgorithm.buildItemUserMap(orderItems);
        Map<Long, Map<Long, Integer>> coMatrix = itemCFAlgorithm.buildCoOccurrenceMatrix(itemUserMap);
        Map<Long, List<TDishSimilarity>> similarityMap = itemCFAlgorithm.calcSimilarityMatrix(itemUserMap, coMatrix);
        redisTemplate.opsForValue().set(SIMILARITY_CACHE_KEY, similarityMap);

        // ================= UserCF 计算 =================
        System.out.println("   - 正在计算 UserCF 矩阵...");
        Map<Long, Set<Long>> userItemMap = userCFAlgorithm.buildUserItemMap(orderItems);
        Map<Long, List<TUserSimilarity>> userSimMap = userCFAlgorithm.calcUserSimilarity(userItemMap);
        redisTemplate.opsForValue().set(USER_SIM_CACHE_KEY, userSimMap);
        redisTemplate.opsForValue().set(USER_ITEM_MAP_KEY, userItemMap);

        System.out.println("[" + new Date() + "] <<< ItemCF + UserCF 相似度矩阵刷新完成");
    }

    @Override
    public List<TDish> getUserRecommend(Long userId, int topN) {
        Object cacheObj = redisTemplate.opsForValue().get(SIMILARITY_CACHE_KEY);
        if (cacheObj == null) {
            System.out.println("⚠️ 缓存未命中，返回热门菜品兜底");
            return getHotDishes(topN);
        }

        @SuppressWarnings("unchecked")
        Map<Long, List<TDishSimilarity>> similarityMap = (Map<Long, List<TDishSimilarity>>) cacheObj;

        String userDishSql = "SELECT DISTINCT oi.dish_id " +
                "FROM t_order_item oi " +
                "JOIN t_order o ON oi.order_id = o.id " +
                "WHERE o.user_id = ?";
        List<Long> userOwnDishList = jdbcTemplate.queryForList(userDishSql, Long.class, userId);
        Set<Long> userOwnDishes = new HashSet<>(userOwnDishList);

        List<Long> recommendDishIds = itemCFAlgorithm.recommendForUser(userId, userOwnDishes, similarityMap, topN);
        return recommendDishIds.isEmpty() ? getHotDishes(topN) : listDishesByIds(recommendDishIds);
    }

    @Override
    public List<TDish> getRelatedDish(Long dishId, int topN) {
        Object cacheObj = redisTemplate.opsForValue().get(SIMILARITY_CACHE_KEY);
        if (cacheObj == null) {
            return getHotDishes(topN);
        }

        @SuppressWarnings("unchecked")
        Map<Long, List<TDishSimilarity>> similarityMap = (Map<Long, List<TDishSimilarity>>) cacheObj;

        if (!similarityMap.containsKey(dishId)) {
            return getHotDishes(topN);
        }

        List<Long> relatedIds = similarityMap.get(dishId).stream()
                .limit(topN)
                .map(TDishSimilarity::getRelatedDishId)
                .collect(Collectors.toList());
        return listDishesByIds(relatedIds);
    }

    @Override
    public List<TDish> getHybridRecommend(Long userId, int topN) {
        // 获取缓存并判空
        @SuppressWarnings("unchecked")
        Map<Long, List<TDishSimilarity>> itemSimMap = Optional.ofNullable(redisTemplate.opsForValue().get(SIMILARITY_CACHE_KEY))
                .map(obj -> (Map<Long, List<TDishSimilarity>>) obj)
                .orElse(new HashMap<>());

        @SuppressWarnings("unchecked")
        Map<Long, List<TUserSimilarity>> userSimMap = Optional.ofNullable(redisTemplate.opsForValue().get(USER_SIM_CACHE_KEY))
                .map(obj -> (Map<Long, List<TUserSimilarity>>) obj)
                .orElse(new HashMap<>());

        @SuppressWarnings("unchecked")
        Map<Long, Set<Long>> userItemMap = Optional.ofNullable(redisTemplate.opsForValue().get(USER_ITEM_MAP_KEY))
                .map(obj -> (Map<Long, Set<Long>>) obj)
                .orElse(new HashMap<>());

        Set<Long> userOwnDishes = userItemMap.getOrDefault(userId, Collections.emptySet());

        // 获取多一点的候选集，以便后续加权排序
        List<Long> itemCfResult = itemCFAlgorithm.recommendForUser(userId, userOwnDishes, itemSimMap, topN * 2);
        List<Long> userCfResult = userCFAlgorithm.recommendByUserCF(userId, userItemMap, userSimMap, topN * 2);

        Map<Long, Double> scoreMap = new HashMap<>();

        // 加权评分：ItemCF 权重 0.6
        for (int i = 0; i < itemCfResult.size(); i++) {
            scoreMap.merge(itemCfResult.get(i), 0.6 * (1.0 / (i + 1)), Double::sum);
        }
        // 加权评分：UserCF 权重 0.4
        for (int i = 0; i < userCfResult.size(); i++) {
            scoreMap.merge(userCfResult.get(i), 0.4 * (1.0 / (i + 1)), Double::sum);
        }

        List<Long> finalDishIds = scoreMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return finalDishIds.isEmpty() ? getHotDishes(topN) : listDishesByIds(finalDishIds);
    }

    /**
     * 根据菜品 ID 集合查询菜品信息
     */
    private List<TDish> listDishesByIds(List<Long> dishIds) {
        if (CollectionUtils.isEmpty(dishIds)) return Collections.emptyList();

        // 防止 SQL 注入，实际生产建议使用 PreparedStatement 或 MyBatis
        String inSql = dishIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        String sql = "SELECT id, dish_name, price, cover_img, status FROM t_dish WHERE id IN (" + inSql + ") AND status = 1";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            TDish dish = new TDish();
            dish.setId(rs.getLong("id"));
            dish.setDishName(rs.getString("dish_name"));
            dish.setPrice(rs.getDouble("price"));
            dish.setCoverImg(rs.getString("cover_img"));
            dish.setStatus(rs.getLong("status"));
            return dish;
        });
    }

    /**
     * 获取热门菜品（兜底逻辑）
     */
    private List<TDish> getHotDishes(int topN) {
        List<TDish> hotDishes = Optional.ofNullable(redisTemplate.opsForValue().get(HOT_DISH_CACHE_KEY))
                .map(obj -> (List<TDish>) obj)
                .orElse(null);

        if (!CollectionUtils.isEmpty(hotDishes)) {
            return hotDishes.stream().limit(topN).collect(Collectors.toList());
        }

        String sql = "SELECT d.id, d.dish_name, d.price, d.cover_img, d.status, COUNT(oi.id) as sale_count " +
                "FROM t_order_item oi " +
                "JOIN t_dish d ON oi.dish_id = d.id " +
                "WHERE d.status = 1 " +
                "GROUP BY d.id, d.dish_name, d.price, d.cover_img, d.status " +
                "ORDER BY sale_count DESC LIMIT ?";

        hotDishes = jdbcTemplate.query(sql, (rs, rowNum) -> {
            TDish dish = new TDish();
            dish.setId(rs.getLong("id"));
            dish.setDishName(rs.getString("dish_name"));
            dish.setPrice(rs.getDouble("price"));
            dish.setCoverImg(rs.getString("cover_img"));
            dish.setStatus(rs.getLong("status"));
            return dish;
        }, topN);

        // 缓存 1 小时
        redisTemplate.opsForValue().set(HOT_DISH_CACHE_KEY, hotDishes, 1, TimeUnit.HOURS);
        return hotDishes;
    }
}