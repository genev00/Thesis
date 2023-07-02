package com.genev.a100nts.server.utils;

import java.util.Random;

public final class CodeGenerator {

    private static final int CODE_LENGTH;
    private static final Random RANDOM;


    static {
        CODE_LENGTH = 10;
        RANDOM = new Random();
    }

    private CodeGenerator() {
    }

    public static String generateCode() {
        return RANDOM.ints('0', 'z' + 1)
                .filter(c -> (c <= '9' || c >= 'A') && (c <= 'Z' || c >= 'a'))
                .limit(CODE_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
