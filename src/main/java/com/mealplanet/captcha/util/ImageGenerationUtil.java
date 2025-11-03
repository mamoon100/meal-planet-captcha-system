package com.mealplanet.captcha.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageGenerationUtil {

  private static final Font FONT = new Font("Arial", Font.BOLD, 32);
  private static final int WIDTH = 150;
  private static final int HEIGHT = 50;

  public static File generateAndSaveImage(String text, String outputDir) throws IOException {
    BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = image.createGraphics();
    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, WIDTH, HEIGHT);
    g2d.setColor(new Color(220, 220, 220));
    g2d.setFont(FONT);
    g2d.setColor(Color.BLACK);
    FontMetrics fm = g2d.getFontMetrics();
    int textWidth = fm.stringWidth(text);
    int x = (WIDTH - textWidth) / 2;
    int y = (HEIGHT - fm.getHeight()) / 2 + fm.getAscent();
    g2d.drawString(text, x, y);
    g2d.dispose();
    File dir = new File(outputDir);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    File outputFile = new File(outputDir, System.currentTimeMillis() + ".png");
    ImageIO.write(image, "png", outputFile);
    return outputFile;
  }



}
