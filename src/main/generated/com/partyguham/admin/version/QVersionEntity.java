package com.partyguham.admin.version;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QVersionEntity is a Querydsl query type for VersionEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVersionEntity extends EntityPathBase<VersionEntity> {

    private static final long serialVersionUID = 1687620261L;

    public static final QVersionEntity versionEntity = new QVersionEntity("versionEntity");

    public final StringPath downloadUrl = createString("downloadUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isForceUpdate = createBoolean("isForceUpdate");

    public final StringPath latestVersion = createString("latestVersion");

    public final StringPath minRequiredVersion = createString("minRequiredVersion");

    public final StringPath platform = createString("platform");

    public final StringPath releaseNotes = createString("releaseNotes");

    public QVersionEntity(String variable) {
        super(VersionEntity.class, forVariable(variable));
    }

    public QVersionEntity(Path<? extends VersionEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVersionEntity(PathMetadata metadata) {
        super(VersionEntity.class, metadata);
    }

}

