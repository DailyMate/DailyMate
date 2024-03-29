//package com.dailymate.domain.user.api;
//
//import com.dailymate.global.common.oauth.dto.GoogleLoginResponse;
//import com.dailymate.global.common.oauth.dto.GoogleOAuthRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.net.URI;
//
/**
 * 구글 로그인의 정석대로
 * code를 구글과 주고받아 유저정보를 추출하는 컨트롤러입니다.
 *
 * 하지만 이걸 사용하지 않고
 * Spring Security OAuth2의 기본 동작인 loadUser와 successHandler를 사용하여 유저정보를 추출할 것이라 주석처리 하였습니다.
 */
//@Slf4j
//@RestController
//@RequestMapping("/oauth")
//public class GoogleOAuthController {
//
//    @Value("${spring.security.oauth2.client.registration.google.client-id}")
//    private String googleClientId;
//
//    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
//    private String googleClientSecret;
//
////    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
//    private String googleRedirectUrl = "http://localhost:8080/oauth/google/callback";
//
//    private String googleAuthUrl = "https://oauth2.googleapis.com";
//    private String googleLoginUrl = "https://accounts.google.com";
//
//    // 구글 로그인창 호출
//    @GetMapping("/login/google")
//    public ResponseEntity<?> googleLogin(HttpServletRequest request) throws Exception   {
//        String reqUrl = googleLoginUrl + "/o/oauth2/v2/auth?client_id=" + googleClientId + "&redirect_uri=" + googleRedirectUrl
//                + "&response_type=code&scope=email%20profile%20openid&access_type=offline";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setLocation(URI.create(reqUrl));
//
//        // 1. requestUrl 구글로그인 창을 띄우고, 로그인 후 /oauth/google/callback 으로 리다이렉션하게 된다.
//        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
//    }
//
//    // 구글에서 리다이렉션
//    @GetMapping("/google/callback")
//    public String googleRedirect(HttpServletRequest request, @RequestParam(value = "code") String authCode, HttpServletResponse response) throws Exception {
//        // 2. 구글에 등록된 데일리메이트 설정 정보를 보내어 약속된 토큰을 받기 위한 객체 생성
//        GoogleOAuthRequest googleOAuthRequest = GoogleOAuthRequest.builder()
//                .clientId(googleClientId)
//                .clientSecret(googleClientSecret)
//                .code(authCode)
//                .redirectUri(googleRedirectUrl)
//                .grantType("authorization_code")
//                .build();
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        // 3. 토큰 요청을 한다
//        ResponseEntity<GoogleLoginResponse> apiResponse = restTemplate.postForEntity(googleAuthUrl + "/token", googleOAuthRequest, GoogleLoginResponse.class);
//
//        // 4. 받은 토큰을 토큰 객체에 저장
//        GoogleLoginResponse googleLoginResponse = apiResponse.getBody();
//
//        log.info("[구글꼰뜨롤러 - 리다이렉션 메서드] responseBody : {}", googleLoginResponse.toString());
//
//        String googleToken = googleLoginResponse.getId_token();
//
//        // 5. 받은 토큰을 구글에 보내 유저 정보를 얻는다.
//        String requestUrl = UriComponentsBuilder.fromHttpUrl(googleAuthUrl + "/tokeninfo").queryParam("id_token", googleToken).toUriString();
//
//        // 6. 허가된 토큰의 유저정보를 결과로 받는다.
//        String resultJson = restTemplate.getForObject(requestUrl, String.class);
//
//        return resultJson;
//    }
//
//}
