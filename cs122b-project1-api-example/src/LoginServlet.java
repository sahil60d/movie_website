import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")

public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    private UserDAO userDAO;

    public void init(ServletConfig config) {
        try {
            DataSource dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
            userDAO = new UserDAO(dataSource);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Verify reCAPTCHA
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "reCAPTCHA verification error");
            response.getWriter().write(responseJsonObject.toString());
            return;
        }

        // Verify user login
        String username = request.getParameter("username");
        String password = request.getParameter("password");


        JsonObject responseJsonObject = new JsonObject();

        /*
        User user = userDAO.findUser(username, password);

        if (user != null) {
            // Login success:

            // set this user into the session
            request.getSession().setAttribute("user", user);

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");

        } else {
            // Login fail
            responseJsonObject.addProperty("status", "fail");
            // Log to localhost log
            request.getServletContext().log("Login failed");
            // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
            if (UserDAO.findUser(username, null) == null) {
                responseJsonObject.addProperty("message", "Invalid username or password");
            }
        }

         */

        try {
            if (userDAO.verifyCredentials(username, password)) {
                // Login success:

                // set this user into the session
                request.getSession().setAttribute("user", new User(username));

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
            } else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
                if (userDAO.findUser(username, null) == null) {
                    responseJsonObject.addProperty("message", "Invalid username or password");
                }
            }

            response.getWriter().write(responseJsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}