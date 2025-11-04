package com.mealplanet.captcha.util;

import com.mealplanet.captcha.exception.GeneralErrorException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashingUtilTest {


    @Test
    void whenHashingNullString_thenReturnNull() {
        assertThrows(GeneralErrorException.class, () -> HashingUtil.hashSHA256(null));
    }

    @Test
    void whenHashingString_thenReturnHashedString() {
        String hashedString = HashingUtil.hashSHA256("test");
        assertNotNull(hashedString);
    }


}