package com.tq.module.order.controller;

import com.tq.common.api.PageResult;
import com.tq.common.api.Result;
import com.tq.common.util.UrlUtil;
import com.tq.module.order.dto.OrderCreateRequest;
import com.tq.module.order.dto.OrderDetailVO;
import com.tq.module.order.dto.OrderListItemVO;
import com.tq.module.order.service.OrderService;
import com.tq.security.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<Map<String, String>> create(@RequestBody @Valid OrderCreateRequest req) {
        Long userId = UserContext.getUserId();
        String orderId = orderService.create(userId, req);
        return Result.ok(Map.of("orderId", orderId));
    }

    @GetMapping
    public Result<PageResult<OrderListItemVO>> page(@RequestParam(required = false) String status,
                                                    @RequestParam int pageNum,
                                                    @RequestParam int pageSize) {
        Long userId = UserContext.getUserId();
        return Result.ok(orderService.page(userId, status, pageNum, pageSize));
    }

    @GetMapping("/{orderId}")
    public Result<OrderDetailVO> detail(@PathVariable Long orderId, HttpServletRequest req) {
        Long userId = UserContext.getUserId();
        String baseUrl = UrlUtil.buildBaseUrl(req);
        return Result.ok(orderService.detail(userId, orderId, baseUrl));
    }

    @PutMapping("/{orderId}/cancel")
    public Result<Void> cancel(@PathVariable Long orderId, @RequestBody(required = false) java.util.Map<String, String> body) {
        Long userId = UserContext.getUserId();
        // 前端传参格式为 { reason: "..." }
        String reason = (body != null && body.containsKey("reason")) ? body.get("reason") : "用户主动取消";
        orderService.cancel(userId, orderId, reason);
        return Result.ok();
    }

    @PostMapping("/{orderId}/pay")
    public Result<Void> pay(@PathVariable Long orderId) {
        Long userId = UserContext.getUserId();
        orderService.pay(userId, orderId);
        return Result.ok();
    }

}
