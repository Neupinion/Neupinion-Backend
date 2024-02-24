package com.neupinion.neupinion.opinion.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.neupinion.neupinion.opinion.exception.OpinionException.EmptyOpinionContentException;
import com.neupinion.neupinion.opinion.exception.OpinionException.TooLongOpinionContentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@SuppressWarnings("NonAsciiCharacters")
class OpinionContentTest {

    @ParameterizedTest
    @NullAndEmptySource
    void 의견_내용이_null_또는_공백이면_예외가_발생한다(final String value) {
        // given
        // when
        // then
        assertThatThrownBy(() -> new OpinionContent(value))
            .isInstanceOf(EmptyOpinionContentException.class);
    }

    @Test
    void 의견_내용이_300자를_넘으면_예외가_발생한다() {
        // given
        final String value = "a".repeat(301);

        // when
        // then
        assertThatThrownBy(() -> new OpinionContent(value))
            .isInstanceOf(TooLongOpinionContentException.class);
    }
}
