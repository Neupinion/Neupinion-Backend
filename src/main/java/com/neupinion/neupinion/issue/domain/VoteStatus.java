package com.neupinion.neupinion.issue.domain;

import com.neupinion.neupinion.issue.exception.VoteStatusException.VoteStatusNotFoundException;
import java.util.Arrays;
import java.util.Map;
import lombok.Getter;

@Deprecated
@Getter
public enum VoteStatus {

    HIGHLY_TRUSTED("매우 신뢰", true),
    SOMEWHAT_TRUSTED("약간 신뢰", true),
    SOMEWHAT_DISTRUSTED("약간 의심", false),
    HIGHLY_DISTRUSTED("매우 의심", false),
    NOT_VOTED("미투표", false)
    ;

    private final String value;
    private final boolean isReliable;

    VoteStatus(final String value, final boolean isReliable) {
        this.value = value;
        this.isReliable = isReliable;
    }

    public static VoteStatus from(final String value) {
        return Arrays.stream(values())
            .filter(tag -> tag.name().equals(value))
            .findFirst()
            .orElseThrow(() -> new VoteStatusNotFoundException(Map.of("WrongVoteStatus", value)));
    }
}
