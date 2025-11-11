package com.partyguham.user.profile.entity;

import com.partyguham.user.account.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name="user_profile",
        uniqueConstraints=@UniqueConstraint(name="uk_profile_nickname", columnNames="nickname"))
@Getter
@Setter
public class UserProfile {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;

    @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id", nullable=false, unique=true)
    private User user;

    LocalDate birth;

    @Enumerated(EnumType.STRING)
    Gender gender; // MALE/FEMALE/UNKNOWN

    boolean birthVisible = true;

    boolean genderVisible = true;

    String portfolioTitle;

    @Lob String portfolio;

    @Lob String image;

    // 예: region/career/trait FK/조인테이블은 여기로 이동
}