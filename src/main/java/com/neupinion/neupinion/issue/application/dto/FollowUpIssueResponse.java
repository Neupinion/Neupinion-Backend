package com.neupinion.neupinion.issue.application.dto;

import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "후속 이슈 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FollowUpIssueResponse {

    @Schema(description = "후속 이슈 ID", example = "1")
    private final Long id;

    @Schema(description = "후속 이슈 제목", example = "이슈 제목")
    private final String title;

    @Schema(description = "후속 이슈 썸네일 이미지 url", example = "https://neupinion.com/images?value=image")
    private final String imageUrl;

    @Schema(description = "후속 이슈 카테고리", example = "국제")
    private final String category;

    @Schema(description = "후속 이슈 작성 날짜", example = "2024-02-12T11:44:30.327959")
    private final LocalDateTime createdAt;

    public static FollowUpIssueResponse from(final FollowUpIssue followUpIssue) {
        return new FollowUpIssueResponse(
            followUpIssue.getId(),
            followUpIssue.getTitle().getValue(),
            followUpIssue.getImageUrl(),
            followUpIssue.getCategory().getValue(),
            followUpIssue.getCreatedAt()
        );
    }
}
