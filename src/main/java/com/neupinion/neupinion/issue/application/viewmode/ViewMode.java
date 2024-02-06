package com.neupinion.neupinion.issue.application.viewmode;

import com.neupinion.neupinion.issue.exception.FollowUpIssueException.InvalidViewModeException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum ViewMode {

    ALL, VOTED;

    public static ViewMode from(final String mode) {
        return Arrays.stream(values())
            .filter(viewMode -> viewMode.name().equalsIgnoreCase(mode))
            .findFirst()
            .orElseThrow(InvalidViewModeException::new);
    }
}
