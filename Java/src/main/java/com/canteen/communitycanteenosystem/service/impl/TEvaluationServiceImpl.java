package com.canteen.communitycanteenosystem.service.impl;

import com.canteen.communitycanteenosystem.common.BusinessException;
import com.canteen.communitycanteenosystem.dto.DishEvaluationDTO;
import com.canteen.communitycanteenosystem.dto.EvaluationSubmitDTO;
import com.canteen.communitycanteenosystem.entity.TDish;
import com.canteen.communitycanteenosystem.entity.TDishEvaluation;
import com.canteen.communitycanteenosystem.entity.TEvaluation;
import com.canteen.communitycanteenosystem.entity.TOrder;
import com.canteen.communitycanteenosystem.entity.TOrderItem;
import com.canteen.communitycanteenosystem.entity.TUser;
import com.canteen.communitycanteenosystem.mapper.*;
import com.canteen.communitycanteenosystem.service.TEvaluationService;
import com.canteen.communitycanteenosystem.vo.DishEvaluationVO;
import com.canteen.communitycanteenosystem.vo.EvaluationDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TEvaluationServiceImpl implements TEvaluationService {

    @Autowired
    private TEvaluationMapper evaluationMapper;

    @Autowired
    private TMerchantMapper merchantMapper;

    @Autowired
    private TDishEvaluationMapper dishEvaluationMapper;

    @Autowired
    private TOrderMapper orderMapper;

    @Autowired
    private TOrderItemMapper orderItemMapper;

    @Autowired
    private TDishMapper dishMapper;

    @Autowired
    private TUserMapper userMapper;

    @Override
    public List<TEvaluation> getMerchantEvaluationList(Long merchantId) {
        if (merchantId == null || merchantMapper.countMerchantById(merchantId) == 0) {
            return new ArrayList<>();
        }
        List<TEvaluation> evaluationList = evaluationMapper.listByMerchantId(merchantId);
        // 确保返回空列表而非null，避免前端报错
        if (evaluationList == null) {
            return new ArrayList<>();
        }
        // 补充orderNo字段（如果Mapper已关联t_order，这里已有值，无需额外处理）
        return evaluationList;
    }

    @Override
    public boolean replyEvaluation(Long evaluationId, Long merchantId, String merchantReply) {
        if (evaluationId == null || merchantId == null || !StringUtils.hasText(merchantReply)) {
            return false;
        }
        if (merchantMapper.countMerchantById(merchantId) == 0) {
            return false;
        }
        if (evaluationMapper.countByEvalIdAndMerchantId(evaluationId, merchantId) == 0) {
            return false;
        }
        // 补充更新时间
        TEvaluation evaluation = new TEvaluation();
        evaluation.setId(evaluationId);
        evaluation.setMerchantReply(merchantReply);
        evaluation.setReplyTime(LocalDateTime.now());
        evaluation.setUpdateTime(LocalDateTime.now());
        return evaluationMapper.updateById(evaluation) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitEvaluation(EvaluationSubmitDTO dto) {
        log.info("接收到的评价提交参数：{}", dto);
        log.info("菜品评价列表是否为空：{}，数据：{}",
                dto.getDishEvaluations() == null,
                dto.getDishEvaluations());
        // 1. 校验订单合法性
        TOrder order = validateOrder(dto.getOrderId(), dto.getUserId());

        // 2. 获取订单项，用于匹配菜品评价
        List<TOrderItem> orderItems = orderItemMapper.listByOrderId(dto.getOrderId());
        log.info("查询到 {} 个订单项", orderItems != null ? orderItems.size() : 0);
        if (orderItems != null) {
            orderItems.forEach(item -> log.info("订单项: id={}, dishId={}, dishName={}",
                    item.getId(), item.getDishId(), item.getDishName()));
        }

        Map<Long, TOrderItem> itemMap = orderItems.stream()
                .collect(Collectors.toMap(TOrderItem::getId, item -> item));
        log.info("itemMap keys: {}", itemMap.keySet());

        // 3. 检查是否已有评价
        TEvaluation existEvaluation = evaluationMapper.selectByOrderId(dto.getOrderId());
        Long evaluationId;

        if (existEvaluation != null) {
            // 已有评价，使用现有评价ID
            evaluationId = existEvaluation.getId();
            log.info("订单已有评价，使用现有评价ID：{}", evaluationId);
        } else {
            // 4. 计算菜品评分平均值作为订单评分（核心逻辑）
            int avgScore = 5; // 默认5分
            if (dto.getDishEvaluations() != null && !dto.getDishEvaluations().isEmpty()) {
                double avg = dto.getDishEvaluations().stream()
                        .mapToInt(DishEvaluationDTO::getScore)
                        .average()
                        .orElse(5.0);
                avgScore = (int) Math.round(avg);
            }

            // 5. 构建订单评价对象
            TEvaluation evaluation = new TEvaluation();
            evaluation.setOrderId(dto.getOrderId());
            evaluation.setUserId(dto.getUserId());
            evaluation.setMerchantId(order.getMerchantId()); // 从订单获取商家ID，无需前端传
            evaluation.setScore(avgScore);
            evaluation.setContent(dto.getContent());

            // 6. 处理图片URL（数组转字符串）- 原有逻辑正确，保留
            if (dto.getImgUrls() != null && !dto.getImgUrls().isEmpty()) {
                String imgUrlsStr = String.join(",", dto.getImgUrls());
                evaluation.setImgUrls(imgUrlsStr);
            }

            // 7. 设置时间字段
            evaluation.setCreateTime(LocalDateTime.now());
            evaluation.setUpdateTime(LocalDateTime.now());

            // 8. 保存订单评价（生成ID）
            int evalResult = evaluationMapper.insert(evaluation);
            log.info("插入的订单评价ID: {}", evaluation.getId());
            if (evalResult <= 0) {
                throw new BusinessException("订单评价保存失败");
            }
            evaluationId = evaluation.getId(); // 获取自增的评价ID
        }

        // 9. 处理菜品评价
        List<TDishEvaluation> dishEvaluations = new ArrayList<>();
        if (dto.getDishEvaluations() != null && !dto.getDishEvaluations().isEmpty()) {
            log.info("开始处理 {} 条菜品评价", dto.getDishEvaluations().size());

            // 同时用 orderItemId 和 dishId 建立映射，增加匹配成功率
            Map<Long, TOrderItem> dishIdMap = orderItems.stream()
                    .collect(Collectors.toMap(TOrderItem::getDishId, item -> item, (a, b) -> a));

            for (DishEvaluationDTO dishDto : dto.getDishEvaluations()) {
                Long orderItemId = dishDto.getOrderItemId();
                Long dishId = dishDto.getDishId();
                log.info("处理菜品评价: orderItemId={}, dishId={}, score={}",
                        orderItemId, dishId, dishDto.getScore());

                // 优先用 orderItemId 匹配，匹配失败则用 dishId 匹配
                TOrderItem item = itemMap.get(orderItemId);
                if (item == null && dishId != null) {
                    item = dishIdMap.get(dishId);
                    log.info("通过dishId匹配: dishId={}, 找到={}", dishId, item != null);
                }

                if (item == null) {
                    log.warn("订单项不存在：orderItemId={}, dishId={}, 可用的orderItemIds={}, 可用的dishIds={}",
                            orderItemId, dishId, itemMap.keySet(), dishIdMap.keySet());
                    continue;
                }

                // 构建菜品评价对象
                TDishEvaluation dishEval = new TDishEvaluation();
                dishEval.setEvaluationId(String.valueOf(evaluationId)); // 关联订单评价ID
                dishEval.setOrderItemId(item.getId()); // 使用数据库中的订单项ID
                dishEval.setDishId(dishId);
                dishEval.setDishName(item.getDishName()); // 从订单项取菜品名，保证准确性
                dishEval.setUserId(dto.getUserId()); // 设置用户ID
                dishEval.setScore(dishDto.getScore().longValue());
                dishEval.setContent(dishDto.getContent());
                // 兼容Timestamp类型，保留原有赋值
                dishEval.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
                dishEval.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));

                log.info("菜品评价对象：dishId={}, dishName={}, score={}, evaluationId={}",
                        dishEval.getDishId(), dishEval.getDishName(), dishEval.getScore(), dishEval.getEvaluationId());

                dishEvaluations.add(dishEval);
            }
        }

        // 10. 批量保存菜品评价
        log.info("准备插入菜品评价，数量: {}", dishEvaluations.size());
        if (!dishEvaluations.isEmpty()) {
            try {
                int insertCount = dishEvaluationMapper.batchInsert(dishEvaluations);
                log.info("batchInsert返回值: {}, 插入了 {} 条菜品评价，关联评价ID：{}",
                        insertCount, dishEvaluations.size(), evaluationId);
            } catch (Exception e) {
                log.error("批量插入菜品评价失败", e);
                throw new BusinessException("菜品评价保存失败：" + e.getMessage());
            }
        } else {
            log.warn("没有菜品评价需要插入");
        }

        return true;
    }

    @Override
    public List<EvaluationDetailVO> getMerchantEvaluationDetail(Long merchantId) {
        List<EvaluationDetailVO> evaluationList = evaluationMapper.selectEvaluationDetailByMerchantId(merchantId);

        if (evaluationList == null || evaluationList.isEmpty()) {
            return new ArrayList<>();
        }

        // 遍历处理每个评价的图片URL和时间字段
        for (EvaluationDetailVO eval : evaluationList) {
            if (StringUtils.hasText(eval.getImgUrls())) {
                // 可选：如果VO新增了imgUrlList字段，可赋值
                // List<String> imgUrlList = Arrays.stream(eval.getImgUrls().split(","))
                //         .map(String::trim)
                //         .filter(url -> !url.isEmpty())
                //         .collect(Collectors.toList());
                // eval.setImgUrlList(imgUrlList);
            }

            if (eval.getOrderNo() == null && StringUtils.hasText(eval.getOrderId())) {
                // 兜底：从订单表查询orderNo（防止Mapper关联失败）
                TOrder order = orderMapper.selectById(eval.getOrderId());
                if (order != null) {
                    eval.setOrderNo(order.getOrderNo());
                }
            }

            // 关联菜品评价 - 将 String ID 转换为 Long 类型
            Long evaluationIdLong = null;
            try {
                evaluationIdLong = Long.valueOf(eval.getId());
            } catch (NumberFormatException e) {
                log.warn("【商家评价】评价ID格式错误: {}, 无法查询菜品评价", eval.getId());
            }
            
            List<TDishEvaluation> dishEvals = new ArrayList<>();
            if (evaluationIdLong != null) {
                dishEvals = dishEvaluationMapper.selectByEvaluationId(evaluationIdLong);
                log.info("【商家评价】评价ID: {}, 查询到菜品评价数量: {}", evaluationIdLong, dishEvals.size());
            }

            List<DishEvaluationVO> dishVOs = dishEvals.stream().map(dish -> {
                DishEvaluationVO vo = new DishEvaluationVO();
                // 手动赋值，解决Long→String、Timestamp→LocalDateTime转换问题
                vo.setId(Long.valueOf(String.valueOf(dish.getId())));          // Long转String
                vo.setDishId(Long.valueOf(String.valueOf(dish.getDishId())));  // Long转String
                vo.setDishName(dish.getDishName());              // 确保菜品名赋值
                vo.setScore(dish.getScore().intValue());         // Long转int
                vo.setContent(dish.getContent());                // 评价内容
                // 时间类型转换：Timestamp → LocalDateTime
                if (dish.getCreateTime() != null) {
                    vo.setCreateTime(dish.getCreateTime().toLocalDateTime());
                }
                return vo;
            }).collect(Collectors.toList());

            eval.setDishEvaluations(dishVOs);
        }

        return evaluationList;
    }

    @Override
    public List<EvaluationDetailVO> getUserEvaluations(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        List<TEvaluation> evaluations = evaluationMapper.listByUserId(userId);
        if (evaluations == null || evaluations.isEmpty()) {
            return new ArrayList<>();
        }

        List<EvaluationDetailVO> voList = new ArrayList<>();
        for (TEvaluation eval : evaluations) {
            EvaluationDetailVO vo = new EvaluationDetailVO();
            // 避免BeanUtils.copyProperties复制不兼容的时间类型，手动赋值核心字段
            vo.setId(String.valueOf(eval.getId()));
            vo.setOrderId(eval.getOrderId());
            vo.setOrderNo(eval.getOrderNo()); // 关键：传递订单号
            vo.setUserId(eval.getUserId());
            vo.setScore(eval.getScore());
            vo.setContent(eval.getContent());
            vo.setImgUrls(eval.getImgUrls()); // 保留逗号分隔字符串，前端解析
            vo.setMerchantReply(eval.getMerchantReply());
            vo.setReplyTime(eval.getReplyTime());
            vo.setCreateTime(eval.getCreateTime());
            vo.setUpdateTime(eval.getUpdateTime());

            // 关联菜品评价 - 将 String ID 转换为 Long 类型
            Long evaluationIdLong = null;
            try {
                evaluationIdLong = Long.valueOf(eval.getId());
            } catch (NumberFormatException e) {
                log.warn("【用户评价】评价ID格式错误: {}, 无法查询菜品评价", eval.getId());
            }
            
            List<TDishEvaluation> dishEvals = new ArrayList<>();
            if (evaluationIdLong != null) {
                dishEvals = dishEvaluationMapper.selectByEvaluationId(evaluationIdLong);
                log.info("【用户评价】评价ID: {}, 查询到菜品评价数量: {}", evaluationIdLong, dishEvals.size());
            }

            List<DishEvaluationVO> dishVOs = dishEvals.stream().map(dish -> {
                DishEvaluationVO dishVO = new DishEvaluationVO();
                dishVO.setId(Long.valueOf(String.valueOf(dish.getId())));         // Long转String
                dishVO.setDishId(Long.valueOf(String.valueOf(dish.getDishId()))); // Long转String
                dishVO.setDishName(dish.getDishName());             // 菜品名
                dishVO.setScore(dish.getScore().intValue());        // Long转int
                dishVO.setContent(dish.getContent());               // 评价内容
                if (dish.getCreateTime() != null) {
                    dishVO.setCreateTime(dish.getCreateTime().toLocalDateTime()); // 时间转换
                }
                return dishVO;
            }).collect(Collectors.toList());

            vo.setDishEvaluations(dishVOs);
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public boolean canEvaluate(String orderId, Long userId) {
        TOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            log.warn("订单不存在：orderId={}", orderId);
            return false;
        }
        log.info("canEvaluate检查 - orderId={}, orderUserId={}, requestUserId={}, orderStatus={}",
                orderId, order.getUserId(), userId, order.getOrderStatus());

        if (!order.getUserId().equals(userId)) {
            log.warn("用户不匹配：orderUserId={}, requestUserId={}", order.getUserId(), userId);
            return false;
        }
        // 订单状态3为已完成（根据你的业务调整）
        if (order.getOrderStatus() == null || order.getOrderStatus() != 3L) {
            log.warn("订单状态不是已完成：orderStatus={}", order.getOrderStatus());
            return false;
        }
        TEvaluation evaluation = evaluationMapper.selectByOrderId(orderId);
        if (evaluation != null) {
            log.warn("订单已评价：evaluationId={}", evaluation.getId());
            return false;
        }
        return true;
    }

    @Override
    public boolean canSubmitDishEvaluation(String orderId, Long userId) {
        TOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            log.warn("订单不存在：orderId={}", orderId);
            return false;
        }
        log.info("canSubmitDishEvaluation检查 - orderId={}, orderUserId={}, requestUserId={}, orderStatus={}",
                orderId, order.getUserId(), userId, order.getOrderStatus());

        if (!order.getUserId().equals(userId)) {
            log.warn("用户不匹配：orderUserId={}, requestUserId={}", order.getUserId(), userId);
            return false;
        }
        // 订单状态3为已完成
        if (order.getOrderStatus() == null || order.getOrderStatus() != 3L) {
            log.warn("订单状态不是已完成：orderStatus={}", order.getOrderStatus());
            return false;
        }
        // 不检查是否已评价，允许重复提交菜品评价
        return true;
    }

    @Override
    public List<DishEvaluationVO> getDishEvaluations(Long evaluationId) {
        if (evaluationId == null) {
            return new ArrayList<>();
        }
        return getDishEvaluations(String.valueOf(evaluationId));
    }

    private TOrder validateOrder(String orderId, Long userId) {
        TOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("不能评价他人订单");
        }
        if (order.getOrderStatus() == null || order.getOrderStatus() != 3L) {
            throw new BusinessException("只有已完成的订单才能评价");
        }
        return order;
    }

    @Override
    public List<DishEvaluationVO> getDishEvaluations(String evaluationId) {
        // 空值校验
        if (evaluationId == null || evaluationId.trim().isEmpty()) {
            log.warn("getDishEvaluations: evaluationId为空");
            return new ArrayList<>();
        }
        log.info("getDishEvaluations: 查询evaluationId={}", evaluationId);

        // 使用 Long 类型查询
        List<TDishEvaluation> dishEvals = dishEvaluationMapper.selectByEvaluationId(evaluationId);
        log.info("getDishEvaluations: 查询到 {} 条菜品评价", dishEvals != null ? dishEvals.size() : 0);

        if (dishEvals == null || dishEvals.isEmpty()) {
            return new ArrayList<>();
        }

        // 手动映射VO，解决所有类型转换问题
        return dishEvals.stream().map(dish -> {
            DishEvaluationVO vo = new DishEvaluationVO();
            vo.setId(Long.valueOf(String.valueOf(dish.getId())));         // Long → String
            vo.setDishId(Long.valueOf(String.valueOf(dish.getDishId()))); // Long → String
            vo.setDishName(dish.getDishName());             // 确保菜品名不为空
            vo.setScore(dish.getScore().intValue());        // Long → int
            vo.setContent(dish.getContent());               // 评价内容
            // 时间转换：Timestamp → LocalDateTime
            if (dish.getCreateTime() != null) {
                vo.setCreateTime(dish.getCreateTime().toLocalDateTime());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean validateEvaluationMerchant(Long evaluationId, Long merchantId) {
        int count = evaluationMapper.countByEvalIdAndMerchantId(evaluationId, merchantId);
        return count > 0;
    }

    @Override
    public EvaluationDetailVO getMerchantEvaluationDetailById(Long evaluationId, Long merchantId) {
        if (evaluationId == null || merchantId == null) {
            return null;
        }
        if (!validateEvaluationMerchant(evaluationId, merchantId)) {
            return null;
        }
        TEvaluation evaluation = evaluationMapper.selectById(evaluationId);
        if (evaluation == null) {
            return null;
        }
        EvaluationDetailVO vo = new EvaluationDetailVO();
        vo.setId(String.valueOf(evaluation.getId()));
        vo.setOrderId(evaluation.getOrderId());
        vo.setUserId(evaluation.getUserId());
        vo.setScore(evaluation.getScore());
        vo.setContent(evaluation.getContent());
        vo.setImgUrls(evaluation.getImgUrls());
        vo.setMerchantReply(evaluation.getMerchantReply());
        vo.setReplyTime(evaluation.getReplyTime());
        vo.setCreateTime(evaluation.getCreateTime());
        vo.setUpdateTime(evaluation.getUpdateTime());

        if (evaluation.getUserId() != null) {
            TUser user = userMapper.selectById(evaluation.getUserId());
            if (user != null) {
                vo.setUserName(user.getNickname());
                vo.setUserAvatar(user.getAvatar());
            }
        }
        if (StringUtils.hasText(evaluation.getOrderId())) {
            TOrder order = orderMapper.selectById(evaluation.getOrderId());
            if (order != null) {
                vo.setOrderNo(order.getOrderNo());
            }
        }
        // 使用 Long 类型查询
        List<TDishEvaluation> dishEvals = dishEvaluationMapper.selectByEvaluationId(evaluationId);
        List<DishEvaluationVO> dishVOs = dishEvals.stream().map(dish -> {
            DishEvaluationVO dishVO = new DishEvaluationVO();
            dishVO.setId(Long.valueOf(String.valueOf(dish.getId())));
            dishVO.setDishId(Long.valueOf(String.valueOf(dish.getDishId())));
            dishVO.setDishName(dish.getDishName());
            dishVO.setScore(dish.getScore().intValue());
            dishVO.setContent(dish.getContent());
            if (dish.getCreateTime() != null) {
                dishVO.setCreateTime(dish.getCreateTime().toLocalDateTime());
            }
            return dishVO;
        }).collect(Collectors.toList());
        vo.setDishEvaluations(dishVOs);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMerchantEvaluation(Long evaluationId, Long merchantId) {
        if (evaluationId == null || merchantId == null) {
            return false;
        }
        if (!validateEvaluationMerchant(evaluationId, merchantId)) {
            return false;
        }
        dishEvaluationMapper.deleteByEvaluationId(String.valueOf(evaluationId));
        return evaluationMapper.deleteById(evaluationId) > 0;
    }

    @Override
    public boolean updateMerchantEvaluation(TEvaluation evaluation, Long merchantId) {
        if (evaluation == null || evaluation.getId() == null || merchantId == null) {
            return false;
        }
        if (!validateEvaluationMerchant(evaluation.getId(), merchantId)) {
            return false;
        }
        TEvaluation patch = new TEvaluation();
        patch.setId(evaluation.getId());
        patch.setScore(evaluation.getScore());
        patch.setContent(evaluation.getContent());
        patch.setImgUrls(evaluation.getImgUrls());
        patch.setUpdateTime(LocalDateTime.now());
        return evaluationMapper.updateById(patch) > 0;
    }

    @Override
    public TEvaluation createMerchantEvaluation(TEvaluation evaluation, Long merchantId) {
        if (evaluation == null || merchantId == null) {
            return null;
        }
        if (!StringUtils.hasText(evaluation.getOrderId()) || evaluation.getUserId() == null) {
            return null;
        }
        evaluation.setMerchantId(merchantId);
        evaluation.setCreateTime(LocalDateTime.now());
        evaluation.setUpdateTime(LocalDateTime.now());
        evaluationMapper.insert(evaluation);
        return evaluation;
    }

    @Override
    public boolean deleteMerchantReply(Long evaluationId, Long merchantId) {
        if (evaluationId == null || merchantId == null) {
            return false;
        }
        if (!validateEvaluationMerchant(evaluationId, merchantId)) {
            return false;
        }
        return evaluationMapper.deleteReply(evaluationId, merchantId) > 0;
    }

@Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUserEvaluation(String orderId, Long userId) {
        // 1. 查询评价
        TEvaluation evaluation = evaluationMapper.selectByOrderId(orderId);
        if (evaluation == null) {
            throw new BusinessException("评价不存在");
        }

        // 2. 验证权限
        if (!evaluation.getUserId().equals(userId)) {
            throw new BusinessException("无权删除他人的评价");
        }

        // 3. 删除菜品评价
        dishEvaluationMapper.deleteByEvaluationId(String.valueOf(evaluation.getId()));

        // 4. 删除评价
        return evaluationMapper.deleteById(evaluation.getId()) > 0;
    }

    @Override
    public List<DishEvaluationVO> getDishReviewsByDishId(Long dishId) {
        if (dishId == null) {
            return new ArrayList<>();
        }

        // 查询该菜品的所有评价
        List<TDishEvaluation> dishEvals = dishEvaluationMapper.selectByDishId(dishId);
        if (dishEvals == null || dishEvals.isEmpty()) {
            return new ArrayList<>();
        }

        // 转换为VO并补充用户信息
        return dishEvals.stream().map(dish -> {
            DishEvaluationVO vo = new DishEvaluationVO();
            vo.setId(dish.getId());
            vo.setDishId(dish.getDishId());
            vo.setDishName(dish.getDishName());
            vo.setScore(dish.getScore().intValue());
            vo.setContent(dish.getContent());
            
            // 设置用户信息
            if (dish.getUserId() != null) {
                vo.setUserId(dish.getUserId());
                TUser user = userMapper.selectById(dish.getUserId());
                if (user != null) {
                    vo.setUserName(user.getNickname());
                    vo.setUserAvatar(user.getAvatar());
                }
            }
            
            // 时间转换
            if (dish.getCreateTime() != null) {
                vo.setCreateTime(dish.getCreateTime().toLocalDateTime());
            }
            
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitDishReview(Long dishId, Long userId, Integer score, String content) {
        // 1. 校验参数
        if (dishId == null || userId == null || score == null) {
            throw new BusinessException("参数不完整");
        }
        if (score < 1 || score > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }

        // 2. 校验菜品是否存在
        TDish dish = dishMapper.selectById(dishId);
        if (dish == null) {
            throw new BusinessException("菜品不存在");
        }

        // 3. 校验用户是否存在
        TUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 4. 创建菜品评价
        TDishEvaluation dishEval = new TDishEvaluation();
        dishEval.setDishId(dishId);
        dishEval.setDishName(dish.getDishName());
        dishEval.setUserId(userId);
        dishEval.setScore(score.longValue());
        dishEval.setContent(content);
        dishEval.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        dishEval.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
        // evaluationId 和 orderItemId 为空，表示直接评价菜品

        // 5. 保存
        int result = dishEvaluationMapper.insertSingle(dishEval);
        if (result <= 0) {
            throw new BusinessException("评价保存失败");
        }

        log.info("用户{}成功评价菜品{}，评分：{}", userId, dishId, score);
        return true;
    }

    @Override
    public List<DishEvaluationVO> getAllDishReviewsByMerchantId(Long merchantId) {
        if (merchantId == null) {
            return new ArrayList<>();
        }

        log.info("getAllDishReviewsByMerchantId: 查询商家{}的菜品评价", merchantId);

        // 获取该商家所有菜品的ID
        List<TDish> dishes = dishMapper.selectDishListByMerchantId(merchantId);
        log.info("getAllDishReviewsByMerchantId: 商家{}有{}个菜品", merchantId, dishes != null ? dishes.size() : 0);

        if (dishes == null || dishes.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> dishIds = dishes.stream()
                .map(TDish::getId)
                .collect(Collectors.toList());
        log.info("getAllDishReviewsByMerchantId: 菜品IDs={}", dishIds);

        // 查询所有菜品的评价
        List<DishEvaluationVO> allReviews = new ArrayList<>();
        for (Long dishId : dishIds) {
            List<DishEvaluationVO> reviews = getDishReviewsByDishId(dishId);
            log.info("getAllDishReviewsByMerchantId: 菜品{}有{}条评价", dishId, reviews.size());
            allReviews.addAll(reviews);
        }

        log.info("getAllDishReviewsByMerchantId: 总共{}条菜品评价", allReviews.size());

        // 按时间倒序排列
        allReviews.sort((a, b) -> {
            if (a.getCreateTime() == null) return 1;
            if (b.getCreateTime() == null) return -1;
            return b.getCreateTime().compareTo(a.getCreateTime());
        });

        return allReviews;
    }

    @Override
    public List<DishEvaluationVO> getDishEvaluationsByOrderId(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            log.warn("getDishEvaluationsByOrderId: orderId为空");
            return new ArrayList<>();
        }

        log.info("getDishEvaluationsByOrderId: 查询订单{}的菜品评价", orderId);

        List<TDishEvaluation> dishEvals = dishEvaluationMapper.selectByOrderId(orderId);
        log.info("getDishEvaluationsByOrderId: 查询到{}条菜品评价", dishEvals != null ? dishEvals.size() : 0);

        if (dishEvals == null || dishEvals.isEmpty()) {
            log.info("getDishEvaluationsByOrderId: 无菜品评价数据");
            return new ArrayList<>();
        }

        List<DishEvaluationVO> result = dishEvals.stream().map(dish -> {
            DishEvaluationVO vo = new DishEvaluationVO();
            vo.setId(dish.getId());
            vo.setDishId(dish.getDishId());
            vo.setDishName(dish.getDishName());
            vo.setScore(dish.getScore().intValue());
            vo.setContent(dish.getContent());
            vo.setOrderItemId(dish.getOrderItemId());
            if (dish.getCreateTime() != null) {
                vo.setCreateTime(dish.getCreateTime().toLocalDateTime());
            }
            log.info("getDishEvaluationsByOrderId: 菜品评价 - dishId={}, dishName={}, score={}, orderItemId={}",
                    dish.getDishId(), dish.getDishName(), dish.getScore(), dish.getOrderItemId());
            return vo;
        }).collect(Collectors.toList());

        return result;
    }
}
