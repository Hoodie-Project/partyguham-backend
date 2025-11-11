package com.partyguham.catalog.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class PersonalityQuestion {

    @Id
    @GeneratedValue
    private Long id;

    private String text;   // 질문 내용

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<PersonalityOption> options = new ArrayList<>();
}