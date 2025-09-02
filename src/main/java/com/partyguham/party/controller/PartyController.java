package com.partyguham.party.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PartyController {

    @GetMapping("/party/{partyId}")
    public String getParty(@PathVariable Long partyId,
                           @RequestParam String status){
        return "party";
    }



}
