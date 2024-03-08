package com.neupinion.neupinion.issue.application.dto;

import com.neupinion.neupinion.issue.domain.repository.dto.ReprocessedIssueWithCommentCount;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "메인 홈의 재가공 이슈 응답")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ShortReprocessedIssueResponse {

    @Schema(description = "재가공 이슈 id", example = "1")
    private final Long id;

    @Schema(description = "재가공 이슈 제목", example = "재가공 이슈 제목")
    private final String title;

    @Schema(description = "재가공 이슈 썸네일 이미지", example = "https://image.com?image=1234")
    private final String imageUrl;

    @Schema(description = "재가공 이슈 카테고리", example = "경제")
    private final String category;

    @Schema(description = "재가공 이슈의 조회수", example = "10")
    private final int views;

    @Schema(description = "공개된 포스트잇 개수", example = "10")
    private final int opinionCount;

    @Schema(description = "재가공 이슈 작성 날짜", example = "2024-01-08T11:44:30.327959")
    private final LocalDateTime createdAt;

    public static List<ShortReprocessedIssueResponse> of(final List<ReprocessedIssueWithCommentCount> reprocessedIssues) {
        return reprocessedIssues.stream()
            .map(issue -> new ShortReprocessedIssueResponse(
                issue.getReprocessedIssue().getId(),
                issue.getReprocessedIssue().getTitle(),
                issue.getReprocessedIssue().getImageUrl(),
                issue.getReprocessedIssue().getCategory().getValue(),
                issue.getReprocessedIssue().getViews(),
                issue.getCommentCount().intValue(),
                issue.getReprocessedIssue().getCreatedAt()
            ))
            .toList();
    }
}
