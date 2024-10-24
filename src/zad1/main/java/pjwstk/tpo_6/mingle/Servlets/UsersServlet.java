package pjwstk.tpo_6.mingle.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pjwstk.tpo_6.mingle.Services.UsersService;
import pjwstk.tpo_6.mingle.Utils.CookieMap;
import pjwstk.tpo_6.mingle.Utils.JwtToken;
import pjwstk.tpo_6.mingle.Utils.JwtUtil;

import java.io.IOException;


@WebServlet(name = "usersServlet", urlPatterns = {"/Mingle/app/users", "/Mingle/app/users/"})
public class UsersServlet extends HttpServlet {
    private final UsersService usersService = new UsersService();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pattern = req.getParameter("pattern");
        CookieMap cookies = CookieMap.mapCookies(req.getCookies());
        JwtToken token = JwtUtil.parseToken(cookies.get("jwt").getValue());
        String result = usersService.findUsersByPattern(token.getUserId(), pattern);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(result);
    }
}
