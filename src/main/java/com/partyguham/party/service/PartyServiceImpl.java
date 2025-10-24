package com.partyguham.party.service;

import com.partyguham.party.repository.PartyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyServiceImpl implements PartyService{

    @Autowired
    PartyRepository partyRepository;

    public void save(){
        partyRepository.findById(1L);
    }
}
