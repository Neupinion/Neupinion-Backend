package com.neupinion.neupinion.issue.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReprocessedIssueParagraphRequest {

    private final String content;
    private final boolean isSelectable;

    public boolean getIsSelectable() {
        return isSelectable;
    }
}
