package com.partyguham.recruitment.entity;

import com.partyguham.common.entity.BaseEntity;
import com.partyguham.application.entity.PartyApplication;
import com.partyguham.party.entity.Party;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "party_recruitment")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SequenceGenerator(
        name = "party_recruitment_id_seq_gen",
        sequenceName = "party_recruitment_id_seq",
        allocationSize = 50
)
public class PartyRecruitment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "party_recruitment_id_seq_gen")
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String content;

    @Column(nullable = false)
    private Integer maxParticipants;

    @Column(nullable = false)
    private Integer currentParticipants;

    @Column(nullable = false)
    private Boolean isCompleted;

    @OneToMany(mappedBy = "partyRecruitment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyApplication> partyApplications;
}
