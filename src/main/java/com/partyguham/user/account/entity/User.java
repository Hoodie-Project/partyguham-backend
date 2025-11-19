package com.partyguham.user.account.entity;

import com.partyguham.auth.oauth.entity.OauthAccount;
import com.partyguham.common.entity.BaseEntity;
import com.partyguham.user.profile.entity.UserLocation;
import com.partyguham.user.profile.entity.UserProfile;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    Long id;

    @Column(unique=true)
    String externalId;

    @Column(unique=true)
    String email;

    @Column(nullable=false, length=15)
    String nickname;

    @OneToOne(mappedBy="user", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
    private UserProfile profile;

    @OneToMany(mappedBy="user", cascade=CascadeType.REMOVE, orphanRemoval=true)
    private List<OauthAccount> oauths = new ArrayList<>();

    public void attachProfile(UserProfile p){
        this.profile=p;
        p.setUser(this);
    }


}