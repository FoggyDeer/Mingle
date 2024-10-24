package pjwstk.tpo_6.mingle.Servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pjwstk.tpo_6.mingle.Exceptions.InternalServerErrorException;
import pjwstk.tpo_6.mingle.Exceptions.InvalidRefreshTokenException;
import pjwstk.tpo_6.mingle.Exceptions.InvalidUserIdException;
import pjwstk.tpo_6.mingle.Exceptions.RefreshTokenExpiredException;
import pjwstk.tpo_6.mingle.Services.AuthenticationService;
import pjwstk.tpo_6.mingle.Utils.CookieMap;

import java.io.IOException;
import java.util.HashMap;

@WebServlet(name = "refreshTokenServlet", value = "/Mingle/r")
public class RefreshTokenServlet extends HttpServlet {
    private final AuthenticationService authService = new AuthenticationService();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CookieMap cookieMap = CookieMap.mapCookies(req.getCookies());
        String jwtToken = cookieMap.getValue("jwt");
        String refreshToken = cookieMap.getValue("rfrt");
        try {
            HashMap<String, String> tokens = authService.updateTokens(jwtToken, refreshToken);

            resp.setStatus(HttpServletResponse.SC_OK);

            System.out.println("Tokens has been refreshed");
            resp.addCookie(CookieMap.newCookie("jwt", tokens.get("jwt")));
            resp.addCookie(CookieMap.newCookie("rfrt", tokens.get("rfrt")));
            resp.getWriter().println("Tokens has been refreshed");
        } catch (InvalidRefreshTokenException | RefreshTokenExpiredException | InvalidUserIdException e) {

            resp.addCookie(CookieMap.newCookie("jwt", ""));
            resp.addCookie(CookieMap.newCookie("rfrt", ""));
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("Refresh token is invalid or expired");
        } catch (InternalServerErrorException | RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Internal server error");
        }
    }
}
