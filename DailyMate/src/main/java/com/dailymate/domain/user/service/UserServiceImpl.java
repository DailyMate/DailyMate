package com.dailymate.domain.user.service;

import com.dailymate.domain.user.dao.UserRepository;
import com.dailymate.domain.user.dto.LogInReqDto;
import com.dailymate.domain.user.dto.LogInResDto;
import com.dailymate.domain.user.dto.SignUpReqDto;
import com.dailymate.domain.user.exception.UserBadRequestException;
import com.dailymate.domain.user.exception.UserExceptionMessage;
import com.dailymate.global.common.jwt.JwtTokenDto;
import com.dailymate.global.common.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void signUp(SignUpReqDto reqDto) {
        String email = reqDto.getEmail();
        log.info("[회원가입] 회원가입 요청 email : {}", email);

        // 이메일 중복 검사 -> 메서드 따로 뺼거임
        // 닉네임 중복 확인 -> 메서드 따로 뺄거임

        // 회원가입 정보 유효성 체크(다 입력했는가)
        if(!checkSignupInfo(reqDto)) {
            log.error("[회원가입] 회원가입 정보 유효성 검사 FALSE");
            throw new UserBadRequestException(UserExceptionMessage.SIGN_UP_BAD_REQUEST.getMsg());
        }

        // 비밀번호 정규화 체크(프론트에서 처리하지만, 만약을 대비해서 에러를 날리기로 합의함)
        if(!checkPasswordRegex(reqDto.getPassword())) {
            log.error("[회원가입] 비밀번호는 8~16자 이내 영문, 숫자, 특수문자를 포함해야합니다.");
            throw new UserBadRequestException(UserExceptionMessage.PASSWORD_NOT_MATCH_REGEX.getMsg());
        }

        // 비밀번호 암호화
        reqDto.setPassword(passwordEncoder.encode(reqDto.getPassword()));

        // 엔티티 저장
        userRepository.save(reqDto.dtoToEntity());
        log.info("[회원가입] 회원가입이 완료되었습니다 !!!");
    }

    /**
     * 회원가입 전 이메일 중복 검사
     * 중복 O - true / 중복 X - false
     */
    @Override
    public Boolean checkEmail(String email) {
        log.info("[이메일 중복 검사] email : {}", email);
        return userRepository.existsByEmail(email);
    }

    /**
     * 회원가입 전 닉네임 중복 검사
     * 중복 O - true / 중복 X - false
     */
    @Override
    public Boolean checkNickname(String nickname) {
        log.info("[닉네임 중복 검사] nickname : {}", nickname);
        return userRepository.existsByNickname(nickname);
    }

    @Override
    public JwtTokenDto logIn(LogInReqDto reqDto) {
        String email = reqDto.getEmail();
        String password = reqDto.getPassword();
        log.info("[로그인] 로그인 요청 email : {}", email);

        // 1. email + password 기반으로 Authentication 객체 생성
        // 이때 authentication은 인증 여부를 확인하는 authenticated값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        log.info("authenticationToken : {}", authenticationToken);

        // 2. 실제 검증
        // authenticate() 메서드를 통해 요청된 Users에 대한 검증 진행
        // authenticate 메서드가 실행될 때 UserDetailsServiceImpl에서 만든 loadUserByUserName메서드 실행
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtTokenDto jwtToken = jwtTokenProvider.generateToken(authentication);

        return jwtToken;
    }


    /**
     * 회원가입 정보 유효성 검사
     */
    private boolean checkSignupInfo(SignUpReqDto reqDto) {
        // StringUtils.hasText() : 값이 있을 경우 true, 공백이나 NULL일 경우 false
        if(!StringUtils.hasText(reqDto.getEmail()) ||
                !StringUtils.hasText(reqDto.getPassword()) ||
                !StringUtils.hasText(reqDto.getNickname()))
            return false;

        return true;
    }

    /**
     * 비밀번호 정규식 검사
     * 8 ~ 16자 이내 영문, 숫자, 특수문자 포함(대소문자 구분 X)
     * 포함 : true / 불포함 : false
     */
    private boolean checkPasswordRegex(String password) {
        Pattern regexPattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,16}$");
        Matcher matcher = regexPattern.matcher(password);

        return matcher.find();
    }

}