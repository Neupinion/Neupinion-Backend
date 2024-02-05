package com.neupinion.neupinion.issue.domain;

import static com.neupinion.neupinion.issue.domain.Category.from;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.neupinion.neupinion.issue.exception.CategoryException.CategoryNotFoundException;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class CategoryTest {

    @Test
    void 존재하지_않는_카테고리_조회시_예외가_발생한다() {
        // given
        final String notExistCategory = "NOT_EXIST_CATEGORY";

        // when
        // then
        assertThatThrownBy(() -> from(notExistCategory))
            .isInstanceOf(CategoryNotFoundException.class);
    }
}
