package com.neupinion.neupinion.issue.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.neupinion.neupinion.issue.exception.IssueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@SuppressWarnings("NonAsciiCharacters")
class IssueTitleTest {

    @NullAndEmptySource
    @ParameterizedTest
    void 이슈_제목이_빈칸이거나_null인_경우_예외가_발생한다(final String value) {
        // given
        // when
        // then
        assertThatThrownBy(() -> new IssueTitle(value))
            .isInstanceOf(IssueException.NullOrEmptyTitleException.class);
    }

    @Test
    void 이슈_제목이_너무_긴_경우_예외가_발생한다() {
        // given
        final String 매우_긴_제목 = "-".repeat(101);

        // when
        // then
        assertThatThrownBy(() -> new IssueTitle(매우_긴_제목))
            .isInstanceOf(IssueException.TooLongIssueTitleException.class);
    }
}
