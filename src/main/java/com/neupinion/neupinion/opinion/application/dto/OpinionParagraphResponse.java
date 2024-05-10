package com.neupinion.neupinion.opinion.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "문단별 보기 의견 응답")
@Getter
@AllArgsConstructor
public class OpinionParagraphResponse {

    @Schema(description = "문단 ID", example = "1")
    private final Long id;

    @Schema(description = "문단 내용", example = "펜타곤이 폭발한 사진이 트위터에서 활발하게 공유되었습니다.")
    private final String content;

    @Schema(description = "문단과 관련된 의견들")
    private final List<ReprocessedIssueOpinionResponse> opinions;
}
