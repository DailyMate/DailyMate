package com.dailymate.domain.user.oauth.noUse.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleOAuthRequest {

    private String redirectUri;
    private String clientId;
    private String clientSecret;
    private String code;
    private String scope;
    private String grantType;

//    private String responseType;
//    private String accessType;
//    private String state;
//    private String includeGrantedScopes;
//    private String loginHint;
//    private String prompt;

}
