package com.partyguham.domain.notification.service.scheduler;

import com.partyguham.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationBatchScheduler {

    private final NotificationRepository notificationRepository;

    /**
     * 매일 새벽 4시에 2주 지난 알림 삭제
     * cron = "초 분 시 일 월 요일"
     */
    @Transactional // DB 삭제 작업을 위해 필요
    @Scheduled(cron = "0 0 4 * * *")
    public void cleanupOldNotifications() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);

        log.info("[Batch] 오래된 알림 삭제 스케줄러 시작. 기준 시간: {}", twoWeeksAgo);

        try {
            notificationRepository.deleteOldNotifications(twoWeeksAgo);
            log.info("[Batch] 오래된 알림 삭제 완료.");
        } catch (Exception e) {
            log.error("[Batch] 알림 삭제 중 에러 발생: {}", e.getMessage());
        }
    }
}