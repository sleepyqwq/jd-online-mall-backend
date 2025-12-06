package com.tq.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tq.common.exception.BizException;
import com.tq.common.exception.ErrorCode;
import com.tq.user.entity.User;
import com.tq.user.mapper.UserMapper;
import com.tq.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getByIdOrThrow(Long userId) {
        if (userId == null) {
            throw new BizException(ErrorCode.AUTH_ERROR, "请先登录");
        }

        User user = this.getOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getId, userId)
                        .eq(User::getDeleted, 0)
        );

        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BizException(ErrorCode.FORBIDDEN, "账号已被禁用");
        }

        return user;
    }
}
