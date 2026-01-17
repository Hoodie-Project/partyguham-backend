package com.partyguham.catalog.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPersonalityOption is a Querydsl query type for PersonalityOption
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPersonalityOption extends EntityPathBase<PersonalityOption> {

    private static final long serialVersionUID = -1671122226L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPersonalityOption personalityOption = new QPersonalityOption("personalityOption");

    public final StringPath content = createString("content");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPersonalityQuestion personalityQuestion;

    public final ListPath<com.partyguham.user.profile.entity.UserPersonality, com.partyguham.user.profile.entity.QUserPersonality> userPersonalities = this.<com.partyguham.user.profile.entity.UserPersonality, com.partyguham.user.profile.entity.QUserPersonality>createList("userPersonalities", com.partyguham.user.profile.entity.UserPersonality.class, com.partyguham.user.profile.entity.QUserPersonality.class, PathInits.DIRECT2);

    public QPersonalityOption(String variable) {
        this(PersonalityOption.class, forVariable(variable), INITS);
    }

    public QPersonalityOption(Path<? extends PersonalityOption> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPersonalityOption(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPersonalityOption(PathMetadata metadata, PathInits inits) {
        this(PersonalityOption.class, metadata, inits);
    }

    public QPersonalityOption(Class<? extends PersonalityOption> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.personalityQuestion = inits.isInitialized("personalityQuestion") ? new QPersonalityQuestion(forProperty("personalityQuestion")) : null;
    }

}

