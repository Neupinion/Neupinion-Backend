package com.neupinion.neupinion.issue.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "신뢰도 투표 요청")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class TrustVoteRequest {

    @Schema(description = "신뢰도 투표 상태", example = "TRUSTED")
    @NotBlank
    private String status;
}
