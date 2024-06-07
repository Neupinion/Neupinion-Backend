package com.neupinion.neupinion.issue.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "재가공 이슈 투표 순위 정보")
@Getter
@AllArgsConstructor
public class VoteRankingResponse {

    @Schema(description = "이슈 입장", example = "하이브")
    private final String stand;

    @Schema(description = "공감율", example = "10")
    private final int relatablePercentage;
}
