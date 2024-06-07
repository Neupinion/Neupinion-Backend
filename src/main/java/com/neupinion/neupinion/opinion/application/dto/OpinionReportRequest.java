package com.neupinion.neupinion.opinion.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "의견 신고 요청")
@Getter
@AllArgsConstructor
public class OpinionReportRequest {

    @Schema(description = "신고 사유", example = "욕설")
    @NotBlank
    private final String reason;

    @Schema(description = "신고 상세 내용", example = "욕설이 포함되어 있습니다.")
    private final String content;
}
