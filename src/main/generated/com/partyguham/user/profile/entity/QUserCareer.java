package com.partyguham.user.profile.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserCareer is a Querydsl query type for UserCareer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserCareer extends EntityPathBase<UserCareer> {

    private static final long serialVersionUID = -331976563L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserCareer userCareer = new QUserCareer("userCareer");

    public final EnumPath<CareerType> careerType = createEnum("careerType", CareerType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.partyguham.catalog.entity.QPosition position;

    public final com.partyguham.user.account.entity.QUser user;

    public final NumberPath<Integer> years = createNumber("years", Integer.class);

    public QUserCareer(String variable) {
        this(UserCareer.class, forVariable(variable), INITS);
    }

    public QUserCareer(Path<? extends UserCareer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserCareer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserCareer(PathMetadata metadata, PathInits inits) {
        this(UserCareer.class, metadata, inits);
    }

    public QUserCareer(Class<? extends UserCareer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.position = inits.isInitialized("position") ? new com.partyguham.catalog.entity.QPosition(forProperty("position")) : null;
        this.user = inits.isInitialized("user") ? new com.partyguham.user.account.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

