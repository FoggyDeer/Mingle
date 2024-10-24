package pjwstk.tpo_6.mingle.Filtres;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pjwstk.tpo_6.mingle.Exceptions.InvalidRefreshTokenException;
import pjwstk.tpo_6.mingle.Exceptions.RefreshTokenExpiredException;
import pjwstk.tpo_6.mingle.Services.AuthenticationService;
import pjwstk.tpo_6.mingle.Utils.CookieMap;

import java.io.IOException;

@WebFilter("/Mingle/*")
public class AuthenticationFilter extends HttpFilter {

    private static final String LOGIN_URL_PATTERN = "/Mingle/login";
    private static final String REGISTER_URL_PATTERN = "/Mingle/register";
    private static final String REFRESH_URL_PATTERN = "/Mingle/r";
    private final AuthenticationService authService = new AuthenticationService();

    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request.getRequestURI().equals("/Mingle") || request.getRequestURI().equals("/Mingle/")) {
            chain.doFilter(request, response);
            return;
        }

        boolean loginOrRegister = request.getRequestURI().startsWith(LOGIN_URL_PATTERN) || request.getRequestURI().startsWith(REGISTER_URL_PATTERN);

        CookieMap cookieMap = CookieMap.mapCookies(request.getCookies());
        String jwtToken = cookieMap.getValue("jwt");
        String refreshToken = cookieMap.getValue("rfrt");

        try {
            if (jwtToken == null || jwtToken.isEmpty()) {
                throw new MalformedJwtException("Invalid jwt token");
            }

            if (refreshToken == null || refreshToken.isEmpty())
                throw new InvalidRefreshTokenException();

            authService.validateTokens(jwtToken, refreshToken);

            if(loginOrRegister){
                if("GET".equals(request.getMethod())){
                    response.sendRedirect("/Mingle/app");
                } else if("POST".equals(request.getMethod())) {
                    response.setHeader("Location", "/Mingle/app");
                    response.sendRedirect("/Mingle/app");
                }
                return;
            }

            chain.doFilter(request, response);
        } catch (ExpiredJwtException expiredJwtException) {
            if (request.getRequestURI().startsWith(REFRESH_URL_PATTERN)) {
                chain.doFilter(request, response);
                return;
            }
            if("GET".equals(request.getMethod())){
                response.sendRedirect("/Mingle");
            } else if("POST".equals(request.getMethod())) {
                response.setHeader("AccessToken-Expired", "true");
                response.setHeader("Location", "/Mingle/r");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access-Token expired");
            }
        } catch (JwtException | InvalidRefreshTokenException | RefreshTokenExpiredException e) {
            if(loginOrRegister){
                chain.doFilter(request, response);
                return;
            }
            response.addCookie(CookieMap.newCookie("jwt", ""));
            response.addCookie(CookieMap.newCookie("rfrt", ""));
            if("GET".equals(request.getMethod())){
                response.sendRedirect("/Mingle/login");
            } else if("POST".equals(request.getMethod())) {
                response.setHeader("Location", "/Mingle/login");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access");
            }
        }
        catch (Exception e ) {
            e.printStackTrace();
        }
    }

    public void destroy() {
    }
}
