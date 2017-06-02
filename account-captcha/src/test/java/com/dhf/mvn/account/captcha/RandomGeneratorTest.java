package com.dhf.mvn.account.captcha;

import com.dhf.mvn.account.captcha.service.RandomGenerator;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dhf on 2017/6/2.
 */
public class RandomGeneratorTest {
    @Test
    public void testGetRandomString() {
        Set<String> randoms = new HashSet<>();
        for(int i = 0; i < 100; i++) {
            String random = RandomGenerator.getRandomString();
            assertFalse(randoms.contains(random));
            randoms.add(random);
        }
    }
}
