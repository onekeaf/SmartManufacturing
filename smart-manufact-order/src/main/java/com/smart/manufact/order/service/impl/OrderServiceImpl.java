package com.smart.manufact.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.manufact.common.enums.OrderPriority;
import com.smart.manufact.common.enums.OrderStatus;
import com.smart.manufact.common.exception.BusinessException;
import com.smart.manufact.order.config.ProductionFeignClient;
import com.smart.manufact.order.dto.OrderDTO;
import com.smart.manufact.order.entity.Order;
import com.smart.manufact.order.mapper.OrderMapper;
import com.smart.manufact.order.service.OrderService;
import com.smart.manufact.order.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderMapper orderMapper;
    private final ProductionFeignClient productionFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        BeanUtil.copyProperties(orderDTO, order);
        
        order.setOrderNo(generateOrderNo());
        order.setStatus(OrderStatus.PENDING.getCode());
        order.setCreateTime(LocalDateTime.now());
        
        orderMapper.insert(order);
        
        log.info("订单创建成功，订单号：{}", order.getOrderNo());
        
        return convertToVO(order);
    }

    @Override
    public OrderVO getOrderById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return convertToVO(order);
    }

    @Override
    public Page<OrderVO> getOrderList(Integer page, Integer size, Integer status) {
        Page<Order> orderPage = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        
        wrapper.orderByDesc(Order::getPriority)
               .orderByDesc(Order::getCreateTime);
        
        Page<Order> result = orderMapper.selectPage(orderPage, wrapper);
        
        Page<OrderVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<OrderVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO updateOrderStatus(Long id, Integer status) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        log.info("订单状态更新成功，订单号：{}，状态：{}", order.getOrderNo(), status);
        
        return convertToVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (order.getStatus().equals(OrderStatus.PRODUCING.getCode())) {
            throw new BusinessException("订单生产中，无法取消");
        }
        
        order.setStatus(OrderStatus.CANCELLED.getCode());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        
        log.info("订单取消成功，订单号：{}", order.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO triggerProduction(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!order.getStatus().equals(OrderStatus.CONFIRMED.getCode())) {
            throw new BusinessException("订单状态不正确，无法触发生产");
        }
        
        Map<String, Object> productionRequest = new HashMap<>();
        productionRequest.put("orderId", order.getId());
        productionRequest.put("orderNo", order.getOrderNo());
        productionRequest.put("vehicleModel", order.getVehicleModel());
        productionRequest.put("quantity", order.getQuantity());
        productionRequest.put("priority", order.getPriority());
        productionRequest.put("deliveryDate", order.getDeliveryDate());
        
        try {
            Map<String, Object> resultWrapper = productionFeignClient.createProductionPlan(productionRequest);
            
            // 检查Feign调用是否成功
            if (resultWrapper == null) {
                throw new BusinessException("调用生产计划服务失败：服务无响应");
            }
            
            // 从Result对象中提取实际的数据
            Map<String, Object> result = (Map<String, Object>) resultWrapper.get("data");
            if (result == null) {
                String errorMessage = "调用生产计划服务失败：返回数据为空";
                if (resultWrapper.containsKey("message")) {
                    errorMessage = (String) resultWrapper.get("message");
                }
                throw new BusinessException(errorMessage);
            }
            
            String planId = (String) result.get("planId");
            
            order.setProductionPlanId(planId);
            order.setStatus(OrderStatus.PRODUCING.getCode());
            order.setUpdateTime(LocalDateTime.now());
            orderMapper.updateById(order);
            
            log.info("订单触发生产成功，订单号：{}，生产计划ID：{}", order.getOrderNo(), planId);
            
        } catch (Exception e) {
            log.error("调用生产计划服务失败", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("触发生产失败：" + e.getMessage());
        }
        
        return convertToVO(order);
    }

    private String generateOrderNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = IdUtil.fastSimpleUUID().substring(0, 8).toUpperCase();
        return "ORD" + date + uuid;
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtil.copyProperties(order, vo);
        
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode().equals(order.getStatus())) {
                vo.setStatusDesc(status.getDesc());
                break;
            }
        }
        
        for (OrderPriority priority : OrderPriority.values()) {
            if (priority.getCode().equals(order.getPriority())) {
                vo.setPriorityDesc(priority.getDesc());
                break;
            }
        }
        
        return vo;
    }
}
