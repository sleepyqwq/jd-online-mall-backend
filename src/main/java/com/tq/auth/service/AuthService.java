package com.tq.auth.service;

import com.tq.auth.dto.UserLoginRequest;
import com.tq.auth.dto.UserLoginResponse;
import com.tq.auth.dto.UserRegisterRequest;
import com.tq.user.entity.User;

public interface AuthService {

    void register(UserRegisterRequest request);

    UserLoginResponse login(UserLoginRequest request);

}
