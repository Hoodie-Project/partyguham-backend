package com.partyguham.banner.entity;

import com.partyguham.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "banners")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@SequenceGenerator(
        name = "banners_seq_gen",
        sequenceName = "banners_id_seq",
        allocationSize = 1
)
public class Banner extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "banners_seq_gen")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BannerPlatform platform;  // WEB / APP

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 255)
    private String image;

    @Column(columnDefinition = "text")
    private String link;  // nullable
}