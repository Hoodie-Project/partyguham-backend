package com.partyguham.party.repository;

import com.partyguham.party.dto.partyAdmin.request.GetAdminPartyUsersRequestDto;
import com.partyguham.party.entity.PartyUser;
import com.partyguham.party.entity.PartyAuthority;
import com.partyguham.common.entity.Status;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.partyguham.party.entity.QPartyUser.partyUser;
import static com.partyguham.user.account.entity.QUser.user;
import static com.partyguham.catalog.entity.QPosition.position;
import static com.partyguham.user.profile.entity.QUserProfile.userProfile;

@Repository
@RequiredArgsConstructor
public class PartyUserQueryRepositoryImpl implements PartyUserQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public long countAllByPartyIdNotDeleted(Long partyId) {
        // 이 파티에 삭제되지 않은 파티원이 몇 명인지 (전체 인원 수)
        Long count = queryFactory
                .select(partyUser.count())
                .from(partyUser)
                .where(
                        partyUser.party.id.eq(partyId),
                        partyUser.status.ne(Status.DELETED)
                )
                .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public Page<PartyUser> searchAdminPartyUsers(Long partyId,
                                                 GetAdminPartyUsersRequestDto req,
                                                 Pageable pageable) {

        BooleanBuilder where = buildWhere(partyId, req);

        // 1) 내용 조회 (join + fetchJoin + 페이징)
        List<PartyUser> content = queryFactory
                .selectFrom(partyUser)
                .join(partyUser.user, user).fetchJoin()
                .leftJoin(user.profile, userProfile).fetchJoin()
                .leftJoin(partyUser.position, position).fetchJoin()
                .where(where)
                .orderBy(partyUser.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2) 카운트 조회 (페이징 계산용)
        Long total = queryFactory
                .select(partyUser.count())
                .from(partyUser)
                .join(partyUser.user, user)
                .leftJoin(partyUser.position, position)
                .where(where)
                .fetchOne();

        long totalCount = total != null ? total : 0L;

        return new PageImpl<>(content, pageable, totalCount);
    }

    /**
     * 필터 조건(authority, nickname, main 등)을 한 곳에서 조립
     */
    private BooleanBuilder buildWhere(Long partyId, GetAdminPartyUsersRequestDto req) {
        BooleanBuilder where = new BooleanBuilder()
                .and(partyUser.party.id.eq(partyId))
                .and(partyUser.status.ne(Status.DELETED));

        PartyAuthority authority = req.getAuthority();
        String nickname = req.getNickname();
        String main = req.getMain();

        if (authority != null) {
            where.and(partyUser.authority.eq(authority));
        }
        if (nickname != null && !nickname.isBlank()) {
            where.and(user.nickname.containsIgnoreCase(nickname));
        }
        if (main != null && !main.isBlank()) {
            where.and(position.main.eq(main));
        }

        return where;
    }
}