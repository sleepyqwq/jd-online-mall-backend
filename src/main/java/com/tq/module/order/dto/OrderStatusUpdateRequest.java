package com.tq.module.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {

    @NotBlank(message = "status不能为空")
    private String status; // SHIPPED / COMPLETED 等

    private String remark;
}
