package com.partyguham.party.entity;

import com.partyguham.common.entity.BaseEntity;
import com.partyguham.common.entity.Status;
import com.partyguham.recruitment.entity.PartyRecruitment;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "parties")
@SequenceGenerator(name="parties_seq_gen",
        sequenceName="parties_id_seq",
        allocationSize=50)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Party extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "parties_seq_gen")
    @Column
    private Long id;

    @Column
    private String title;

    @Column(columnDefinition = "text")
    private String content;

    @Column()
    private String image;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyStatus partyStatus = PartyStatus.IN_PROGRESS;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_type_id", nullable = false)
    private PartyType partyType;

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyUser> partyUsers;

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyRecruitment> partyRecruitments;

    public void delete() {
        this.changeStatus(Status.DELETED);
    }

}
