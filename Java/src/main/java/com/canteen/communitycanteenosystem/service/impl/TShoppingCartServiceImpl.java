package com.canteen.communitycanteenosystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.communitycanteenosystem.entity.TDish;
import com.canteen.communitycanteenosystem.entity.TShoppingCart;
import com.canteen.communitycanteenosystem.mapper.TShoppingCartMapper;
import com.canteen.communitycanteenosystem.service.TDishCategoryService;
import com.canteen.communitycanteenosystem.service.TDishService;
import com.canteen.communitycanteenosystem.service.TShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.service.impl
 * @Author: 夏日花店
 * @CreateTime: 2026/02/06 14:09
 * @Description: 购物车服务实现类
 * @Version: 2.0
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class TShoppingCartServiceImpl extends ServiceImpl<TShoppingCartMapper, TShoppingCart> implements TShoppingCartService {

    // 注入菜品服务，用于校验菜品信息、库存
    @Resource
    private TDishService dishService;

    /**
     * 加入购物车，添加事务保证原子性
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCart(Long userId, Long dishId, Integer count) {
        try {
            // 1. 参数校验
            if (userId == null || dishId == null) {
                throw new RuntimeException("用户ID或菜品ID不能为空");
            }
            if (count <= 0) {
                throw new RuntimeException("购买数量必须大于0");
            }

            // 2. 校验菜品状态
            TDish dish = dishService.getById(dishId);
            if (dish == null) {
                throw new RuntimeException("菜品[" + dishId + "]不存在");
            }
            if (dish.getStatus() != 1) {
                throw new RuntimeException("菜品[" + dish.getDishName() + "]已下架，无法加入购物车");
            }

            // 3. 原子扣减库存（数据库级别，解决并发超卖）
            LambdaUpdateWrapper<TDish> stockWrapper = new LambdaUpdateWrapper<>();
            stockWrapper.eq(TDish::getId, dishId)
                    .ge(TDish::getStock, count) // 库存大于等于购买数量才执行扣减
                    .setSql("stock = stock - " + count);
            boolean updateStock = dishService.update(stockWrapper);

            // 扣减失败，说明库存不足
            if (!updateStock) {
                throw new RuntimeException("菜品[" + dish.getDishName() + "]库存不足，剩余库存：" + dish.getStock());
            }

            // 4. 处理购物车数据（解决唯一约束冲突）
            LambdaQueryWrapper<TShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TShoppingCart::getUserId, userId)
                    .eq(TShoppingCart::getDishId, dishId)
                    .eq(TShoppingCart::getIsDeleted, 0);
            TShoppingCart cart = this.getOne(queryWrapper);

            if (cart != null) {
                // 已存在：累加数量
                cart.setCount(cart.getCount() + count);
                cart.setUpdateTime(LocalDateTime.now());
                this.updateById(cart);
                log.info("用户{}购物车商品{}数量更新为{}", userId, dishId, cart.getCount());
            } else {
                // 新增购物车
                TShoppingCart newCart = new TShoppingCart();
                newCart.setUserId(userId);
                newCart.setDishId(dishId);
                newCart.setMerchantId(dish.getMerchantId());
                newCart.setCount(count);
                newCart.setIsDeleted(0);
                newCart.setDishName(dish.getDishName());
                newCart.setPrice(dish.getPrice());
                newCart.setCoverImg(dish.getCoverImg());
                newCart.setCreateTime(LocalDateTime.now());
                newCart.setUpdateTime(LocalDateTime.now());
                this.save(newCart);
                log.info("用户{}购物车新增商品{}，数量{}", userId, dishId, count);
            }
        } catch (Exception e) {
            log.error("加入购物车失败", e);
            throw e; // 抛出异常，触发事务回滚（库存会回滚）
        }
    }

    /**
     * 查询当前用户的有效购物车数据
     */
    @Override
    public List<TShoppingCart> getCartList(Long userId) {
        if (userId == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        LambdaQueryWrapper<TShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TShoppingCart::getUserId, userId)
                .eq(TShoppingCart::getIsDeleted, 0)
                .orderByDesc(TShoppingCart::getCreateTime); // 按添加时间倒序
        return this.list(wrapper);
    }

    /**
     * 更新购物车中商品的数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCartCount(Long userId, Long dishId, Integer count) {
        try {
            if (userId == null || dishId == null) {
                throw new RuntimeException("用户ID或菜品ID不能为空");
            }
            if (count <= 0) {
                throw new RuntimeException("商品数量必须大于0");
            }

            // 查询原有数量，计算库存调整量
            LambdaQueryWrapper<TShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TShoppingCart::getUserId, userId)
                    .eq(TShoppingCart::getDishId, dishId)
                    .eq(TShoppingCart::getIsDeleted, 0);
            TShoppingCart cart = this.getOne(queryWrapper);
            if (cart == null) {
                throw new RuntimeException("未找到对应的购物车记录");
            }

            // 计算库存调整量（新数量 - 旧数量）
            int stockChange = count - cart.getCount();
            if (stockChange != 0) {
                LambdaUpdateWrapper<TDish> stockWrapper = new LambdaUpdateWrapper<>();
                if (stockChange > 0) {
                    // 数量增加，扣减库存
                    stockWrapper.eq(TDish::getId, dishId)
                            .ge(TDish::getStock, stockChange)
                            .setSql("stock = stock - " + stockChange);
                    boolean update = dishService.update(stockWrapper);
                    if (!update) {
                        throw new RuntimeException("库存不足，无法增加商品数量");
                    }
                } else {
                    // 数量减少，归还库存
                    stockWrapper.eq(TDish::getId, dishId)
                            .setSql("stock = stock + " + Math.abs(stockChange));
                    dishService.update(stockWrapper);
                }
            }

            // 更新购物车数量
            LambdaUpdateWrapper<TShoppingCart> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(TShoppingCart::getUserId, userId)
                    .eq(TShoppingCart::getDishId, dishId)
                    .eq(TShoppingCart::getIsDeleted, 0)
                    .set(TShoppingCart::getCount, count)
                    .set(TShoppingCart::getUpdateTime, LocalDateTime.now());
            boolean update = this.update(wrapper);
            if (!update) {
                throw new RuntimeException("未找到对应的购物车记录");
            }
            log.info("用户{}购物车商品{}数量更新为{}", userId, dishId, count);
        } catch (Exception e) {
            log.error("更新购物车数量失败", e);
            throw e;
        }
    }

    /**
     * 删除购物车单商品，归还库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCartItem(Long userId, Long dishId) {
        try {
            if (userId == null || dishId == null) {
                throw new RuntimeException("用户ID或菜品ID不能为空");
            }

            LambdaQueryWrapper<TShoppingCart> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TShoppingCart::getUserId, userId)
                    .eq(TShoppingCart::getDishId, dishId)
                    .eq(TShoppingCart::getIsDeleted, 0);
            TShoppingCart cart = this.getOne(wrapper);
            if (cart == null) {
                throw new RuntimeException("购物车商品不存在");
            }

            // 归还库存
            LambdaUpdateWrapper<TDish> stockWrapper = new LambdaUpdateWrapper<>();
            stockWrapper.eq(TDish::getId, dishId)
                    .setSql("stock = stock + " + cart.getCount());
            dishService.update(stockWrapper);

            // 逻辑删除购物车记录
            cart.setIsDeleted(1);
            cart.setUpdateTime(LocalDateTime.now());
            this.updateById(cart);
            log.info("用户{}删除购物车商品{}，归还库存{}", userId, dishId, cart.getCount());
        } catch (Exception e) {
            log.error("删除购物车商品失败", e);
            throw e;
        }
    }

    /**
     * 重写清空购物车：清空时批量归还库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearCart(Long userId) {
        try {
            if (userId == null) {
                throw new RuntimeException("用户ID不能为空");
            }

            // 1. 查询所有购物车商品
            List<TShoppingCart> cartList = this.getCartList(userId);
            if (cartList.isEmpty()) {
                log.info("用户{}购物车为空，无需清空", userId);
                return;
            }

            // 2. 批量归还库存
            for (TShoppingCart cart : cartList) {
                LambdaUpdateWrapper<TDish> stockWrapper = new LambdaUpdateWrapper<>();
                stockWrapper.eq(TDish::getId, cart.getDishId())
                        .setSql("stock = stock + " + cart.getCount());
                dishService.update(stockWrapper);
            }

            // 3. 逻辑删除购物车
            LambdaUpdateWrapper<TShoppingCart> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(TShoppingCart::getUserId, userId)
                    .eq(TShoppingCart::getIsDeleted, 0)
                    .set(TShoppingCart::getIsDeleted, 1)
                    .set(TShoppingCart::getUpdateTime, LocalDateTime.now());
            this.update(wrapper);
            log.info("用户{}购物车已清空，共归还{}个商品库存", userId, cartList.size());
        } catch (Exception e) {
            log.error("清空购物车失败", e);
            throw e;
        }
    }

    /**
     * 下单后清空购物车（不归还库存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearCartAfterOrder(Long userId, List<Long> dishIds) {
        try {
            if (userId == null || dishIds == null || dishIds.isEmpty()) {
                log.warn("清空购物车参数异常：userId={}, dishIds={}", userId, dishIds);
                return;
            }

            // 仅更新当前订单包含的商品，而不是全量更新用户的购物车
            LambdaUpdateWrapper<TShoppingCart> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(TShoppingCart::getUserId, userId)
                    .eq(TShoppingCart::getIsDeleted, 0)
                    .in(TShoppingCart::getDishId, dishIds)
                    .set(TShoppingCart::getIsDeleted, 1)
                    .set(TShoppingCart::getUpdateTime, LocalDateTime.now());

            long updateCount = this.update(wrapper) ? this.getBaseMapper().selectCount(wrapper) : 0;
            log.info("用户{}下单后清空购物车，共处理{}个商品", userId, updateCount);
        } catch (Exception e) {
            log.error("下单后清空购物车失败", e);
            throw e;
        }
    }
}