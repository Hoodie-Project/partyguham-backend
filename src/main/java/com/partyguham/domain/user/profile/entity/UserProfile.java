package com.partyguham.domain.user.profile.entity;

import com.partyguham.domain.user.account.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name="user_profiles",
        uniqueConstraints=@UniqueConstraint(name="uk_profile_nickname", columnNames="nickname"))
@SequenceGenerator(name="user_profiles_seq_gen",
        sequenceName="user_profiles_id_seq",
        allocationSize=50)
@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
//@Builder
@Setter
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_profiles_seq_gen")
    Long id;

    LocalDate birth;

    @Enumerated(EnumType.STRING)
    Gender gender; // MALE/FEMALE/UNKNOWN

    boolean birthVisible = true;

    boolean genderVisible = true;

    String portfolioTitle;

    @Lob String portfolio;

    @Lob String image;

    //==RelationShip
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false, unique=true)
    private User user;

}