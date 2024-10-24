package pjwstk.tpo_6.mingle.Utils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    private static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public static String generateToken(String userId, String username) {
        return Jwts.builder()
                .setSubject(userId)
                .setHeaderParam("type", "JWT")
                .claim("username", username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 1000)) // + 60 * 60 * 1000
                .signWith(secretKey)
                .compact();
    }

    public static JwtToken parseToken(String token){
        return new JwtToken(Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody());
    }
}
