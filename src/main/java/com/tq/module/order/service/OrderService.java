package com.tq.module.order.service;

import com.tq.common.api.PageResult;
import com.tq.module.order.dto.*;

public interface OrderService {

    String create(Long userId, OrderCreateRequest req);

    PageResult<OrderListItemVO> page(Long userId, String status, int pageNum, int pageSize);

    OrderDetailVO detail(Long userId, Long orderId, String baseUrl);

    void cancel(Long userId, Long orderId);

    void pay(Long userId, Long orderId);

    PageResult<AdminOrderListItemVO> adminPage(String orderNo, String status, Long userId, int pageNum, int pageSize);

    OrderDetailVO adminDetail(Long orderId, String baseUrl);

    void adminUpdateStatus(Long orderId, OrderStatusUpdateRequest req);

    void cancelExpiredOrders(); // 定时任务调用
}
