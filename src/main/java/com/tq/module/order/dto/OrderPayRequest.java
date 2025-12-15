package com.tq.module.order.dto;

import lombok.Data;

/**
 * 预留：当前为模拟支付，接口可不传 body
 */
@Data
public class OrderPayRequest {
    private String payChannel;
}
