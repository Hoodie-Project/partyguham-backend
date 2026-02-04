package com.partyguham.user.profile.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserLocation is a Querydsl query type for UserLocation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserLocation extends EntityPathBase<UserLocation> {

    private static final long serialVersionUID = 705954052L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserLocation userLocation = new QUserLocation("userLocation");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.partyguham.catalog.entity.QLocation location;

    public final com.partyguham.user.account.entity.QUser user;

    public QUserLocation(String variable) {
        this(UserLocation.class, forVariable(variable), INITS);
    }

    public QUserLocation(Path<? extends UserLocation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserLocation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserLocation(PathMetadata metadata, PathInits inits) {
        this(UserLocation.class, metadata, inits);
    }

    public QUserLocation(Class<? extends UserLocation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.location = inits.isInitialized("location") ? new com.partyguham.catalog.entity.QLocation(forProperty("location")) : null;
        this.user = inits.isInitialized("user") ? new com.partyguham.user.account.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

