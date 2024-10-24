package pjwstk.tpo_6.mingle.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pjwstk.tpo_6.mingle.Repositories.UsersRepository;
import pjwstk.tpo_6.mingle.Utils.CookieMap;
import pjwstk.tpo_6.mingle.Utils.JwtToken;
import pjwstk.tpo_6.mingle.Utils.JwtUtil;

import java.io.IOException;

@WebServlet (name = "nameServlet", urlPatterns = {"/Mingle/app/name", "/Mingle/app/name/"})
public class NameServlet extends HttpServlet {
    private final UsersRepository usersRepository = new UsersRepository();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CookieMap cookies = CookieMap.mapCookies(req.getCookies());
        JwtToken token = JwtUtil.parseToken(cookies.get("jwt").getValue());
        String name = usersRepository.getName(Integer.parseInt(token.getUserId()));
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(name);
    }
}
