package com.neupinion.neupinion.issue.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "세부 이슈 투표 결과 응답")
@AllArgsConstructor
@Getter
public class IssueVoteResultResponse {

    @Schema(description = "신뢰 비율", example = "82")
    private final int trustRate;

    @Schema(description = "의심 비율", example = "18")
    private final int doubtRate;
}
