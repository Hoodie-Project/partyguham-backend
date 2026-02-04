package com.partyguham.domain.party.exception;

import com.partyguham.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PartyUserErrorCode implements ErrorCode {

    // 400 영역: 비즈니스 로직 위반
    PARTY_LEAVE_REQUEST_BY_LEADER(400, "PU_001", "파티장은 파티를 떠날 수 없습니다."),
    PARTY_DELEGATION_NOT_ALLOWED(400, "PU_002", "파티장만 권한을 위임할 수 있습니다."),
    PARTY_USER_ALREADY_MASTER(400, "PU_003", "이미 파티장인 멤버에게는 위임할 수 없습니다."),
    PARTY_USER_SELF_DELEGATION(400, "PU_004", "자기 자신에게 파티장 권한을 위임할 수 없습니다."),
    PARTY_USER_KICK_MASTER_NOT_ALLOWED(400, "PU_005", "파티장은 강제 퇴장시킬 수 없습니다."),

    // 403 영역: 권한 및 소속 여부 (핵심 추가)
    NOT_PARTY_MEMBER(403, "PU_301", "해당 파티의 멤버가 아닙니다."),
    NEED_MASTER_AUTHORITY(403, "PU_302", "파티장 권한이 없습니다."),
    NEED_MANAGER_AUTHORITY(403, "PU_303", "매니저 권한이 없습니다."),

    // 404 영역: 리소스 존재 여부
    PARTY_USER_NOT_FOUND(404, "PU_401", "파티원을 찾을 수 없습니다."),
    PARTY_MASTER_NOT_FOUND(404, "PU_402", "현재 파티장 정보를 찾을 수 없습니다."),


    ;

    private final int status;
    private final String code;
    private final String message;
}

