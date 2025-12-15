package com.tq.module.address.controller;

import com.tq.common.api.Result;
import com.tq.module.address.dto.AddressCreateRequest;
import com.tq.module.address.dto.AddressUpdateRequest;
import com.tq.module.address.dto.AddressVO;
import com.tq.module.address.service.AddressService;
import com.tq.security.context.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public Result<List<AddressVO>> list() {
        Long userId = UserContext.getUserId();
        return Result.ok(addressService.list(userId));
    }

    @PostMapping
    public Result<Map<String, String>> create(@RequestBody @Valid AddressCreateRequest req) {
        Long userId = UserContext.getUserId();
        String id = addressService.create(userId, req);
        return Result.ok(Map.of("id", id));
    }

    @PutMapping("/{addressId}")
    public Result<Void> update(@PathVariable Long addressId, @RequestBody @Valid AddressUpdateRequest req) {
        Long userId = UserContext.getUserId();
        addressService.update(userId, addressId, req);
        return Result.ok();
    }

    @DeleteMapping("/{addressId}")
    public Result<Void> delete(@PathVariable Long addressId) {
        Long userId = UserContext.getUserId();
        addressService.delete(userId, addressId);
        return Result.ok();
    }

    @PutMapping("/{addressId}/default")
    public Result<Void> setDefault(@PathVariable Long addressId) {
        Long userId = UserContext.getUserId();
        addressService.setDefault(userId, addressId);
        return Result.ok();
    }
}
