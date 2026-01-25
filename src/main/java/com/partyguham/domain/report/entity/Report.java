package com.partyguham.domain.report.entity;

import com.partyguham.global.entity.BaseEntity;
import com.partyguham.domain.user.account.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "reports_seq_gen",
        sequenceName = "reports_id_seq",
        allocationSize = 1
)
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reports_seq_gen")
    private Long id;

    /** 신고 대상 타입 (USER / PARTY / PARTY_RECRUITMENT ...) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReportTargetType targetType;

    /** 신고 대상 PK */
    @Column(nullable = false)
    private Long targetId;

    /** 신고 내용 */
    @Lob
    @Column(nullable = false)
    private String content;

    /** 신고한 유저 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;
}
