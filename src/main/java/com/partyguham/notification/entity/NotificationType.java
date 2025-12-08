package com.partyguham.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "notification_types",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_notification_type",
                        columnNames = "type"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NotificationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ex) "PARTY, RECRUIT"
    @Column(nullable = false, length = 50)
    private String type;

    // 화면에 보여줄 이름
    @Column(nullable = false, length = 100)
    private String label;

    @Builder.Default
    @OneToMany(mappedBy = "notificationType")
    private List<Notification> notifications = new ArrayList<>();
}