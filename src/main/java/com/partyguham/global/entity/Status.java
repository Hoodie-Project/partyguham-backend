package com.partyguham.global.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// 레코드 라이프사이클
public enum Status {
    ACTIVE,      // 데이터가 활성화되어 사용 가능한 상태
    INACTIVE,    // 데이터가 비활성화되어 사용 불가능한 상태
    DELETED,     // 데이터가 삭제된 상태
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
