package com.neupinion.neupinion.issue.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(name = "재가공 이슈 생성 요청")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReprocessedIssueCreateRequest {

    @Schema(description = "재가공 이슈 제목", example = "재가공 이슈 제목")
    @NotBlank
    private final String title;

    @Schema(description = "재가공 이슈 썸네일 이미지", example = "https://image.com?image=1234")
    @NotBlank
    private final String imageUrl;

    @Schema(description = "재가공 이슈 카테고리", example = "ECONOMY")
    @NotBlank
    private final String category;

    @Schema(description = "재가공 이슈가 속한 이슈", example = "1")
    @Positive
    private final Long issueId;

    public static ReprocessedIssueCreateRequest of(final String title, final String imageUrl, final String category,
                                                   final Long issueId) {
        return new ReprocessedIssueCreateRequest(title, imageUrl, category, issueId);
    }
}
