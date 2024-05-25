package com.neupinion.neupinion.image.application;

import com.neupinion.neupinion.image.exception.ImageException.ImageNotFoundException;
import com.neupinion.neupinion.image.exception.ImageException.ImageProcessingException;
import com.neupinion.neupinion.image.exception.ImageException.InvalidFileNameException;
import com.neupinion.neupinion.utils.StringChecker;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class ImageService {

    private static final String STATIC_IMAGES_DIR = "static/images/";

    public byte[] getImage(final String fileName) {
        validateFileName(fileName);
        final ClassPathResource classPathResource = new ClassPathResource(STATIC_IMAGES_DIR + fileName);

        try (final InputStream in = classPathResource.getInputStream();
            final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            return readImage(in, out);
        } catch (IOException e) {
            throw new ImageNotFoundException(Map.of("fileName", fileName));
        }
    }

    private void validateFileName(final String fileName) {
        if (StringChecker.isNullOrBlank(fileName)) {
            throw new InvalidFileNameException();
        }
    }

    private byte[] readImage(final InputStream in, final ByteArrayOutputStream out) {
        final byte[] buffer = new byte[1024];

        try {
            int readCount = 0;

            while ((readCount = in.read(buffer)) != -1) {
                out.write(buffer, 0, readCount);
            }

            return out.toByteArray();
        } catch (IOException e) {
            throw new ImageProcessingException();
        }
    }
}
