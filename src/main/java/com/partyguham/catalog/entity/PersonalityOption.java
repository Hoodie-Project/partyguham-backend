package com.partyguham.catalog.entity;

import jakarta.persistence.*;

@Entity
public class PersonalityOption {

    @Id @GeneratedValue
    private Long id;

    private String text;   // 보기(옵션) 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private PersonalityQuestion question;
}
