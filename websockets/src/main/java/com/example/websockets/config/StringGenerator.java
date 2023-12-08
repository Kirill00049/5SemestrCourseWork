package com.example.websockets.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringGenerator {
    public static String generateLongString(String username, String password, Integer len) {
        try {
            // Объединяем username и password для создания уникального сида
            String seed = username + ":" + password;
            // Используем SHA-256 для генерации хеша
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(seed.getBytes(StandardCharsets.UTF_8));

            // Конвертируем байты в шестнадцатеричный формат
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            // Если нужна строка большей длины, можно повторять хеш несколько раз
            String longString = hexString.toString();
            while (longString.length() < len) { // Допустим, нам нужна строка длиной 1000 символов
                longString += hexString;
            }

            return longString.substring(0, len); // Возвращаем подстроку нужной длины
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not generate hash", e);
        }
    }
}
