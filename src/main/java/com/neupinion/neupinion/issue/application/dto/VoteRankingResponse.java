package com.neupinion.neupinion.issue.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "재가공 이슈 투표 순위 정보")
@Getter
@AllArgsConstructor
public class VoteRankingResponse {

    @Schema(description = "투표 결과 값", example = "완전 신뢰")
    private final String status;

    @Schema(description = "득표율", example = "10")
    private final int votePercentage;
}
