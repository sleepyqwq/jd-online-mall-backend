package com.tq.schedule;

import com.tq.module.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutJob {

    private final OrderService orderService;

    @Scheduled(fixedDelay = 10_000) // 每 10 秒扫描一次（演示期足够）
    public void run() {
        try {
            orderService.cancelExpiredOrders();
        } catch (Exception e) {
            log.error("OrderTimeoutJob failed", e);
        }
    }
}
