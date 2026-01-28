package com.partyguham.recruitment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPartyRecruitment is a Querydsl query type for PartyRecruitment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPartyRecruitment extends EntityPathBase<PartyRecruitment> {

    private static final long serialVersionUID = -1053669330L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPartyRecruitment partyRecruitment = new QPartyRecruitment("partyRecruitment");

    public final com.partyguham.global.entity.QBaseEntity _super = new com.partyguham.global.entity.QBaseEntity(this);

    public final BooleanPath completed = createBoolean("completed");

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> currentParticipants = createNumber("currentParticipants", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> maxParticipants = createNumber("maxParticipants", Integer.class);

    public final com.partyguham.party.entity.QParty party;

    public final ListPath<com.partyguham.application.entity.PartyApplication, com.partyguham.application.entity.QPartyApplication> partyApplications = this.<com.partyguham.application.entity.PartyApplication, com.partyguham.application.entity.QPartyApplication>createList("partyApplications", com.partyguham.application.entity.PartyApplication.class, com.partyguham.application.entity.QPartyApplication.class, PathInits.DIRECT2);

    public final com.partyguham.catalog.entity.QPosition position;

    //inherited
    public final EnumPath<com.partyguham.global.entity.Status> status = _super.status;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPartyRecruitment(String variable) {
        this(PartyRecruitment.class, forVariable(variable), INITS);
    }

    public QPartyRecruitment(Path<? extends PartyRecruitment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPartyRecruitment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPartyRecruitment(PathMetadata metadata, PathInits inits) {
        this(PartyRecruitment.class, metadata, inits);
    }

    public QPartyRecruitment(Class<? extends PartyRecruitment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.party = inits.isInitialized("party") ? new com.partyguham.party.entity.QParty(forProperty("party"), inits.get("party")) : null;
        this.position = inits.isInitialized("position") ? new com.partyguham.catalog.entity.QPosition(forProperty("position")) : null;
    }

}

