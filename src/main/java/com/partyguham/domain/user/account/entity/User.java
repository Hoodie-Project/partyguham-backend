package com.partyguham.domain.user.account.entity;

import com.partyguham.domain.auth.oauth.entity.OauthAccount;
import com.partyguham.global.entity.BaseEntity;
import com.partyguham.global.entity.Status;
import com.partyguham.global.exception.BusinessException;
import com.partyguham.domain.user.profile.entity.UserProfile;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;

import static com.partyguham.domain.user.exception.UserErrorCode.*;

@Entity
@Table(name = "users",
        indexes = @Index(name = "idx_nickname_lower", columnList = "lower(nickname)", unique = true))
@SequenceGenerator(name="users_seq_gen",
        sequenceName="users_id_seq",
        allocationSize=50)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq_gen")
    Long id;

    @Column(unique = true)
    String externalId;

    @Column(length = 255)
    private String fcmToken;

    @Column(unique = true)
    String email;

    @Column(nullable = false, unique = true, length = 15)
    String nickname;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserProfile profile;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<OauthAccount> oauths = new ArrayList<>();

    public void attachProfile(UserProfile p) {
        this.profile = p;
        p.setUser(this);
    }

    /**
     * 회원 탈퇴
     * 공통 상태를 DELETED로 변경하고, 개인정보를 마스킹 처리합니다.
     */
    public void withdraw() {
        if (this.getStatus() == Status.DELETED) {
            throw new BusinessException(USER_ALREADY_WITHDRAWN) {
            };
        }

        this.changeStatus(Status.DELETED);
        // 복구 가능성을 생각해야함
        // this.email = "deleted_" + this.getId();
        // this.nickname = "탈퇴유저#" + this.getId();
    }

    /**
     * 회원 복구
     */
    public void restore() {
        if (this.getStatus() != Status.INACTIVE) {
            throw new BusinessException(USER_PERMANENTLY_DELETED);
        }

        this.changeStatus(Status.ACTIVE);
    }

    /**
     * fcmToken 업데이트
     *
     * @param fcmToken
     */
    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    /**
     * fcmToken 존재 검증
     */
    public void validateFcmToken() {
        if (this.fcmToken == null || this.fcmToken.isBlank()) {
            throw new BusinessException(FCM_TOKEN_NOT_FOUND);
        }
    }

}