package com.neupinion.neupinion.issue.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "세부 이슈 투표 결과 응답")
@AllArgsConstructor
@Getter
public class IssueVoteResultResponse {

    @Schema(description = "첫 번째 입장 공감 비율", example = "82")
    private final int firstStandRelatablePercentage;

    @Schema(description = "두 번째 입장 공감 비율", example = "18")
    private final int secondStandRelatablePercentage;
}
