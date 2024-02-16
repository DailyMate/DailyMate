package com.dailymate.domain.user.service;

import com.dailymate.domain.user.dto.LogInReqDto;
import com.dailymate.domain.user.dto.LogInResDto;
import com.dailymate.domain.user.dto.SignUpReqDto;
import com.dailymate.global.common.jwt.JwtTokenDto;

public interface UserService {

    void signUp(SignUpReqDto reqDto);

    Boolean checkEmail(String email);

    Boolean checkNickname(String nickname);

//    LogInResDto logIn(LogInReqDto reqDto);
    JwtTokenDto logIn(LogInReqDto reqDto);
}