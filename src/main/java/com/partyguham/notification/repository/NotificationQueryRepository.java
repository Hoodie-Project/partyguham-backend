package com.partyguham.notification.repository;

import com.partyguham.notification.entity.Notification;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.partyguham.notification.entity.QNotification.notification;
import static com.partyguham.notification.entity.QNotificationType.notificationType;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Slice<Notification> findNotifications(
            Long userId,
            int limit,
            Long cursor,              // null이면 처음 페이지
            Long notificationTypeId   // null이면 타입 필터 없음
    ) {
        // where 조건 조립
        BooleanBuilder builder = new BooleanBuilder()
                .and(notification.user.id.eq(userId));

        if (notificationTypeId != null) {
            builder.and(notification.notificationType.id.eq(notificationTypeId));
        }

        if (cursor != null) {
            // cursor 보다 작은 id 들만 (DESC 정렬이니까)
            builder.and(notification.id.lt(cursor));
        }

        // limit + 1 로 hasNext 판단
        List<Notification> result = queryFactory
                .selectFrom(notification)
                .join(notification.notificationType, notificationType).fetchJoin()
                .where(builder)
                .orderBy(notification.id.desc())
                .limit(limit + 1)
                .fetch();

        boolean hasNext = false;
        if (result.size() > limit) {
            hasNext = true;
            result.remove(limit); // 초과분 하나 제거
        }

        return new SliceImpl<>(result, PageRequest.of(0, limit), hasNext);
    }
}