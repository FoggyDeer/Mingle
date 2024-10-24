package pjwstk.tpo_6.mingle.Servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pjwstk.tpo_6.mingle.Models.Message;
import pjwstk.tpo_6.mingle.Services.MessagesService;
import pjwstk.tpo_6.mingle.Utils.CookieMap;
import pjwstk.tpo_6.mingle.Utils.JwtToken;
import pjwstk.tpo_6.mingle.Utils.JwtUtil;

import java.io.IOException;

@WebServlet(name = "messagesServlet", urlPatterns = {"/Mingle/app/messages", "/Mingle/app/messages/"})
public class MessagesServlet extends HttpServlet {
    private final MessagesService messagesService = new MessagesService();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CookieMap cookies = CookieMap.mapCookies(req.getCookies());
        String jwt = cookies.get("jwt").getValue();

        JwtToken token = JwtUtil.parseToken(jwt);

        String result = messagesService.getMessagesFor(token.getUserId(), req.getParameter("username"), req.getParameter("minutesOffset"), req.getParameter("timeFrom"), req.getParameter("timeTo"));

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(result);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CookieMap cookies = CookieMap.mapCookies(req.getCookies());
        String jwt = cookies.get("jwt").getValue();

        JwtToken token = JwtUtil.parseToken(jwt);

        /*BufferedReader reader = req.getReader();
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }*/

        Message message = new Gson().fromJson(req.getReader(), Message.class);

        try {
            messagesService.sendMessage(token.getUserId(), message.to, message.text);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Message sent");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Bad request");
        }
    }
}