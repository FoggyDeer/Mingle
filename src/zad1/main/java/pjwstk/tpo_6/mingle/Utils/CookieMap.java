package pjwstk.tpo_6.mingle.Utils;

import jakarta.servlet.http.Cookie;

import java.util.Arrays;
import java.util.HashSet;

public class CookieMap {
    private final HashSet<Cookie> cookies;
    private CookieMap(Cookie[] cookies) {
        this.cookies = new HashSet<>(Arrays.asList(cookies));
    }

    public static CookieMap mapCookies(Cookie[] cookies){
        if(cookies == null)
            return new CookieMap(new Cookie[0]);
        return new CookieMap(cookies);
    }

    public String getValue(String name){
        Cookie cookie = get(name);
        if(cookie != null){
            return cookie.getValue();
        }
        return null;
    }

    public Cookie get(String name){
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(name)){
                return cookie;
            }
        }
        return null;
    }

    public static Cookie newCookie(String name, String value){
        Cookie cookie =  new Cookie(name, value);
        //cookie.setHttpOnly(true);
        //cookie.setSecure(true);
        cookie.setMaxAge(60 * 60 * 24 * 7);
        cookie.setPath("/");
        return cookie;
    }
}
