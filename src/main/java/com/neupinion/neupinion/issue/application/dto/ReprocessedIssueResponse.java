package com.neupinion.neupinion.issue.application.dto;

import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "재가공 이슈 응답")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReprocessedIssueResponse {

    @Schema(description = "재가공 이슈 id", example = "1")
    private final Long id;

    @Schema(description = "재가공 이슈 제목", example = "재가공 이슈 제목")
    private final String title;

    @Schema(description = "재가공 이슈 썸네일 이미지", example = "https://image.com?image=1234")
    private final String imageUrl;

    @Schema(description = "재가공 이슈 카테고리", example = "ECONOMY")
    private final String category;

    @Schema(description = "재가공 이슈의 조회수", example = "10")
    private final int views;

    @Schema(description = "재가공 이슈 작성 날짜", example = "2024-01-08T11:44:30.327959")
    private final LocalDateTime createdAt;

    public static List<ReprocessedIssueResponse> of(final List<ReprocessedIssue> reprocessedIssues) {
        return reprocessedIssues.stream()
            .map(issue -> new ReprocessedIssueResponse(
                issue.getId(),
                issue.getTitle().getValue(),
                issue.getImageUrl(),
                issue.getCategory().getValue(),
                issue.getViews(),
                issue.getCreatedAt()
            ))
            .toList();
    }
}
