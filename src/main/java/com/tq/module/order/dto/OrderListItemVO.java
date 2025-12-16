package com.tq.module.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderListItemVO {
    private String orderId;
    private String orderNo;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createTime;
    private LocalDateTime payTime;

    private List<OrderDetailVO.ItemVO> items;
}
