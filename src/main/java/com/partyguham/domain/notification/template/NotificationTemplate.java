package com.partyguham.domain.notification.template;

public enum NotificationTemplate {
    // 파티 상태 변경
    PARTY_FINISHED(
            "파티가 성공적으로 종료되었어요",
            "'%s' 파티가 성공적으로 종료되었어요. 참여해 주셔서 감사합니다."
    ),
    PARTY_REOPENED(
            "파티가 다시 활성화되었어요",
            "'%s' 파티가 다시 활성화되었어요. 다시 새로운 도전을 시작해 보세요."
    ),
    PARTY_INFO_UPDATED(
            "파티 정보가 업데이트되었어요",
            "'%s' 파티 정보가 업데이트되었어요. 변경된 내용을 확인하세요."
    ),

    // 파티원 변경
    PARTY_MEMBER_LEFT(
            "파티원이 탈퇴했어요",
            "'%s' 파티에서 %s님이 탈퇴했어요."
    ),
    PARTY_MEMBER_KICKED(
            "파티원이 제외되었어요",
            "'%s' 파티에서 %s님이 제외되었습니다."
    ),
    PARTY_LEADER_CHANGED(
            "파티장이 변경되었어요",
            "'%s' 파티에서 파티장이 %s님이 되었어요."
    ),
    PARTY_MEMBER_POSITION_CHANGED(
            "파티원의 포지션이 변경되었어요",
            "'%s' 파티에서 %s님의 포지션이 %s(으)로 변경되었어요."
    ),

    // 지원자/합류 관련
    PARTY_APPLICATION_CREATED(
            "새로운 파티 지원이 도착했어요",
            "'%s' 파티에 %s님이 지원했습니다. 지원서를 검토해 보세요."
    ),
    PARTY_APPLICATION_ACCEPTED(
            "지원이 수락되었어요",
            "'%s' 파티에서 지원이 수락되었어요. 합류 여부를 결정해 주세요."
    ),
    PARTY_APPLICATION_REJECTED(
            "지원이 거절되었어요",
            "'%s' 파티에서 지원이 거절되었어요."
    ),
    PARTY_APPLICATION_DECLINED(
            "지원자가 합류를 거절했어요",
            "%s님이 파티 합류를 거절했어요. 다른 지원자를 확인해 보세요."
    ),
    PARTY_NEW_MEMBER_JOINED(
            "새로운 파티원이 합류했어요",
            "%s님이 새롭게 '%s' 파티에 합류했어요."
    ),
    PARTY_RECRUITMENT_CLOSED(
            "지원한 모집 공고가 마감되었어요",
            "'%s' 파티에 지원하신 파티 모집공고가 마감되었습니다."
    );

    private final String title;
    private final String body;

    NotificationTemplate(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String title(Object... args) {
        return String.format(title, args);
    }

    public String body(Object... args) {
        return String.format(body, args);
    }
}
