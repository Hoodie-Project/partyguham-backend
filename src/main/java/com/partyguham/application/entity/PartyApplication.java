package com.partyguham.application.entity;

import com.partyguham.common.entity.BaseEntity;
import com.partyguham.party.entity.PartyUser;
import com.partyguham.recruitment.entity.PartyRecruitment;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "party_applications")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SequenceGenerator(
        name = "party_application_id_seq_gen",
        sequenceName = "party_application_id_seq",
        allocationSize = 50
)
public class PartyApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "party_application_id_seq_gen")
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_user_id", nullable = false)
    private PartyUser partyUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_recruitment_id", nullable = false)
    private PartyRecruitment partyRecruitment;

    @Column(columnDefinition = "text")
    private String message;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false)
    private PartyApplicationStatus applicationStatus = PartyApplicationStatus.PENDING;
}
