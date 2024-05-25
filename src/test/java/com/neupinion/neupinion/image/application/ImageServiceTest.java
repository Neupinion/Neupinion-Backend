package com.neupinion.neupinion.image.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class ImageServiceTest {

    @Test
    void 기본_이미지를_가져온다() {
        // given
        final ImageService imageService = new ImageService();
        final String fileName = "default-profile.png";

        // when
        // then
        Assertions.assertDoesNotThrow(() -> imageService.getImage(fileName));
    }
}
