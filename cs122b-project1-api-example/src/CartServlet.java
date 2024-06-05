import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

/**
 * This CartServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/cart.
 */
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        ArrayList<String[]> cart = (ArrayList<String[]>) session.getAttribute("cart");

        JsonArray cartJsonArray = new JsonArray();
        if (cart != null) {
            for (String[] movieInfo : cart) {
                JsonObject movieJson = new JsonObject();
                movieJson.addProperty("title", movieInfo[0]);
                movieJson.addProperty("price", movieInfo[1]);
                movieJson.addProperty("quantity", movieInfo[2]);
                movieJson.addProperty("total", movieInfo[3]);
                cartJsonArray.add(movieJson);
            }
        }

        //JsonObject responseJsonObject = new JsonObject();
        //responseJsonObject.add("cart", cartJsonArray);
        response.getWriter().write(cartJsonArray.toString());
    }

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movieId = request.getParameter("movieId");
        HttpSession session = request.getSession();

        // Retrive the movie title from the movieId
        String query = "SELECT title FROM movies WHERE id = ?";

        String title= "";

        try {
            // Connect to the database
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, movieId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                title = rs.getString("title");
            }
            rs.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "fail to get movie title");
            response.getWriter().write(responseJsonObject.toString());
            return;
        }

        String price = String.valueOf((int)(Math.random() * 41) + 10);
        int quantity = 1;
        int total = quantity * Integer.parseInt(price);
        String[] movieInfo = {title, price, String.valueOf(quantity), String.valueOf(total), movieId};

        // get the previous items in a ArrayList
        ArrayList<String[]> cart = (ArrayList<String[]>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<String[]>();
            session.setAttribute("cart", cart);
        }

        // set changeQuantity to true if the changeQuantity button was pressed
        String titleToChange = request.getParameter("title");
        String change = request.getParameter("change");

        // check if the item is already in the cart
        // if it is, check if changeQuantity button was pressed. If not, update the quantity
        // if not, add the item to the cart
        boolean found = false;
        for (String[] movie : cart) {
            if (movie[0].equals(title) || movie[0].equals(titleToChange)) {

                if (change != null && change.equals("-")) {     // Decrement quantity
                    movie[2] = String.valueOf(Integer.parseInt(movie[2]) - 1);
                    movie[3] = String.valueOf(Integer.parseInt(movie[1]) * Integer.parseInt(movie[2]));

                    // if quantity is 0, remove the item from the cart
                    if (movie[2].equals("0")) {
                        cart.remove(movie);
                    }
                    found = true;
                    break;

                } else if (change != null && change.equals("delete")) {  // Remove item from cart
                    cart.remove(movie);
                    found = true;
                    break;

                } else {    // Increment quantity
                    movie[2] = String.valueOf(Integer.parseInt(movie[2]) + 1);
                    movie[3] = String.valueOf(Integer.parseInt(movie[1]) * Integer.parseInt(movie[2]));
                    found = true;
                    break;
                }
            }
        }
        if (!found)
            cart.add(movieInfo);

        // Log to localhost log
        request.getServletContext().log("adding " + title + " to the session");

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("status", "success");
        responseJsonObject.addProperty("message", "success");

        response.getWriter().write(responseJsonObject.toString());
    }
}