package com.neupinion.neupinion.opinion.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private Long paragraphId;

    @Schema(description = "의견 내용")
    private String content;

    public static OpinionUpdateRequest of(final Long paragraphId, final String content) {
        return new OpinionUpdateRequest(paragraphId, content);
    }
}
