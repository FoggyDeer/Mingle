package pjwstk.tpo_6.mingle.Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pjwstk.tpo_6.mingle.Utils.CookieMap;

import java.io.IOException;

@WebServlet(name = "logoutServlet", value = "/Mingle/app/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addCookie(CookieMap.newCookie("jwt", ""));
        resp.addCookie(CookieMap.newCookie("rfrt", ""));
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().println("Logged out");
    }
}
