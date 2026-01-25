package com.partyguham.party.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QParty is a Querydsl query type for Party
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QParty extends EntityPathBase<Party> {

    private static final long serialVersionUID = 2008660036L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QParty party = new QParty("party");

    public final com.partyguham.global.entity.QBaseEntity _super = new com.partyguham.global.entity.QBaseEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image = createString("image");

    public final ListPath<com.partyguham.recruitment.entity.PartyRecruitment, com.partyguham.recruitment.entity.QPartyRecruitment> partyRecruitments = this.<com.partyguham.recruitment.entity.PartyRecruitment, com.partyguham.recruitment.entity.QPartyRecruitment>createList("partyRecruitments", com.partyguham.recruitment.entity.PartyRecruitment.class, com.partyguham.recruitment.entity.QPartyRecruitment.class, PathInits.DIRECT2);

    public final EnumPath<PartyStatus> partyStatus = createEnum("partyStatus", PartyStatus.class);

    public final QPartyType partyType;

    public final ListPath<PartyUser, QPartyUser> partyUsers = this.<PartyUser, QPartyUser>createList("partyUsers", PartyUser.class, QPartyUser.class, PathInits.DIRECT2);

    //inherited
    public final EnumPath<com.partyguham.global.entity.Status> status = _super.status;

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QParty(String variable) {
        this(Party.class, forVariable(variable), INITS);
    }

    public QParty(Path<? extends Party> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QParty(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QParty(PathMetadata metadata, PathInits inits) {
        this(Party.class, metadata, inits);
    }

    public QParty(Class<? extends Party> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.partyType = inits.isInitialized("partyType") ? new QPartyType(forProperty("partyType")) : null;
    }

}

