package com.partyguham.party.exception;

public class PartyUserNotFoundException extends RuntimeException {
    
    public PartyUserNotFoundException(Long partyId, Long userId) {
        super("파티에 속한 유저를 찾을 수 없습니다.");
    }
}

