package pjwstk.tpo_6.mingle.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pjwstk.tpo_6.mingle.Services.ChatsService;
import pjwstk.tpo_6.mingle.Utils.CookieMap;
import pjwstk.tpo_6.mingle.Utils.JwtToken;
import pjwstk.tpo_6.mingle.Utils.JwtUtil;

import java.io.IOException;

@WebServlet(name = "chatsServlet", urlPatterns = {"/Mingle/app/chats", "/Mingle/app/chats/"})
public class ChatsServlet extends HttpServlet {
    private final ChatsService chatsService = new ChatsService();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CookieMap cookies = CookieMap.mapCookies(req.getCookies());
        String jwt = cookies.get("jwt").getValue();

        JwtToken token = JwtUtil.parseToken(jwt);

        String result = chatsService.getChatsFor(token.getUserId(), req.getParameter("minutesOffset"));

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(result);
    }
}
