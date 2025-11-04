package com.mealplanet.captcha.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ImageGenerationUtilTest {

    private static final String OUTPUT = "captcha/output";



    @AfterEach
    void tearDown() {
        truncateOutputFolder(OUTPUT);
    }

    private void truncateOutputFolder(String folder) {
        File file = new File(folder);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }



    @Test
    void whenGenerateImage_thenReturnValidImage() throws IOException {
        File imageBytes = ImageGenerationUtil.generateAndSaveImage("test", OUTPUT);
        assertNotNull(imageBytes);
    }

    @Test
    void givenNonExistenceOutputFolderWhenGenerateAndSaveImage_thenCreateFolderAndSaveImageThere() throws IOException {
        String nonExistingFolder = OUTPUT + "/nonExistingFolder/" + System.currentTimeMillis();
        File imageBytes = ImageGenerationUtil.generateAndSaveImage("test", nonExistingFolder);
        assertNotNull(imageBytes);
        truncateOutputFolder(nonExistingFolder);
    }




}