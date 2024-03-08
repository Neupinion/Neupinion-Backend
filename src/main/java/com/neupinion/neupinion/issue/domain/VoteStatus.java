package com.neupinion.neupinion.issue.domain;

import com.neupinion.neupinion.issue.exception.VoteStatusException.VoteStatusNotFoundException;
import java.util.Arrays;
import java.util.Map;
import lombok.Getter;

@Getter
public enum VoteStatus {

    HIGHLY_TRUSTED("매우 신뢰"),
    SOMEWHAT_TRUSTED("약간 신뢰"),
    SOMEWHAT_DISTRUSTED("약간 의심"),
    HIGHLY_DISTRUSTED("매우 의심"),
    NOT_VOTED("미투표")
    ;

    private final String value;

    VoteStatus(final String value) {
        this.value = value;
    }

    public static VoteStatus from(final String value) {
        return Arrays.stream(values())
            .filter(tag -> tag.name().equals(value))
            .findFirst()
            .orElseThrow(() -> new VoteStatusNotFoundException(Map.of("WrongVoteStatus", value)));
    }
}
