package com.partyguham.auth.oauth.entity;

import com.partyguham.user.account.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
        name = "oauth_accounts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_oauth_provider_external_id",
                        columnNames = {"provider", "external_id"}
                )
        },
        indexes = {
                @Index(name = "idx_oauth_user", columnList = "user_id")
        }
)
@SequenceGenerator(
        name = "oauth_accounts_seq_gen",
        sequenceName = "oauth_accounts_id_seq",
        allocationSize = 1
)
public class OauthAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oauth_accounts_seq_gen")
    private Long id;

    @Column(name = "external_id", nullable = false)
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    public static OauthAccount of(User u, Provider p, String extId){
        var oa = new OauthAccount();
        oa.user = u;
        oa.provider = p;
        oa.externalId = extId;
        return oa;
    }
}