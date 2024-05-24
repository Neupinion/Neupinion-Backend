package com.neupinion.neupinion.auth.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.neupinion.neupinion.auth.exception.TokenException.RefreshTokenNotFoundException;
import com.neupinion.neupinion.auth.exception.TokenException.TokenPairNotMatchingException;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class InMemoryTokenPairRepositoryTest {

    @Test
    void 리프레시_토큰과_액세스_토큰이_모두_유효하면_예외가_발생하지_않는다() {
        // given
        final InMemoryTokenPairRepository inMemoryTokenPairRepository = new InMemoryTokenPairRepository();
        inMemoryTokenPairRepository.addOrUpdateTokenPair("refreshToken", "accessToken");

        // when
        // then
        assertDoesNotThrow(() -> inMemoryTokenPairRepository.validateTokenPair("refreshToken", "accessToken"));
    }

    @Test
    void 존재하지_않는_리프레시_토큰이면_예외가_발생한다() {
        // given
        final InMemoryTokenPairRepository inMemoryTokenPairRepository = new InMemoryTokenPairRepository();
        inMemoryTokenPairRepository.addOrUpdateTokenPair("refreshToken", "accessToken");

        // when
        // then
        assertThatThrownBy(() -> inMemoryTokenPairRepository.validateTokenPair("wrongRefreshToken", "accessToken"))
            .isInstanceOf(RefreshTokenNotFoundException.class);
    }

    @Test
    void 리프레시_토큰에_해당하는_액세스_토큰이_매칭되지_않으면_예외가_발생한다() {
        // given
        final InMemoryTokenPairRepository inMemoryTokenPairRepository = new InMemoryTokenPairRepository();
        inMemoryTokenPairRepository.addOrUpdateTokenPair("refreshToken", "accessToken");

        // when
        // then
        assertThatThrownBy(() -> inMemoryTokenPairRepository.validateTokenPair("refreshToken", "wrongAccessToken"))
            .isInstanceOf(TokenPairNotMatchingException.class);
    }
}
