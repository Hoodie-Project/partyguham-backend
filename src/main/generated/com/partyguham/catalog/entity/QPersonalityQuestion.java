package com.partyguham.catalog.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPersonalityQuestion is a Querydsl query type for PersonalityQuestion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPersonalityQuestion extends EntityPathBase<PersonalityQuestion> {

    private static final long serialVersionUID = -717540353L;

    public static final QPersonalityQuestion personalityQuestion = new QPersonalityQuestion("personalityQuestion");

    public final StringPath content = createString("content");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<PersonalityOption, QPersonalityOption> personalityOptions = this.<PersonalityOption, QPersonalityOption>createList("personalityOptions", PersonalityOption.class, QPersonalityOption.class, PathInits.DIRECT2);

    public final NumberPath<Short> responseCount = createNumber("responseCount", Short.class);

    public QPersonalityQuestion(String variable) {
        super(PersonalityQuestion.class, forVariable(variable));
    }

    public QPersonalityQuestion(Path<? extends PersonalityQuestion> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPersonalityQuestion(PathMetadata metadata) {
        super(PersonalityQuestion.class, metadata);
    }

}

