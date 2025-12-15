package com.tq.module.order.entity;

import lombok.Data;

@Data
public class AddressSnapshot {
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
}
