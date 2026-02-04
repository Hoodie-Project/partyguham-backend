package com.partyguham.auth.oauth.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOauthAccount is a Querydsl query type for OauthAccount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOauthAccount extends EntityPathBase<OauthAccount> {

    private static final long serialVersionUID = -242015995L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOauthAccount oauthAccount = new QOauthAccount("oauthAccount");

    public final StringPath externalId = createString("externalId");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<Provider> provider = createEnum("provider", Provider.class);

    public final com.partyguham.user.account.entity.QUser user;

    public QOauthAccount(String variable) {
        this(OauthAccount.class, forVariable(variable), INITS);
    }

    public QOauthAccount(Path<? extends OauthAccount> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOauthAccount(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOauthAccount(PathMetadata metadata, PathInits inits) {
        this(OauthAccount.class, metadata, inits);
    }

    public QOauthAccount(Class<? extends OauthAccount> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.partyguham.user.account.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

