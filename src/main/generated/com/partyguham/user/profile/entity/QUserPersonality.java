package com.partyguham.user.profile.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserPersonality is a Querydsl query type for UserPersonality
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserPersonality extends EntityPathBase<UserPersonality> {

    private static final long serialVersionUID = 1941091007L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserPersonality userPersonality = new QUserPersonality("userPersonality");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.partyguham.catalog.entity.QPersonalityOption personalityOption;

    public final com.partyguham.catalog.entity.QPersonalityQuestion question;

    public final com.partyguham.user.account.entity.QUser user;

    public QUserPersonality(String variable) {
        this(UserPersonality.class, forVariable(variable), INITS);
    }

    public QUserPersonality(Path<? extends UserPersonality> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserPersonality(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserPersonality(PathMetadata metadata, PathInits inits) {
        this(UserPersonality.class, metadata, inits);
    }

    public QUserPersonality(Class<? extends UserPersonality> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.personalityOption = inits.isInitialized("personalityOption") ? new com.partyguham.catalog.entity.QPersonalityOption(forProperty("personalityOption"), inits.get("personalityOption")) : null;
        this.question = inits.isInitialized("question") ? new com.partyguham.catalog.entity.QPersonalityQuestion(forProperty("question")) : null;
        this.user = inits.isInitialized("user") ? new com.partyguham.user.account.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

