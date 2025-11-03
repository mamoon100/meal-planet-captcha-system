package com.mealplanet.captcha.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;

@Service
public class ImageGenerationService {

  private static final SecureRandom random = new SecureRandom();
  private static final Font FONT = new Font("Arial", Font.BOLD, 32);
  private static final int WIDTH = 150;
  private static final int HEIGHT = 50;

  public File generateAndSaveImage(String text, String outputDir) throws IOException {
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
    String captchaDir = System.getProperty("java.io.tmpdir") + outputDir;
    File dir = new File(outputDir);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    File outputFile = new File(outputDir, System.currentTimeMillis() + ".png");
    ImageIO.write(image, "png", outputFile);
    return outputFile;
  }



}
