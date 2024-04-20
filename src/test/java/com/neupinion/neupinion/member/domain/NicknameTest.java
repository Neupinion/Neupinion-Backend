package com.neupinion.neupinion.member.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.neupinion.neupinion.member.exception.MemberException;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class NicknameTest {

    @Test
    void 닉네임이_30자를_넘으면_예외가_발생한다() {
        // given
        final String wrongNicknameValue = "-".repeat(31);

        // when
        // then
        assertThatThrownBy(() -> new Nickname(wrongNicknameValue))
            .isInstanceOf(MemberException.NicknameLengthExceededException.class);
    }
}
