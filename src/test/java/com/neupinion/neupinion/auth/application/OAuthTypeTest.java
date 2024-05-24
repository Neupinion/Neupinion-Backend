package com.neupinion.neupinion.auth.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.neupinion.neupinion.auth.exception.OAuthException.NotFoundOAuthTypeException;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class OAuthTypeTest {

    @Test
    void 존재하지_않는_타입을_입력하면_예외가_발생한다() {
        // given
        final String type = "WRONG";

        // when
        // then
        assertThatThrownBy(() -> OAuthType.from(type))
            .isInstanceOf(NotFoundOAuthTypeException.class);
    }
}
