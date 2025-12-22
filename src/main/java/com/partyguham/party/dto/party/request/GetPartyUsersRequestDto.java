package com.partyguham.party.dto.party.request;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPartyUsersRequestDto {

    // 필수
    private Long partyId;
    private Integer page;     // 페이지 번호
    private Integer size;    // 데이터 개수

    // 정렬
    private String sort;      // createdAt (default)
    private String order;     // ASC / DESC (default = ASC)

    // 필터 옵션
    private String main;      // 직군 필터 (기획자, 디자이너, 개발자, 마케터/광고)
    private String nickname;  // 닉네임 검색

    // 기본값 보정 메서드
    public void applyDefaultValues() {
        if (this.page == null || this.page < 1)
            this.page = 1;

        if (this.size == null || this.size < 1)
            this.size = 10;

        if (this.sort == null || this.sort.isBlank())
            this.sort = "createdAt";

        if (this.order == null || this.order.isBlank())
            this.order = "ASC";
    }

    public Pageable toPageable() {
        return PageRequest.of(
                this.page - 1,
                this.size
        );
    }
}
