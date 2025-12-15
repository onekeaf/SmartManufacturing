package com.smart.manufact.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.manufact.order.dto.OrderDTO;
import com.smart.manufact.order.vo.OrderVO;

public interface OrderService {
    
    OrderVO createOrder(OrderDTO orderDTO);
    
    OrderVO getOrderById(Long id);
    
    Page<OrderVO> getOrderList(Integer page, Integer size, Integer status);
    
    OrderVO updateOrderStatus(Long id, Integer status);
    
    void cancelOrder(Long id);
    
    OrderVO triggerProduction(Long id);
}
