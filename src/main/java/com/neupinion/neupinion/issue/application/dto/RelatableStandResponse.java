package com.neupinion.neupinion.issue.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(name = "이슈 입장 공감 응답")
@Getter
@AllArgsConstructor
public class RelatableStandResponse {

    @Schema(description = "이슈 입장 ID", example = "1")
    private final Long id;

    @Schema(description = "이슈 입장", example = "민희진")
    private final String stand;

    @Schema(description = "입장 공감 여부", example = "true")
    private final boolean relatable;

    public boolean getRelatable() {
        return relatable;
    }
}
