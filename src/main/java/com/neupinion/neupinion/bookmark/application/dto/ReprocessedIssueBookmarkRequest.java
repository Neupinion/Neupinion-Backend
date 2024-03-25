package com.neupinion.neupinion.bookmark.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Schema(description = "재가공 이슈 북마크 요청")
@NoArgsConstructor
@AllArgsConstructor
public class ReprocessedIssueBookmarkRequest {

    @Schema(description = "북마크 여부", example = "true")
    @NotNull
    private boolean isBookmarked;

    public boolean getIsBookmarked() {
        return isBookmarked;
    }
}
