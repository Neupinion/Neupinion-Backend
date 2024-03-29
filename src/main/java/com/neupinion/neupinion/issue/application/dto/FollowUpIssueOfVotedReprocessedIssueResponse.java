package com.neupinion.neupinion.issue.application.dto;

import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "유저가 보지 않은 후속 이슈 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FollowUpIssueOfVotedReprocessedIssueResponse {

    @Schema(description = "후속 이슈 ID", example = "1")
    private Long id;

    @Schema(description = "후속 이슈 제목", example = "제목")
    private String title;

    @Schema(description = "재가공 이슈 제목", example = "재가공 이슈 제목")
    private String reprocessedIssueTitle;

    public static FollowUpIssueOfVotedReprocessedIssueResponse of(final FollowUpIssue followUpIssue,
                                                                  final String reprocessedIssueTitle) {
        return new FollowUpIssueOfVotedReprocessedIssueResponse(followUpIssue.getId(),
                                                                followUpIssue.getTitle().getValue(),
                                                                reprocessedIssueTitle);
    }
}
