package com.tq.auth.dto;

import lombok.Data;

@Data
public class UserLoginResponse {

    private String token;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
}
