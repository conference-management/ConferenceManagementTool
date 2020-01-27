package utils;

import java.security.SecureRandom;
import java.util.Random;

public class Generator_Imp implements Generator {

    private final int passwordLength = 12;
    private final int tokenLength = 256;
    private final char[] acceptedChars = "ABCDEFGJKLMNPRSTUVWXYZ0123456789".toCharArray();
    private final Random random = new SecureRandom(((Long) System.currentTimeMillis()).toString().getBytes());

    @Override
    public String generatePassword() {
        return generateSecureRandomString(passwordLength);
    }

    private String generateSecureRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            sb.append(acceptedChars[random.nextInt(acceptedChars.length)]);
        }
        return sb.toString();
    }

    @Override
    public String generateToken() {
        return generateSecureRandomString(tokenLength);
    }
}
