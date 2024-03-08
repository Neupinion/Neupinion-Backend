package com.neupinion.neupinion.issue.application.dto;

import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import com.neupinion.neupinion.issue.domain.ReprocessedIssueParagraph;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "재가공 이슈 응답")
public class ReprocessedIssueResponse {

    @Schema(description = "재가공 이슈 ID", example = "1")
    private final Long id;

    @Schema(description = "재가공 이슈 제목", example = "이슈 제목")
    private final String title;

    @Schema(description = "이미지 URL", example = "https://neupinion.com/image/1")
    private final String imageUrl;

    @Schema(description = "이미지 캡션", example = "이미지 1")
    private final String caption;

    @Schema(description = "카테고리", example = "SOCIAL")
    private final String category;

    @Schema(description = "발행일", example = "2024-02-28T11:44:30.327959")
    private final LocalDateTime createdAt;

    @Schema(description = "원문 링크", example = "https://neupinion.com/origin/1")
    private final String originUrl;

    @Schema(description = "단락 리스트")
    private final List<ReprocessedIssueParagraphResponse> content;

    @Schema(description = "태그 리스트")
    private final List<String> tags;

    public static ReprocessedIssueResponse of(final ReprocessedIssue reprocessedIssue,
                                              final List<ReprocessedIssueParagraph> paragraphs,
                                              final List<String> tags) {
        final List<ReprocessedIssueParagraphResponse> content = paragraphs.stream()
            .map(ReprocessedIssueParagraphResponse::of)
            .toList();

        return new ReprocessedIssueResponse(
            reprocessedIssue.getId(),
            reprocessedIssue.getTitle(),
            reprocessedIssue.getImageUrl(),
            reprocessedIssue.getCaption(),
            reprocessedIssue.getCategory().name(),
            reprocessedIssue.getCreatedAt(),
            reprocessedIssue.getOriginUrl(),
            content,
            tags
        );
    }
}
