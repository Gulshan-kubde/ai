package com.example.ai.util;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class Base64Utils {

    public static String toBase64(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        return Base64.getEncoder().encodeToString(data);

    }
}
