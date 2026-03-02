package com.canteen.communitycanteenosystem.util;

import com.canteen.communitycanteenosystem.entity.TDishSimilarity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.util
 * @Author: 夏日花店
 * @CreateTime: 2026/02/05 23:13
 * @Description: 物品协同过滤的核心算法类
 * @Version: 2.0
 */
@Component
public class ItemCFAlgorithm {

    /**
     * 构建 物品-用户倒排表 key:菜品ID value:购买该菜品的用户ID集合
     */
    public Map<Long, Set<Long>> buildItemUserMap(List<Map<String, Object>> orderItems) {
        Map<Long, Set<Long>> itemUserMap = new HashMap<>();
        for (Map<String, Object> item : orderItems) {
            Long dishId = Long.valueOf(item.get("dish_id").toString());
            Long userId = Long.valueOf(item.get("user_id").toString());

            itemUserMap.computeIfAbsent(dishId, k -> new HashSet<>()).add(userId);
        }
        return itemUserMap;
    }

    /**
     * 构建 菜品共现矩阵 key1:菜品A key2:菜品B value:共同购买次数
     */
    public Map<Long, Map<Long, Integer>> buildCoOccurrenceMatrix(Map<Long, Set<Long>> itemUserMap) {
        Map<Long, Map<Long, Integer>> coMatrix = new HashMap<>();
        // 遍历每个菜品的用户集合
        for (Map.Entry<Long, Set<Long>> entry : itemUserMap.entrySet()) {
            Long dishA = entry.getKey();
            Set<Long> usersA = entry.getValue();

            // 遍历所有菜品，计算两两共现次数
            for (Map.Entry<Long, Set<Long>> innerEntry : itemUserMap.entrySet()) {
                Long dishB = innerEntry.getKey();
                if (dishA.equals(dishB)) continue;

                Set<Long> usersB = innerEntry.getValue();
                // 计算交集大小（共同购买用户数）
                int count = (int) usersA.stream().filter(usersB::contains).count();

                if (count > 0) {
                    coMatrix.computeIfAbsent(dishA, k -> new HashMap<>()).put(dishB, count);
                }
            }
        }
        return coMatrix;
    }

    /**
     * 计算余弦相似度，生成相似度矩阵
     * 公式：sim(i,j) = 交集数 / sqrt(用户数i * 用户数j)
     */
    public Map<Long, List<TDishSimilarity>> calcSimilarityMatrix(
            Map<Long, Set<Long>> itemUserMap,
            Map<Long, Map<Long, Integer>> coMatrix) {

        Map<Long, List<TDishSimilarity>> similarityMap = new HashMap<>();

        for (Map.Entry<Long, Map<Long, Integer>> entry : coMatrix.entrySet()) {
            Long dishA = entry.getKey();
            int countA = itemUserMap.get(dishA).size();

            for (Map.Entry<Long, Integer> innerEntry : entry.getValue().entrySet()) {
                Long dishB = innerEntry.getKey();
                int coCount = innerEntry.getValue();
                int countB = itemUserMap.get(dishB).size();

                // 余弦相似度计算
                double similarity = coCount / Math.sqrt(countA * countB);

                TDishSimilarity similarityVO = new TDishSimilarity();
                similarityVO.setDishId(dishA);
                similarityVO.setRelatedDishId(dishB);
                similarityVO.setSimilarity(Math.round(similarity * 10000) / 10000.0);

                similarityMap.computeIfAbsent(dishA, k -> new ArrayList<>()).add(similarityVO);
            }
        }

        // 对每个菜品的相似菜品按相似度降序排序
        similarityMap.forEach((k, v) -> v.sort(Comparator.comparing(TDishSimilarity::getSimilarity).reversed()));
        return similarityMap;
    }

    /**
     * 为指定用户生成推荐菜品ID列表
     * @param userId 目标用户ID
     * @param userOwnDishes 用户已购买的菜品ID集合
     * @param similarityMap 菜品相似度矩阵
     * @param topN 推荐数量
     * @return 推荐菜品ID列表
     */
    public List<Long> recommendForUser(Long userId, Set<Long> userOwnDishes,
                                       Map<Long, List<TDishSimilarity>> similarityMap, int topN) {
        // 无历史记录，返回空（上层做兜底）
        if (userOwnDishes.isEmpty()) {
            return Collections.emptyList();
        }

        // 推荐评分：key=菜品ID value=总相似度得分
        Map<Long, Double> scoreMap = new HashMap<>();

        // 遍历用户已购菜品，累加相似菜品得分
        for (Long ownDish : userOwnDishes) {
            List<TDishSimilarity> similarList = similarityMap.get(ownDish);
            if (similarList == null || similarList.isEmpty()) continue;

            for (TDishSimilarity sim : similarList) {
                Long relatedDish = sim.getRelatedDishId();
                // 排除用户已经购买过的菜品
                if (userOwnDishes.contains(relatedDish)) continue;

                scoreMap.put(relatedDish, scoreMap.getOrDefault(relatedDish, 0.0) + sim.getSimilarity());
            }
        }

        // 按得分排序，取前N个
        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topN)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
