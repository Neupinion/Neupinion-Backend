package com.neupinion.neupinion.issue.application.dto;

import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.IssueType;
import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(name = "관련 이슈 응답")
@AllArgsConstructor
@Getter
public class RelatedIssueResponse {

    @Schema(description = "관련 이슈 타입", example = "REPROCESSED")
    private String issueType;

    @Schema(description = "관련 이슈 ID", example = "1")
    private final Long id;

    @Schema(description = "관련 이슈 제목", example = "이슈 제목")
    private final String title;

    @Schema(description = "관련 이슈 썸네일 이미지 url", example = "https://neupinion.com/image.jpg")
    private String imageUrl;

    @Schema(description = "관련 이슈 카테고리", example = "국제")
    private String category;

    @Schema(description = "관련 이슈 발행 일자", example = "2024-04-25T00:00:00")
    private LocalDateTime createdAt;

    public static RelatedIssueResponse from(final ReprocessedIssue reprocessedIssue) {
        return new RelatedIssueResponse(IssueType.REPROCESSED.name(), reprocessedIssue.getId(),
                                        reprocessedIssue.getTitle(), reprocessedIssue.getImageUrl(),
                                        reprocessedIssue.getCategory().getValue(), reprocessedIssue.getCreatedAt());
    }

    public static RelatedIssueResponse from(final FollowUpIssue followUpIssue) {
        return new RelatedIssueResponse(IssueType.FOLLOW_UP.name(), followUpIssue.getId(),
                                        followUpIssue.getTitle().getValue(), followUpIssue.getImageUrl(),
                                        followUpIssue.getCategory().getValue(), followUpIssue.getCreatedAt());
    }
}
