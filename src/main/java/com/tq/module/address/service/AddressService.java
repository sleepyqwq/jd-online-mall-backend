package com.tq.module.address.service;

import com.tq.module.address.dto.AddressCreateRequest;
import com.tq.module.address.dto.AddressUpdateRequest;
import com.tq.module.address.dto.AddressVO;
import com.tq.module.address.entity.Address;

import java.util.List;

public interface AddressService {

    String create(Long userId, AddressCreateRequest req);

    void update(Long userId, Long addressId, AddressUpdateRequest req);

    void delete(Long userId, Long addressId);

    void setDefault(Long userId, Long addressId);

    List<AddressVO> list(Long userId);

    Address getByIdForOrder(Long userId, Long addressId);
}
