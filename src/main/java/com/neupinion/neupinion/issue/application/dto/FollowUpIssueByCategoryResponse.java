package com.neupinion.neupinion.issue.application.dto;

import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.repository.dto.FollowUpIssueWithReprocessedIssueTitle;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "후속 이슈 카테고리별 조회 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FollowUpIssueByCategoryResponse {

    @Schema(description = "후속 이슈 ID", example = "1")
    private final Long id;

    @Schema(description = "후속 이슈 제목", example = "후속 이슈 제목")
    private final String title;

    @Schema(description = "이슈 투표 여부", example = "true")
    private final boolean voted;

    @Schema(description = "재가공 이슈 제목", example = "재가공 이슈 제목")
    private final String reprocessedIssueTitle;

    @Schema(description = "후속 이슈 발행 일자", example = "2024-01-08T11:44:30.327959")
    private final LocalDateTime createdAt;

    public static FollowUpIssueByCategoryResponse createVotedResponse(
        final FollowUpIssueWithReprocessedIssueTitle dto) {
        final FollowUpIssue followUpIssue = dto.getFollowUpIssue();

        return new FollowUpIssueByCategoryResponse(followUpIssue.getId(),
                                                   followUpIssue.getTitle().getValue(),
                                                   false,
                                                   dto.getReprocessedIssueTitle(),
                                                   followUpIssue.getCreatedAt());
    }

    public static FollowUpIssueByCategoryResponse createNotVotedResponse(
        final FollowUpIssueWithReprocessedIssueTitle dto) {
        final FollowUpIssue followUpIssue = dto.getFollowUpIssue();

        return new FollowUpIssueByCategoryResponse(followUpIssue.getId(),
                                                   followUpIssue.getTitle().getValue(),
                                                   true,
                                                   dto.getReprocessedIssueTitle(),
                                                   followUpIssue.getCreatedAt());
    }
}
