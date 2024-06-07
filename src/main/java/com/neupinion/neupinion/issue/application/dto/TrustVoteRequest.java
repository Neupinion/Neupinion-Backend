package com.neupinion.neupinion.issue.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "입장 투표 요청")
@Getter
@AllArgsConstructor
public class TrustVoteRequest {

    @Schema(description = "첫 번째 입장 id", example = "1")
    @Positive
    private Long firstStandId;

    @Schema(description = "첫 번째 입장 공감 여부", example = "true")
    @NotNull
    private boolean firstRelatable;

    @Schema(description = "두 번째 입장 id", example = "2")
    @Positive
    private Long secondStandId;

    @Schema(description = "두 번째 입장 공감 여부", example = "false")
    @NotNull
    private boolean secondRelatable;

    public boolean getFirstRelatable() {
        return firstRelatable;
    }

    public boolean getSecondRelatable() {
        return secondRelatable;
    }
}
