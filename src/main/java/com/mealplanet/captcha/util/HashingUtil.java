package com.mealplanet.captcha.util;

import com.mealplanet.captcha.exception.GeneralErrorException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HashingUtil {
  public static String hashSHA256(String value) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] encodedHash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(encodedHash);
    } catch (Exception e) {
      log.error("Error hashing value", e);
      throw new GeneralErrorException();
    }

  }
}
