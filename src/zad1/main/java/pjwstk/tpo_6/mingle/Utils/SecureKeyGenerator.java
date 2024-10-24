package pjwstk.tpo_6.mingle.Utils;

import java.security.SecureRandom;
import java.util.Base64;

public class SecureKeyGenerator {
    public static String generateKey(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(new String(bytes).getBytes());
    }
}
