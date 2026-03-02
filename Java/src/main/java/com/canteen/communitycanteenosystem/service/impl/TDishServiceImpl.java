package com.canteen.communitycanteenosystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.canteen.communitycanteenosystem.config.MerchantProperties;
import com.canteen.communitycanteenosystem.entity.TDish;
import com.canteen.communitycanteenosystem.mapper.TDishMapper;
import com.canteen.communitycanteenosystem.service.TDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @BelongsProject: community-canteen-system
 * @BelongsPackage: com.canteen.communitycanteenosystem.service.impl
 * @Author: 夏日花店
 * @CreateTime: 2026/1/28 11:05
 * @Description: 菜品业务实现类
 * @Version: 1.0
 */
@Service
public class TDishServiceImpl extends ServiceImpl<TDishMapper, TDish> implements TDishService {

    @Autowired
    private TDishMapper dishMapper;
    @Autowired
    private MerchantProperties merchantProperties;
    
    @Value("${file.access.url-prefix:http://127.0.0.1:8080/uploads/}")
    private String accessUrlPrefix;

    // Long merchantId入参
    @Override
    public boolean addDish(Long merchantId, TDish dish) {
        // 基础参数校验保留
        if (!StringUtils.hasText(dish.getDishName())
                || dish.getPrice() == null
                || dish.getCategoryId() == null) {
            return false;
        }
        if (dish.getPrice() <= 0) {
            return false;
        }
        //  手动将Controller透传的merchantId设置到实体
        dish.setMerchantId(merchantId);

        if (dish.getStock() == null) {
            dish.setStock(0L);
        }
        return dishMapper.insertDish(dish) > 0;
    }

    // Long merchantId入参
    @Override
    public boolean editDish(Long merchantId, TDish dish) {
        // 基础校验保留
        if (dish.getId() == null) {
            return false;
        }
        dish.setMerchantId(merchantId);

        int count = dishMapper.countDishByIdAndMerchantId(dish.getId(), dish.getMerchantId());
        if (count == 0) {
            return false;
        }
        if (dish.getPrice() != null && dish.getPrice() <= 0) {
            return false;
        }
        return dishMapper.updateDish(dish) > 0;
    }

    @Override
    public boolean addDish(TDish dish) {
        // 1. 核心参数校验：商家ID、菜品名、价格、分类ID不能为空
        if (dish.getMerchantId() == null
                || !StringUtils.hasText(dish.getDishName())
                || dish.getPrice() == null
                || dish.getCategoryId() == null) {
            return false;
        }
        // 2. 价格校验：必须大于0
        if (dish.getPrice() <= 0) {
            return false;
        }
        // 3. 库存默认值：为空则设为0
        if (dish.getStock() == null) {
            dish.setStock(0L);
        }
        // 4. 执行添加（默认上架由XML保证）
        return dishMapper.insertDish(dish) > 0;
    }

    @Override
    public boolean editDish(TDish dish) {
        // 1. 基础校验：菜品ID、商家ID不能为空
        if (dish.getId() == null || dish.getMerchantId() == null) {
            return false;
        }
        // 2. 归属校验：确保菜品是当前商家的
        int count = dishMapper.countDishByIdAndMerchantId(dish.getId(), dish.getMerchantId());
        if (count == 0) {
            return false;
        }
        // 3. 价格校验：若修改价格，必须大于0
        if (dish.getPrice() != null && dish.getPrice() <= 0) {
            return false;
        }
        // 4. 执行修改
        return dishMapper.updateDish(dish) > 0;
    }

    @Override
    public boolean takeDownDish(Long dishId, Long merchantId) {
        // 1. 参数校验
        if (dishId == null || merchantId == null) {
            return false;
        }
        // 2. 归属校验
        int count = dishMapper.countDishByIdAndMerchantId(dishId, merchantId);
        if (count == 0) {
            return false;
        }
        // 3. 执行下架（status=0）
        return dishMapper.updateDishStatus(dishId, merchantId, 0L) > 0;
    }

    @Override
    public boolean putOnDish(Long dishId, Long merchantId) {
        // 1. 参数校验
        if (dishId == null || merchantId == null) {
            return false;
        }
        // 2. 归属校验
        int count = dishMapper.countDishByIdAndMerchantId(dishId, merchantId);
        if (count == 0) {
            return false;
        }
        // 3. 执行上架（status=1）
        return dishMapper.updateDishStatus(dishId, merchantId, 1L) > 0;
    }

    @Override
    public List<TDish> getMyDishList(Long merchantId) {
        // 参数校验：商家ID不能为空
        if (merchantId == null) {
            return null;
        }
        // 执行查询
        List<TDish> dishList = dishMapper.selectDishListByMerchantId(merchantId);
        // 标准化图片URL
        return normalizeDishImages(dishList);
    }

    @Override
    public boolean deleteDish(Long dishId, Long merchantId) {
        if (dishId == null || merchantId == null) {
            return false;
        }
        int count = dishMapper.countDishByIdAndMerchantId(dishId, merchantId);
        if (count == 0) {
            return false;
        }
        return dishMapper.deleteDish(dishId, merchantId) > 0;
    }

    /**
     * 用户端：查询所有上架菜品（默认取配置文件的商家ID）
     */
    @Override
    public List<TDish> getOnSaleDishList() {
        // 获取默认商家ID（和Controller逻辑一致）
        Long defaultMerchantId = merchantProperties.getDefaultMerchantId();
        // 构造查询条件：商家ID=默认商家 + 状态=1（上架）
        LambdaQueryWrapper<TDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDish::getMerchantId, defaultMerchantId)
                .eq(TDish::getStatus, 1L) // 只查上架菜品
                .orderByDesc(TDish::getCreateTime);
        List<TDish> dishList = this.list(queryWrapper);
        // 标准化图片URL
        return normalizeDishImages(dishList);
    }

    /**
     * 用户端：根据ID查询菜品详情（不限制商家，兼容所有菜品）
     */
    @Override
    public TDish getDishDetailById(Long dishId) {
        if (dishId == null) {
            return null;
        }
        // 直接用MyBatis-Plus的getById（通用查询，不限制商家）
        TDish dish = this.getById(dishId);
        // 标准化图片URL
        if (dish != null) {
            dish.setCoverImg(normalizeImageUrl(dish.getCoverImg()));
        }
        return dish;
    }
    
    /**
     * 批量标准化菜品图片URL
     */
    private List<TDish> normalizeDishImages(List<TDish> dishList) {
        if (dishList == null) {
            return null;
        }
        return dishList.stream().map(dish -> {
            if (dish != null) {
                dish.setCoverImg(normalizeImageUrl(dish.getCoverImg()));
            }
            return dish;
        }).collect(Collectors.toList());
    }
    
    /**
     * 标准化图片URL：确保图片URL使用统一的主机地址
     * 将 localhost 替换为 127.0.0.1，与前端uniapp配置保持一致
     */
    private String normalizeImageUrl(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            return imageUrl;
        }
        String url = imageUrl.trim();
        url = url.replace("http://localhost:", "http://127.0.0.1:");
        url = url.replace("https://localhost:", "https://127.0.0.1:");
        if (url.startsWith("/uploads/")) {
            String prefix = accessUrlPrefix;
            if (!prefix.endsWith("/")) {
                prefix = prefix + "/";
            }
            return prefix + url.substring("/uploads/".length());
        }
        if (url.startsWith("uploads/")) {
            String prefix = accessUrlPrefix;
            if (!prefix.endsWith("/")) {
                prefix = prefix + "/";
            }
            return prefix + url.substring("uploads/".length());
        }
        return url;
    }
}
