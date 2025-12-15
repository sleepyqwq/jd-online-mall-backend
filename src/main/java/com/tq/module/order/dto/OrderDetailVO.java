package com.tq.module.order.dto;

import com.tq.module.order.entity.AddressSnapshot;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVO {

    private String orderId;
    private String orderNo;
    private String status;
    private BigDecimal totalAmount;

    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime cancelTime;
    private String cancelReason;
    private LocalDateTime expireTime;

    private AddressSnapshot addressSnapshot; // 管理端详情会用到
    private List<ItemVO> items;

    @Data
    public static class ItemVO {
        private String productId;
        private String productTitle;
        private String productImage;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotalAmount;
    }
}
