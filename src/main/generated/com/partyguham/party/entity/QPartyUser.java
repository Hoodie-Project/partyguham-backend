package com.partyguham.party.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPartyUser is a Querydsl query type for PartyUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPartyUser extends EntityPathBase<PartyUser> {

    private static final long serialVersionUID = 402937391L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPartyUser partyUser = new QPartyUser("partyUser");

    public final com.partyguham.global.entity.QBaseEntity _super = new com.partyguham.global.entity.QBaseEntity(this);

    public final EnumPath<PartyAuthority> authority = createEnum("authority", PartyAuthority.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QParty party;

    public final com.partyguham.catalog.entity.QPosition position;

    //inherited
    public final EnumPath<com.partyguham.global.entity.Status> status = _super.status;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.partyguham.user.account.entity.QUser user;

    public QPartyUser(String variable) {
        this(PartyUser.class, forVariable(variable), INITS);
    }

    public QPartyUser(Path<? extends PartyUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPartyUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPartyUser(PathMetadata metadata, PathInits inits) {
        this(PartyUser.class, metadata, inits);
    }

    public QPartyUser(Class<? extends PartyUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.party = inits.isInitialized("party") ? new QParty(forProperty("party"), inits.get("party")) : null;
        this.position = inits.isInitialized("position") ? new com.partyguham.catalog.entity.QPosition(forProperty("position")) : null;
        this.user = inits.isInitialized("user") ? new com.partyguham.user.account.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

