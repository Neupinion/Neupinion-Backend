package com.neupinion.neupinion.issue.application.dto;

import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "해당 재가공 이슈의 후속 이슈 응답")
@Getter
@AllArgsConstructor
public class ShortFollowUpIssueResponse {

    @Schema(description = "후속 이슈 ID", example = "1")
    private final Long id;

    @Schema(description = "후속 이슈 제목", example = "후속 이슈 제목")
    private final String title;

    @Schema(description = "후속 이슈 카테고리", example = "국제")
    private final String category;

    @Schema(description = "후속 이슈 생성 날짜", example = "2024-04-25T14:00:00")
    private final LocalDateTime createdAt;

    public static ShortFollowUpIssueResponse from(final FollowUpIssue followUpIssue) {
        return new ShortFollowUpIssueResponse(
            followUpIssue.getId(),
            followUpIssue.getTitle().getValue(),
            followUpIssue.getCategory().getValue(),
            followUpIssue.getCreatedAt()
        );
    }
}
