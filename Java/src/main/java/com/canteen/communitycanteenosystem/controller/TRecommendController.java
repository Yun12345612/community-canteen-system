package com.canteen.communitycanteenosystem.controller;

import com.canteen.communitycanteenosystem.entity.TDish;
import com.canteen.communitycanteenosystem.service.TRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * topN 是推荐接口里的一个数量控制参数，用来指定 “你想要返回多少个推荐结果”。
 * 比如你传 topN=5，接口就会返回排名前 5 的推荐菜品；传 topN=10，就返回前 10 个，以此类推。
 * 它的作用是灵活控制推荐结果的数量，适配不同的前端展示场景（比如首页展示 5 个、详情页展示 3 个）。
 */
@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class TRecommendController {

    private final TRecommendService recommendService;

    /**
     * 用户个性化推荐
     * 接口：/api/recommend/user?userId=1&topN=5
     */
    @GetMapping("/user")
    public ResponseEntity<List<TDish>> userRecommend(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "5") int topN) {
        return ResponseEntity.ok(recommendService.getUserRecommend(userId, topN));
    }

    /**
     * 菜品搭配推荐
     * 接口：/api/recommend/related?dishId=101&topN=3
     */
    @GetMapping("/related")
    public ResponseEntity<List<TDish>> relatedRecommend(
            @RequestParam Long dishId,
            @RequestParam(defaultValue = "3") int topN) {
        return ResponseEntity.ok(recommendService.getRelatedDish(dishId, topN));
    }

    /**
     * 融合算法推荐（首页猜你喜欢）
     */
    @GetMapping("/hybrid")
    public ResponseEntity<List<TDish>> hybridRecommend(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "5") int topN) {
        return ResponseEntity.ok(recommendService.getHybridRecommend(userId, topN));
    }
}