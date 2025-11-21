package com.partyguham.verson.entity;

import com.partyguham.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_version")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "app_version_seq_gen",
        sequenceName = "app_version_seq",
        allocationSize = 50
)
public class AppVersion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_version_seq_gen")
    private Long id;

    // android / ios
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppPlatform platform;

    @Column(name = "latest_version", nullable = false, length = 20)
    private String latestVersion;

    @Column(name = "min_required_version", length = 20)
    private String minRequiredVersion;

    @Column(name = "release_notes", columnDefinition = "text")
    private String releaseNotes;

    @Column(name = "is_force_update", nullable = false)
    private boolean forceUpdate;

    @Column(name = "download_url", length = 255)
    private String downloadUrl;
}