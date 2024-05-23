package com.neupinion.neupinion.opinion.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Schema(description = "의견 좋아요 요청")
@AllArgsConstructor
@NoArgsConstructor
public class OpinionLikeRequest {

    @Schema(description = "좋아요 여부", example = "true")
    @NotNull
    private boolean isLiked;

    public boolean getIsLiked() {
        return isLiked;
    }
}
