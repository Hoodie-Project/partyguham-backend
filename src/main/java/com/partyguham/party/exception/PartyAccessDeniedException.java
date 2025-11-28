package com.partyguham.party.exception;

public class PartyAccessDeniedException extends RuntimeException {

    public PartyAccessDeniedException(Long partyId, Long userId, String reason) {
        super("User " + userId + " cannot access party " + partyId + " : " + reason);
    }
}