package com.neupinion.neupinion.issue.application.dto;

import com.neupinion.neupinion.issue.domain.ReprocessedIssue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "최근 재가공 이슈 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RecentReprocessedIssueByCategoryResponse {

    @Schema(description = "재가공 이슈 ID", example = "1")
    private final Long id;

    @Schema(description = "재가공 이슈 제목", example = "이슈 제목")
    private final String title;

    @Schema(description = "재가공 이슈 이미지 URL", example = "https://neupinion.com/image/1")
    private final String imageUrl;

    @Schema(description = "재가공 이슈 발행일", example = "2024-03-18T11:44:30.327959")
    private final LocalDateTime createdAt;

    public static RecentReprocessedIssueByCategoryResponse of(final ReprocessedIssue reprocessedIssue) {
        return new RecentReprocessedIssueByCategoryResponse(
            reprocessedIssue.getId(),
            reprocessedIssue.getTitle(),
            reprocessedIssue.getImageUrl(),
            reprocessedIssue.getCreatedAt()
        );
    }
}
