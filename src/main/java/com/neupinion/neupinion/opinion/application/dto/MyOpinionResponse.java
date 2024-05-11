package com.neupinion.neupinion.opinion.application.dto;

import com.neupinion.neupinion.opinion.domain.FollowUpIssueOpinion;
import com.neupinion.neupinion.opinion.domain.ReprocessedIssueOpinion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "내 의견 조회 응답")
public class MyOpinionResponse {

    @Schema(description = "의견 id", example = "1")
    private Long id;

    @Schema(description = "문단 id", example = "1")
    private Long paragraphId;

    @Schema(description = "문단 내용", example = "문단 내용")
    private String paragraphContent;

    @Schema(description = "의견 내용", example = "의견 내용")
    private String content;

    @Schema(description = "의견 신뢰도 평가", example = "true")
    private boolean isReliable;

    public static MyOpinionResponse from(final FollowUpIssueOpinion opinion, final String paragraphContent) {
        return new MyOpinionResponse(opinion.getId(), opinion.getParagraphId(), paragraphContent, opinion.getContent(), opinion.getIsReliable());
    }

    public static MyOpinionResponse from(final ReprocessedIssueOpinion opinion, final String paragraphContent) {
        return new MyOpinionResponse(opinion.getId(), opinion.getParagraphId(), paragraphContent, opinion.getContent(), opinion.getIsReliable());
    }

    public boolean getIsReliable() {
        return isReliable;
    }
}
