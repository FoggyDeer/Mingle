package pjwstk.tpo_6.mingle.Servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pjwstk.tpo_6.mingle.Exceptions.*;
import pjwstk.tpo_6.mingle.Services.RegistrationService;
import pjwstk.tpo_6.mingle.Utils.CookieMap;

import java.io.IOException;
import java.util.HashMap;

@WebServlet(name = "registrationServlet", urlPatterns = {"/Mingle/register", "/Mingle/register/*"})
public class RegistrationServlet extends HttpServlet {
    private final RegistrationService registrationService = new RegistrationService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher view = req.getRequestDispatcher("/html/registration.html");
        view.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(req.getRequestURI().endsWith("/userId")){
            isUsernameValid(req, resp);
            return;
        }

        String username = req.getParameter("username");
        String name = req.getParameter("name");
        String password = req.getParameter("password");

        if(!isUsernameValid(req, resp)) return;

        try {
            HashMap<String, String> tokens = registrationService.registerUser(username, name, password);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.addCookie(CookieMap.newCookie("jwt", tokens.get("jwt")));
            resp.addCookie(CookieMap.newCookie("rfrt", tokens.get("rfrt")));
            resp.getWriter().println("User registered successfully");
        } catch (InvalidUserIdException | BadRequestError e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user id");
        } catch (InvalidUsernameException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid username");
        }
    }

    public boolean isUsernameValid(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        try{
            registrationService.validateUsername(req.getParameter("username"));
        }catch (UsernameAlreadyExistsException e){
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Username already exists");
            return false;
        } catch (InvalidUsernameException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid username");
            return false;
        }
        return true;
    }
}
