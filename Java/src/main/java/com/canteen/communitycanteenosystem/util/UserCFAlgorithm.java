package com.canteen.communitycanteenosystem.util;

import com.canteen.communitycanteenosystem.entity.TUserSimilarity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.util
 * @Author: 夏日花店
 * @CreateTime: 2026/02/05 23:09
 * @Description: 用户协同过滤的核心算法类
 * @Version: 2.0
 */
@Component
public class UserCFAlgorithm {
    /**
     * 构建 用户-物品倒排表 key:用户ID value:购买的菜品ID集合
     */
    public Map<Long, Set<Long>> buildUserItemMap(List<Map<String, Object>> orderItems) {
        Map<Long, Set<Long>> userItemMap = new HashMap<>();
        for (Map<String, Object> item : orderItems) {
            Long userId = Long.valueOf(item.get("user_id").toString());
            Long dishId = Long.valueOf(item.get("dish_id").toString());
            userItemMap.computeIfAbsent(userId, k -> new HashSet<>()).add(dishId);
        }
        return userItemMap;
    }

    /**
     * 计算用户相似度矩阵（余弦相似度）
     */
    public Map<Long, List<TUserSimilarity>> calcUserSimilarity(Map<Long, Set<Long>> userItemMap) {
        Map<Long, List<TUserSimilarity>> userSimMap = new HashMap<>();
        // 物品-用户倒排表：辅助计算共同菜品
        Map<Long, Set<Long>> itemUserMap = new HashMap<>();
        for (Map.Entry<Long, Set<Long>> entry : userItemMap.entrySet()) {
            Long userId = entry.getKey();
            for (Long dishId : entry.getValue()) {
                itemUserMap.computeIfAbsent(dishId, k -> new HashSet<>()).add(userId);
            }
        }

        // 统计用户共同购买菜品数
        Map<Long, Map<Long, Integer>> coUserMap = new HashMap<>();
        for (Set<Long> users : itemUserMap.values()) {
            List<Long> userList = new ArrayList<>(users);
            for (int i = 0; i < userList.size(); i++) {
                for (int j = i + 1; j < userList.size(); j++) {
                    Long u1 = userList.get(i);
                    Long u2 = userList.get(j);
                    coUserMap.computeIfAbsent(u1, k -> new HashMap<>()).put(u2, coUserMap.get(u1).getOrDefault(u2, 0) + 1);
                    coUserMap.computeIfAbsent(u2, k -> new HashMap<>()).put(u1, coUserMap.get(u2).getOrDefault(u1, 0) + 1);
                }
            }
        }

        // 计算余弦相似度
        for (Map.Entry<Long, Map<Long, Integer>> entry : coUserMap.entrySet()) {
            Long u1 = entry.getKey();
            int count1 = userItemMap.get(u1).size();
            for (Map.Entry<Long, Integer> innerEntry : entry.getValue().entrySet()) {
                Long u2 = innerEntry.getKey();
                int coCount = innerEntry.getValue();
                int count2 = userItemMap.get(u2).size();

                double sim = coCount / Math.sqrt(count1 * count2);
                TUserSimilarity simVO = new TUserSimilarity();
                simVO.setUserId(u1);
                simVO.setSimilarUserId(u2);
                simVO.setSimilarity(Math.round(sim * 10000) / 10000.0);

                userSimMap.computeIfAbsent(u1, k -> new ArrayList<>()).add(simVO);
            }
        }

        // 相似度降序排序
        userSimMap.forEach((k, v) -> v.sort(Comparator.comparing(TUserSimilarity::getSimilarity).reversed()));
        return userSimMap;
    }

    /**
     * UserCF 生成推荐菜品ID
     * @param userId 目标用户
     * @param userItemMap 用户-菜品表
     * @param userSimMap 用户相似度矩阵
     * @param topN 推荐数量
     */
    public List<Long> recommendByUserCF(Long userId, Map<Long, Set<Long>> userItemMap,
                                        Map<Long, List<TUserSimilarity>> userSimMap, int topN) {
        Set<Long> ownDishes = userItemMap.getOrDefault(userId, Collections.emptySet());
        if (ownDishes.isEmpty() || !userSimMap.containsKey(userId)) {
            return Collections.emptyList();
        }

        // 推荐评分：菜品ID -> 总得分
        Map<Long, Double> scoreMap = new HashMap<>();
        List<TUserSimilarity> similarUsers = userSimMap.get(userId);
        // 取Top10相似用户
        List<TUserSimilarity> topSimUsers = similarUsers.stream().limit(10).collect(Collectors.toList());

        for (TUserSimilarity sim : topSimUsers) {
            Long simUserId = sim.getSimilarUserId();
            Double simScore = sim.getSimilarity();
            Set<Long> simUserDishes = userItemMap.get(simUserId);

            for (Long dishId : simUserDishes) {
                // 排除已购买菜品
                if (ownDishes.contains(dishId)) continue;
                scoreMap.put(dishId, scoreMap.getOrDefault(dishId, 0.0) + simScore);
            }
        }

        // 排序取TopN
        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}