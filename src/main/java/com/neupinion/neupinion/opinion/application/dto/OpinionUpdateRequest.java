package com.neupinion.neupinion.opinion.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "의견 수정 요청")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OpinionUpdateRequest {

    @Schema(description = "문단 id", example = "1")
    @Positive
    private Long paragraphId;

    @Schema(description = "의견 내용")
    @NotBlank
    private String content;

    @Schema(description = "의견 신뢰도 평가", example = "true")
    @NotNull
    private Boolean isReliable;

    public static OpinionUpdateRequest of(final Long paragraphId, final String content, final boolean isReliable) {
        return new OpinionUpdateRequest(paragraphId, content, isReliable);
    }
}
