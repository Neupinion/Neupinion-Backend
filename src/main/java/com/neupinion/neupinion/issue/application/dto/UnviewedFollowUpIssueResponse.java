package com.neupinion.neupinion.issue.application.dto;

import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "유저가 보지 않은 후속 이슈 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UnviewedFollowUpIssueResponse {

    @Schema(description = "후속 이슈 ID", example = "1")
    private Long id;

    @Schema(description = "후속 이슈 제목", example = "제목")
    private String title;

    public static UnviewedFollowUpIssueResponse from(final FollowUpIssue followUpIssue) {
        return new UnviewedFollowUpIssueResponse(followUpIssue.getId(), followUpIssue.getTitle().getValue());
    }
}
