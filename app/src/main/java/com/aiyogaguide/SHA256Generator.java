package com.aiyogaguide;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Generator {
    public static String generateSHA256(String text) {
        try {
            // Create a SHA-256 message digest
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Update the message digest with the input text
            byte[] hash = digest.digest(text.getBytes());

            // Convert the byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
