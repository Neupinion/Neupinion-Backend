package com.neupinion.neupinion.opinion.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "재가공 이슈 의견 생성 요청")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReprocessedIssueOpinionCreateRequest {

    @Schema(description = "단락 ID", example = "1")
    private final Long paragraphId;

    @Schema(description = "재가공 이슈 ID", example = "1")
    private final Long reprocessedIssueId;

    @Schema(description = "의견 내용", example = "이 부분은 잘못된 정보 같네요.")
    private final String content;

    public static ReprocessedIssueOpinionCreateRequest of(final Long paragraphId, final Long reprocessedIssueId,
                                                          final String content) {
        return new ReprocessedIssueOpinionCreateRequest(paragraphId, reprocessedIssueId, content);
    }
}
