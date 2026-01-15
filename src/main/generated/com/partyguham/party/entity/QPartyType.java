package com.partyguham.party.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPartyType is a Querydsl query type for PartyType
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPartyType extends EntityPathBase<PartyType> {

    private static final long serialVersionUID = 402913694L;

    public static final QPartyType partyType = new QPartyType("partyType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<Party, QParty> parties = this.<Party, QParty>createList("parties", Party.class, QParty.class, PathInits.DIRECT2);

    public final StringPath type = createString("type");

    public QPartyType(String variable) {
        super(PartyType.class, forVariable(variable));
    }

    public QPartyType(Path<? extends PartyType> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPartyType(PathMetadata metadata) {
        super(PartyType.class, metadata);
    }

}

