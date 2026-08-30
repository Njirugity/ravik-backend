package net.ravik_cms.ravik_backend.common.utils;

import java.security.SecureRandom;
import java.util.function.Predicate;

public final class ReferenceCodeGenerator {
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SUFFIX_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private ReferenceCodeGenerator() {
    }

    public static String generate(String prefix, Predicate<String> exists) {
        String code;
        do {
            code = prefix + "-" + randomSuffix();
        } while (exists.test(code));
        return code;
    }

    private static String randomSuffix() {
        StringBuilder sb = new StringBuilder(SUFFIX_LENGTH);
        for (int i = 0; i < SUFFIX_LENGTH; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }
}
