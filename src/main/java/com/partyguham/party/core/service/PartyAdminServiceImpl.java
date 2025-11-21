package com.partyguham.party.core.service;

import com.partyguham.party.core.dto.partyAdmin.request.*;
import com.partyguham.party.core.dto.partyAdmin.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyAdminServiceImpl implements PartyAdminService {
    @Override
    public UpdatePartyResponseDto updateParty(Long partyId, Long userId, UpdatePartyRequestDto request) {
        return null;
    }

    @Override
    public UpdatePartyStatusResponseDto updatePartyStatus(Long partyId,
                                                          Long userId,
                                                          UpdatePartyStatusRequestDto request) {
        return null;
    }

    @Override
    public void deletePartyImage(Long partyId, Long userId) {

    }

    @Override
    public void deleteParty(Long partyId, Long userId) {

    }

    @Override
    public PartyDelegationResponseDto delegateParty(Long partyId, Long userId, PartyDelegationRequestDto request) {
        return null;
    }

    @Override
    public GetAdminPartyUsersResponseDto getPartyUsers(Long partyId,
                                                       GetAdminPartyUsersRequestDto request,
                                                       Long userId) {
        return null;
    }

    @Override
    public UpdatePartyUserResponseDto updatePartyUser(Long partyId,
                                                      Long partyUserId,
                                                      Long userId,
                                                      UpdatePartyUserRequestDto request) {
        return null;
    }

    @Override
    public void deletePartyUser(Long partyId, Long partyUserId, Long userId) {

    }

    @Override
    public void deletePartyUserBatch(Long partyId, Long userId, DeletePartyUsersBodyRequestDto request) {

    }
}
