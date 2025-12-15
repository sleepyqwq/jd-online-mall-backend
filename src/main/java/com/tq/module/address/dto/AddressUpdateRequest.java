package com.tq.module.address.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressUpdateRequest {

    @NotBlank(message = "receiverName不能为空")
    private String receiverName;

    @NotBlank(message = "receiverPhone不能为空")
    private String receiverPhone;

    @NotBlank(message = "province不能为空")
    private String province;

    @NotBlank(message = "city不能为空")
    private String city;

    @NotBlank(message = "district不能为空")
    private String district;

    @NotBlank(message = "detailAddress不能为空")
    private String detailAddress;

    private Integer isDefault; // 0/1，可选
}
