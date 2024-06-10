package com.neupinion.neupinion.issue.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
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

    @Schema(description = "재가공 이슈 썸네일 이미지 캡션", example = "이미지 1")
    private final String caption;

    @Schema(description = "재가공 이슈 원문 URL", example = "https://origin.com?origin=1234")
    @NotBlank
    private final String originUrl;

    @Schema(description = "재가공 이슈 카테고리", example = "ECONOMY")
    @NotBlank
    private final String category;

    @Schema(description = "재가공 이슈 문단들")
    private final List<ReprocessedIssueParagraphRequest> paragraphs;

    @Schema(description = "재가공 이슈 입장들")
    @NotBlank
    private final List<String> stands;

    public static ReprocessedIssueCreateRequest of(final String title, final String imageUrl, final String caption,
                                                   final String originUrl, final String category,
                                                   final List<ReprocessedIssueParagraphRequest> paragraphs,
                                                   final List<String> stands) {
        return new ReprocessedIssueCreateRequest(title, imageUrl, caption, originUrl, category, paragraphs, stands);
    }
}
