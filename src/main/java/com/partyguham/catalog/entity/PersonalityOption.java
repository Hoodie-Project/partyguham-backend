package com.partyguham.catalog.entity;

import com.partyguham.user.profile.entity.UserPersonality;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "personality_options")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "personality_option_seq_gen",
        sequenceName = "personality_option_seq",
        allocationSize = 50
)
public class PersonalityOption {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personality_option_seq_gen")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personality_question_id", nullable = false)
    private PersonalityQuestion personalityQuestion;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @OneToMany(
            mappedBy = "personalityOption",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<UserPersonality> userPersonalities = new ArrayList<>();
}