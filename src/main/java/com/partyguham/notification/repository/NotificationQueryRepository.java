package com.partyguham.notification.repository;

import com.partyguham.notification.entity.Notification;
import com.partyguham.notification.entity.NotificationType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.partyguham.notification.entity.QNotification.notification;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Slice<Notification> findNotifications(
            Long userId,
            int size,
            Long cursor,              // null이면 처음 페이지
            NotificationType type   // null이면 타입 필터 없음
    ) {
        // where 조건 조립
        BooleanBuilder builder = new BooleanBuilder()
                .and(notification.user.id.eq(userId));

        // 1. 엔티티의 Enum 필드와 직접 비교
        if (type != null) {
            builder.and(notification.type.eq(type));
        }

        if (cursor != null) {
            // cursor 보다 작은 id 들만 (DESC 정렬이니까)
            builder.and(notification.id.lt(cursor));
        }

        // size + 1 로 hasNext 판단
        List<Notification> result = queryFactory
                .selectFrom(notification)
                .where(builder)
                .orderBy(notification.id.desc())
                .limit(size + 1)
                .fetch();

        boolean hasNext = false;
        if (result.size() > size) {
            hasNext = true;
            result.remove(size); // 초과분 하나 제거
        }

        return new SliceImpl<>(result, PageRequest.of(0, size), hasNext);
    }
}