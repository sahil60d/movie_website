import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

// Declaring a WebServlet called PaymentServlet, which maps to url "/api/payment"
@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;
    private DataSource writeDS;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
            writeDS = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/mastermoviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get the selected search keys from the request
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String ccId = request.getParameter("ccId");
        String expiration = request.getParameter("expiration");

        // Output stream to STDOUT
        //out.write("{ \"status\": \"success\" }");

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Construct a query with parameter represented by "?"
            String query = "SELECT * from creditcards where id = ? and firstName = ? and lastName = ? and expiration = ?";

            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            statement.setString(1, ccId);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, expiration);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            // Iterate through each row of rs
            if (rs.next()) {
                // customer id from custonmers table using firstname, lastname, ccId
                String query1 = "SELECT id from customers where firstName = ? and lastName = ? and ccId = ?";
                PreparedStatement statement1 = dbcon.prepareStatement(query1);
                statement1.setString(1, firstName);
                statement1.setString(2, lastName);
                statement1.setString(3, ccId);
                ResultSet rs1 = statement1.executeQuery();

                if (rs1.next()) {
                    request.getSession().setAttribute("customerId", rs1.getString("id"));
                } else {
                    throw new Exception("Failed to get customer id");
                }
                statement1.close();
                rs1.close();

                // Add each movie stored in cart array in session to the sales table
                Connection writeCon = writeDS.getConnection();

                ArrayList<String[]> cart = (ArrayList<String[]>) request.getSession().getAttribute("cart");

                for (String[] movie : cart) {
                    String query2 = "INSERT INTO sales (customerId, movieId, saleDate) VALUES (?, ?, ?)";
                    PreparedStatement statement2 = writeCon.prepareStatement(query2);
                    statement2.setString(1, request.getSession().getAttribute("customerId").toString());
                    statement2.setString(2, movie[4]);
                    statement2.setString(3, java.time.LocalDate.now().toString());
                    statement2.executeUpdate();

                    // Check for error
                    if (statement2.getUpdateCount() == 0) {
                        throw new Exception("Failed to add movie to sales table");
                    }

                    statement2.close();
                    writeCon.close();
                }

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("status", "success");
                jsonObject.addProperty("message", "success");

                // write JSON string to output
                out.write(jsonObject.toString());

            } else {
                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("status", "fail");
                jsonObject.addProperty("message", "Invalid credit card information");

                // write JSON string to output
                out.write(jsonObject.toString());
            }

            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "fail");
            jsonObject.addProperty("message", e.getMessage());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);

            // write JSON string to output
            out.write(jsonObject.toString());

        }
        out.close();
    }
}