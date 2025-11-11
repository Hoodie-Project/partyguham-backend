package com.partyguham.auth.controller;

import com.partyguham.common.annotation.ApiV2Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ApiV2Controller
@RequestMapping("/oauth/app")
public class AppOAuthController {

    //    private final OauthService

    @PostMapping("/kakao/login") // 카카오 로그인
    public void kakaoLogin(){}

    @PostMapping("/google/login") // 구글 로그인
    public void googleLogin(){}

    @PostMapping("/kakao/link") // 카카오 계정 linkToken 발급
    public void kakaoLink(){}

    @PostMapping("/google/link") // 구글 계정 linkToken 발급
    public void googleLink(){}

    @PostMapping("/link") // 계정 연결
    public void link(){}

    @PostMapping("recover") // 계정 복구
    public void recover(){}
}
