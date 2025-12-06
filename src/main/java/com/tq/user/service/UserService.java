package com.tq.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tq.user.entity.User;

public interface UserService extends IService<User> {

    User getByIdOrThrow(Long userId);
}
