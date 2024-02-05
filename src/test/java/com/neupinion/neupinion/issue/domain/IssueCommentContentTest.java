package com.neupinion.neupinion.issue.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.neupinion.neupinion.issue.exception.IssueCommentException.TooLongIssueCommentException;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class IssueCommentContentTest {

    @Test
    void 댓글의_길이가_1000자를_넘기면_예외가_발생한다() {
        // given
        final String content = "a".repeat(1001);

        // when
        // then
        assertThatThrownBy(() -> new IssueCommentContent(content))
            .isInstanceOf(TooLongIssueCommentException.class);
    }
}
