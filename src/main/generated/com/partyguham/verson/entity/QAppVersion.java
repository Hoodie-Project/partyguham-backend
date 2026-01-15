package com.partyguham.verson.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAppVersion is a Querydsl query type for AppVersion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAppVersion extends EntityPathBase<AppVersion> {

    private static final long serialVersionUID = -1197788700L;

    public static final QAppVersion appVersion = new QAppVersion("appVersion");

    public final com.partyguham.common.entity.QBaseEntity _super = new com.partyguham.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath downloadUrl = createString("downloadUrl");

    public final BooleanPath forceUpdate = createBoolean("forceUpdate");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath latestVersion = createString("latestVersion");

    public final StringPath minRequiredVersion = createString("minRequiredVersion");

    public final EnumPath<AppPlatform> platform = createEnum("platform", AppPlatform.class);

    public final StringPath releaseNotes = createString("releaseNotes");

    //inherited
    public final EnumPath<com.partyguham.common.entity.Status> status = _super.status;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QAppVersion(String variable) {
        super(AppVersion.class, forVariable(variable));
    }

    public QAppVersion(Path<? extends AppVersion> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAppVersion(PathMetadata metadata) {
        super(AppVersion.class, metadata);
    }

}

