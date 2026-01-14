package com.partyguham.party.dto.party.request;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPartyUsersRequest {

    // 필수
    private Long partyId;

    @Builder.Default
    private Integer page = 1;     // 페이지 번호 (기본값 1)

    @Builder.Default
    private Integer size = 10;    // 데이터 개수 (기본값 10)

    // 정렬
    @Builder.Default
    private String sort = "createdAt";      // createdAt (default)

    @Builder.Default
    private String order = "ASC";     // ASC / DESC (default = ASC)

    // 필터 옵션
    private String main;      // 직군 필터 (기획자, 디자이너, 개발자, 마케터/광고)
    private String nickname;  // 닉네임 검색

    public Pageable toPageable() {
        return PageRequest.of(
                this.page - 1,
                this.size
        );
    }
}
