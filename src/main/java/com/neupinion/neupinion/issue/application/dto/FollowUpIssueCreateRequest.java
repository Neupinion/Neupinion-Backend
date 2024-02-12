package com.neupinion.neupinion.issue.application.dto;

import com.neupinion.neupinion.issue.domain.Category;
import com.neupinion.neupinion.issue.domain.FollowUpIssue;
import com.neupinion.neupinion.issue.domain.FollowUpIssueTag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "후속 이슈 생성 요청")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FollowUpIssueCreateRequest {

    @Schema(description = "후속 이슈 제목", example = "푸바오 중국 가는 날짜 확정")
    @NotBlank
    private final String title;

    @Schema(description = "후속 이슈 카테고리", example = "ENTERTAINMENT")
    @NotBlank
    private final String category;

    @Schema(description = "후속 이슈 이미지 URL", example = "https://neupinion.com/image/1")
    @NotBlank
    private final String imageUrl;

    @Schema(description = "후속 이슈 태그", example = "TRIAL_RESULTS")
    @NotBlank
    private final String tag;

    @Schema(description = "재가공 이슈 ID", example = "1")
    @Positive
    private final Long reprocessedIssueId;

    public static FollowUpIssueCreateRequest of(final String title, final String category, final String imageUrl,
                                                final String tag, final Long reprocessedIssueId) {
        return new FollowUpIssueCreateRequest(title, category, imageUrl, tag, reprocessedIssueId);
    }

    public FollowUpIssue toEntity() {
        return FollowUpIssue.forSave(title, imageUrl, Category.from(category), FollowUpIssueTag.from(tag),
                                     reprocessedIssueId);
    }
}
