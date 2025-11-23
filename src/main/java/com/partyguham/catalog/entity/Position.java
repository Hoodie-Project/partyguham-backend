package com.partyguham.catalog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "position")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "position_seq_gen",
        sequenceName = "position_seq",   // DB에 생성된 시퀀스 이름
        allocationSize = 50
)
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "position_seq_gen")
    private Long id;

    @Column(nullable = false)
    private String main;

    @Column(nullable = false)
    private String sub;

    // ==== 연관관계 ====
//    @OneToMany(mappedBy = "position")
//    private List<UserCareer> userPositions = new ArrayList<>();
//
//    @OneToMany(mappedBy = "position")
//    private List<PartyUser> partyUsers = new ArrayList<>();
//
//    @OneToMany(mappedBy = "position")
//    private List<PartyRecruitment> partyRecruitments = new ArrayList<>();
}