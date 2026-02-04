package com.partyguham.domain.recruitment.service;

import com.partyguham.domain.party.entity.PartyUser;
import com.partyguham.domain.party.reader.PartyUserReader;
import com.partyguham.domain.recruitment.dto.request.CreatePartyRecruitmentRequest;
import com.partyguham.domain.recruitment.dto.request.PartyRecruitmentIdsBodyRequest;
import com.partyguham.domain.recruitment.dto.response.PartyRecruitmentsResponse;
import com.partyguham.domain.recruitment.entity.PartyRecruitment;
import com.partyguham.domain.recruitment.reader.PartyRecruitmentReader;
import com.partyguham.domain.recruitment.repository.PartyRecruitmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentAdminService {

    private final PartyUserReader partyUserReader;
    private final PartyRecruitmentReader partyRecruitmentReader;

    private final PartyRecruitmentRepository partyRecruitmentRepository;

    /**
     * 파티 모집공고 완료 처리
     */
    @Transactional
    public void completePartyRecruitment(Long partyId, Long partyRecruitmentId, Long userId) {
        PartyRecruitment recruitment = partyRecruitmentReader.getByPartyId(partyRecruitmentId, partyId);

        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, userId);
        partyUser.checkManager();


        recruitment.complete();
    }

    /**
     * 파티 모집공고 배치 완료 처리
     */
    @Transactional
    public void completePartyRecruitmentBatch(Long partyId, Long userId, PartyRecruitmentIdsBodyRequest request) {
        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, userId);
        partyUser.checkManager();

        List<PartyRecruitment> recruitments = partyRecruitmentRepository.findAllById(request.getPartyRecruitmentIds());

        // 요청한 ID 중 일부가 존재하지 않는지 확인 (findAllById메서드는 존재하지 않는 ID는 결과에 포함되지 않음)
        if (recruitments.size() != request.getPartyRecruitmentIds().size()) {
            throw new EntityNotFoundException("일부 모집공고를 찾을 수 없습니다.");
        }

        recruitments.forEach(PartyRecruitment::complete);
    }

    /**
     * 파티 모집공고 수정
     */
    @Transactional
    public PartyRecruitmentsResponse updatePartyRecruitment(
            Long partyId,
            Long partyRecruitmentId,
            Long userId,
            CreatePartyRecruitmentRequest request) {
        PartyRecruitment recruitment = partyRecruitmentReader.getByPartyId(partyRecruitmentId, partyId);

        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, userId);
        partyUser.checkManager();

        recruitment.update(request.getContent(), request.getMaxParticipants());

        return PartyRecruitmentsResponse.from(recruitment);
    }

    /**
     * 파티 모집 삭제
     */
    @Transactional
    public void deletePartyRecruitment(Long partyId, Long partyRecruitmentId, Long userId) {
        PartyRecruitment recruitment = partyRecruitmentReader.getByPartyId(partyRecruitmentId, partyId);

        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, userId);
        partyUser.checkManager();

        recruitment.delete();
    }

    /**
     * 파티 모집 다수 삭제
     */
    @Transactional
    public void deletePartyRecruitmentBatch(
            Long partyId,
            Long userId,
            PartyRecruitmentIdsBodyRequest request
    ) {
        // 파티 관리자 권한 확인
        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, userId);
        partyUser.checkManager();

        // 모집공고 조회 + 파티 소속 검증
        List<PartyRecruitment> recruitments =
                partyRecruitmentReader.readAllByIdsAndPartyId(
                        request.getPartyRecruitmentIds(),
                        partyId
                );

        // 소프트 삭제
        recruitments.forEach(PartyRecruitment::delete);
    }
}

