package com.partyguham.common.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    ACTIVE,      // 데이터가 활성화되어 사용 가능한 상태
    INACTIVE,    // 데이터가 비활성화되어 사용 불가능한 상태
    DELETED,     // 데이터가 삭제된 상태
    PENDING,     // 데이터가 처리 대기 중인 상태
    PROCESSING,  // 데이터가 처리 중인 상태
    COMPLETED,   // 데이터가 처리 완료된 상태
    APPROVED,    // 데이터가 승인된 상태
    REJECTED,    // 데이터가 거절된 상태
    SUSPENDED,   // 데이터가 일시 중지된 상태
    CANCELED,    // 데이터가 취소된 상태
    EXPIRED,     // 데이터가 유효기간이 만료된 상태
    ARCHIVED;     // 데이터가 보관된 상태

    // JSON → Enum (역직렬화)
    @JsonCreator
    public static Status from(String value) {
        return Status.valueOf(value.toUpperCase());
    }

    // Enum → JSON (직렬화)
    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
