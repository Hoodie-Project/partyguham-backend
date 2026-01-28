package com.partyguham.application.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPartyApplication is a Querydsl query type for PartyApplication
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPartyApplication extends EntityPathBase<PartyApplication> {

    private static final long serialVersionUID = 1105714070L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPartyApplication partyApplication = new QPartyApplication("partyApplication");

    public final com.partyguham.global.entity.QBaseEntity _super = new com.partyguham.global.entity.QBaseEntity(this);

    public final EnumPath<PartyApplicationStatus> applicationStatus = createEnum("applicationStatus", PartyApplicationStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath message = createString("message");

    public final com.partyguham.recruitment.entity.QPartyRecruitment partyRecruitment;

    //inherited
    public final EnumPath<com.partyguham.global.entity.Status> status = _super.status;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.partyguham.user.account.entity.QUser user;

    public QPartyApplication(String variable) {
        this(PartyApplication.class, forVariable(variable), INITS);
    }

    public QPartyApplication(Path<? extends PartyApplication> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPartyApplication(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPartyApplication(PathMetadata metadata, PathInits inits) {
        this(PartyApplication.class, metadata, inits);
    }

    public QPartyApplication(Class<? extends PartyApplication> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.partyRecruitment = inits.isInitialized("partyRecruitment") ? new com.partyguham.recruitment.entity.QPartyRecruitment(forProperty("partyRecruitment"), inits.get("partyRecruitment")) : null;
        this.user = inits.isInitialized("user") ? new com.partyguham.user.account.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

