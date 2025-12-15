package com.tq.module.address.dto;

import lombok.Data;

@Data
public class AddressVO {
    private String id;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Integer isDefault; // 0/1
}
