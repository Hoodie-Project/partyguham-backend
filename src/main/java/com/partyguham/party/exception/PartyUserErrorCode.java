package com.partyguham.party.exception;

import com.partyguham.common.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PartyUserErrorCode implements ErrorCode {

    PARTY_USER_NOT_FOUND(404, "PU_001", "파티 유저를 찾을 수 없습니다."),
    INVALID_LEAVE_REQUEST_BY_LEADER(400, "PU_100", "파티장은 파티를 떠날 수 없습니다."),
    PARTY_MASTER_NOT_FOUND(404, "PU_002", "현재 파티장 정보를 찾을 수 없습니다."),
    PARTY_DELEGATION_NOT_ALLOWED(400, "PU_003", "파티장만 권한을 위임할 수 있습니다."),
    PARTY_USER_ALREADY_MASTER(400, "PU_004", "이미 파티장인 멤버에게는 위임할 수 없습니다."),
    PARTY_USER_SELF_DELEGATION(400, "PU_005", "자기 자신에게 파티장 권한을 위임할 수 없습니다."),
    PARTY_DELEGATION_TARGET_NOT_FOUND(404, "PU_006", "위임 대상 파티원을 찾을 수 없습니다."),
    PARTY_USER_KICK_MASTER_NOT_ALLOWED(400, "PU_007", "파티장은 강제 퇴장시킬 수 없습니다."),
    PARTY_USER_BATCH_KICK_MASTER_NOT_ALLOWED(400, "PU_008", "파티장은 배치 강제 퇴장시킬 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}

