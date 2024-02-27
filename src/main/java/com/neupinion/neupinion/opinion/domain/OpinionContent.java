package com.neupinion.neupinion.opinion.domain;

import com.neupinion.neupinion.opinion.exception.OpinionException.EmptyOpinionContentException;
import com.neupinion.neupinion.opinion.exception.OpinionException.TooLongOpinionContentException;
import com.neupinion.neupinion.utils.StringChecker;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class OpinionContent {

    private static final int MAXIMUM_CONTENT_LENGTH = 300;

    @Column(name = "content", length = MAXIMUM_CONTENT_LENGTH)
    private String value;

    public OpinionContent(final String value) {
        validate(value);
        this.value = value;
    }

    private void validate(final String value) {
        if (StringChecker.isNullOrBlank(value)) {
            throw new EmptyOpinionContentException();
        }
        if (value.length() > MAXIMUM_CONTENT_LENGTH) {
            throw new TooLongOpinionContentException(Map.of("OpinionContent", value));
        }
    }
}
