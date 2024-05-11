package com.neupinion.neupinion.issue.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "타임라인 이슈 응답")
@AllArgsConstructor
@Getter
public class TimelineResponse {

    @Schema(description = "이슈 종류", example = "REPROCESSED")
    private final String issueType;

    @Schema(description = "이슈 ID", example = "1")
    private final Long id;

    @Schema(description = "이슈 제목", example = "코로나 백신 접종")
    private final String title;

    @Schema(description = "이슈가 작성된 시간", example = "2024-05-11T00:00:00")
    private final LocalDateTime createdAt;
}
