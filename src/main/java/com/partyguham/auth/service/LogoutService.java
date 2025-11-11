package com.partyguham.auth.service;

import org.springframework.stereotype.Service;

@Service
public class LogoutService {
    // TODO: Redis/DB 로 대체
    public void revokeRefresh(String refreshToken){ /* 저장소에서 삭제/무효화 */ }
    public void blacklistAccess(String accessToken){ /* 선택: jti 블랙리스트 */ }
}