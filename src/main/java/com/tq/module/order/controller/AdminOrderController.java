package com.tq.module.order.controller;

import com.tq.common.api.PageResult;
import com.tq.common.api.Result;
import com.tq.common.util.UrlUtil;
import com.tq.module.order.dto.AdminOrderListItemVO;
import com.tq.module.order.dto.OrderDetailVO;
import com.tq.module.order.dto.OrderStatusUpdateRequest;
import com.tq.module.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public Result<PageResult<AdminOrderListItemVO>> page(@RequestParam(required = false) String orderNo,
                                                         @RequestParam(required = false) String status,
                                                         @RequestParam(required = false) Long userId,
                                                         @RequestParam int pageNum,
                                                         @RequestParam int pageSize) {
        return Result.ok(orderService.adminPage(orderNo, status, userId, pageNum, pageSize));
    }

    @GetMapping("/{orderId}")
    public Result<OrderDetailVO> detail(@PathVariable Long orderId, HttpServletRequest req) {
        String baseUrl = UrlUtil.buildBaseUrl(req);
        return Result.ok(orderService.adminDetail(orderId, baseUrl));
    }

    @PutMapping("/{orderId}/status")
    public Result<Void> updateStatus(@PathVariable Long orderId, @RequestBody @Valid OrderStatusUpdateRequest req) {
        orderService.adminUpdateStatus(orderId, req);
        return Result.ok();
    }
}
