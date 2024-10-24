package pjwstk.tpo_6.mingle.Servlets;

import java.io.IOException;
import java.util.HashMap;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import pjwstk.tpo_6.mingle.Exceptions.*;
import pjwstk.tpo_6.mingle.Services.AuthenticationService;
import pjwstk.tpo_6.mingle.Utils.CookieMap;

@WebServlet(name = "loginServlet", urlPatterns = {"/Mingle/login", "/Mingle/login/"})
public class LoginServlet extends HttpServlet {
    private final AuthenticationService authService = new AuthenticationService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher view = req.getRequestDispatcher("/html/login.html");
        view.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HashMap<String, String> tokens = authService.login(req.getParameter("username"), req.getParameter("password"));

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.addCookie(CookieMap.newCookie("jwt", tokens.get("jwt")));
            resp.addCookie(CookieMap.newCookie("rfrt", tokens.get("rfrt")));
            resp.getWriter().println("User logged in successfully");
        } catch (InvalidPasswordException | InvalidUsernameException e) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid username or password");
        } catch (NonExistentAccountException e) {
            //resp.setHeader("Non-Existent-Account", "true");
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid username or password");
        } catch (InvalidRefreshTokenException | RefreshTokenExpiredException | InvalidUserIdException |
                 InternalServerErrorException e) {
            throw new RuntimeException(e);
        }
    }
}