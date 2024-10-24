package pjwstk.tpo_6.mingle.Utils;

import io.jsonwebtoken.Claims;

public class JwtToken {
    private Claims claims;
    public JwtToken(Claims claims) {
        this.claims = claims;
    }

    public String getUserName() {
        return claims.get("username", String.class);
    }

    public String getUserId() {
        return claims.getSubject();
    }
}
