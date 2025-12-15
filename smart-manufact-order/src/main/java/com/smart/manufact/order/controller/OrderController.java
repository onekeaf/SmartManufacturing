package com.smart.manufact.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.manufact.common.result.Result;
import com.smart.manufact.order.dto.OrderDTO;
import com.smart.manufact.order.service.OrderService;
import com.smart.manufact.order.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;

    @PostMapping("/create")
    public Result<OrderVO> createOrder(@RequestBody OrderDTO orderDTO) {
        log.info("创建订单请求：{}", orderDTO);
        OrderVO orderVO = orderService.createOrder(orderDTO);
        return Result.success("订单创建成功", orderVO);
    }

    @GetMapping("/{id}")
    public Result<OrderVO> getOrderById(@PathVariable Long id) {
        OrderVO orderVO = orderService.getOrderById(id);
        return Result.success(orderVO);
    }

    @GetMapping("/list")
    public Result<Page<OrderVO>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Page<OrderVO> orderPage = orderService.getOrderList(page, size, status);
        return Result.success(orderPage);
    }

    @PutMapping("/{id}/status")
    public Result<OrderVO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        OrderVO orderVO = orderService.updateOrderStatus(id, status);
        return Result.success("订单状态更新成功", orderVO);
    }

    @DeleteMapping("/{id}")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.success();
    }

    @PostMapping("/{id}/trigger-production")
    public Result<OrderVO> triggerProduction(@PathVariable Long id) {
        log.info("触发生产请求，订单ID：{}", id);
        OrderVO orderVO = orderService.triggerProduction(id);
        return Result.success("生产计划已创建", orderVO);
    }
}
