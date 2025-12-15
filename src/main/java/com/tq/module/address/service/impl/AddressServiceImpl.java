package com.tq.module.address.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tq.common.exception.NotFoundException;
import com.tq.module.address.dto.AddressCreateRequest;
import com.tq.module.address.dto.AddressUpdateRequest;
import com.tq.module.address.dto.AddressVO;
import com.tq.module.address.entity.Address;
import com.tq.module.address.mapper.AddressMapper;
import com.tq.module.address.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Override
    @Transactional
    public String create(Long userId, AddressCreateRequest req) {
        Integer isDefault = req.getIsDefault();
        if (isDefault == null) {
            isDefault = 0;
        }

        // 若用户还没有任何地址，强制设为默认（更符合实际使用）
        long count = addressMapper.selectCount(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId));

        if (count == 0) {
            isDefault = 1;
        }

        if (isDefault == 1) {
            addressMapper.clearDefault(userId);
        }

        Address a = new Address();
        a.setUserId(userId);
        a.setReceiverName(req.getReceiverName());
        a.setReceiverPhone(req.getReceiverPhone());
        a.setProvince(req.getProvince());
        a.setCity(req.getCity());
        a.setDistrict(req.getDistrict());
        a.setDetailAddress(req.getDetailAddress());
        a.setIsDefault(isDefault);
        a.setDeleted(0);
        a.setCreateTime(LocalDateTime.now());
        a.setUpdateTime(LocalDateTime.now());

        addressMapper.insert(a);
        return String.valueOf(a.getId());
    }

    @Override
    @Transactional
    public void update(Long userId, Long addressId, AddressUpdateRequest req) {
        Address a = addressMapper.selectOne(new LambdaQueryWrapper<Address>()
                .eq(Address::getId, addressId)
                .eq(Address::getUserId, userId));
        if (a == null) {
            throw new NotFoundException("地址不存在");
        }

        Integer isDefault = req.getIsDefault();
        if (isDefault != null && isDefault == 1) {
            addressMapper.clearDefault(userId);
        }

        a.setReceiverName(req.getReceiverName());
        a.setReceiverPhone(req.getReceiverPhone());
        a.setProvince(req.getProvince());
        a.setCity(req.getCity());
        a.setDistrict(req.getDistrict());
        a.setDetailAddress(req.getDetailAddress());
        if (isDefault != null) {
            a.setIsDefault(isDefault);
        }
        a.setUpdateTime(LocalDateTime.now());

        addressMapper.updateById(a);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long addressId) {
        Address a = addressMapper.selectOne(new LambdaQueryWrapper<Address>()
                .eq(Address::getId, addressId)
                .eq(Address::getUserId, userId));
        if (a == null) {
            throw new NotFoundException("地址不存在");
        }
        addressMapper.deleteById(addressId);
    }

    @Override
    @Transactional
    public void setDefault(Long userId, Long addressId) {
        Address a = addressMapper.selectOne(new LambdaQueryWrapper<Address>()
                .eq(Address::getId, addressId)
                .eq(Address::getUserId, userId));
        if (a == null) {
            throw new NotFoundException("地址不存在");
        }
        addressMapper.clearDefault(userId);
        int rows = addressMapper.setDefault(userId, addressId);
        if (rows <= 0) {
            throw new NotFoundException("地址不存在");
        }
    }

    @Override
    public List<AddressVO> list(Long userId) {
        List<Address> list = addressMapper.selectList(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getUpdateTime));

        return list.stream().map(this::toVO).toList();
    }

    @Override
    public Address getByIdForOrder(Long userId, Long addressId) {
        Address a = addressMapper.selectOne(new LambdaQueryWrapper<Address>()
                .eq(Address::getId, addressId)
                .eq(Address::getUserId, userId));
        if (a == null) {
            throw new NotFoundException("地址不存在");
        }
        return a;
    }

    private AddressVO toVO(Address a) {
        AddressVO vo = new AddressVO();
        vo.setId(String.valueOf(a.getId()));
        vo.setReceiverName(a.getReceiverName());
        vo.setReceiverPhone(a.getReceiverPhone());
        vo.setProvince(a.getProvince());
        vo.setCity(a.getCity());
        vo.setDistrict(a.getDistrict());
        vo.setDetailAddress(a.getDetailAddress());
        vo.setIsDefault(a.getIsDefault());
        return vo;
    }
}
