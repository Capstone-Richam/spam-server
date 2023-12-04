package com.Nunbody.global.common;
import java.util.Base64;
public class EncoderDecoder {
    public static String encodeToBase64(String input) {
        byte[] encodedBytes = Base64.getEncoder().encode(input.getBytes());
        return new String(encodedBytes);
    }

    // Base64로 인코딩된 문자열을 디코딩하는 함수
    public static String decodeFromBase64(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString.getBytes());
        return new String(decodedBytes);
    }
}
