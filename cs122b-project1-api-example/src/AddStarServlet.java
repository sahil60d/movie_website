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


// Declaring a WebServlet called AddStarServlet, which maps to url "/api/add-star"
@WebServlet(name = "AddStarServlet", urlPatterns = "/api/add-star")
public class AddStarServlet extends HttpServlet {

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
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get the selected search keys from the request
        String name = request.getParameter("name");
        String birthYear = request.getParameter("birthYear");

        // Output stream to STDOUT
        //out.write("name: " + name + ", birthYear: " + birthYear);

        try (Connection connection = dataSource.getConnection()) {

            String query = "SELECT max(id) as maxId from stars";

            PreparedStatement statement = connection.prepareStatement(query);

            // Generate the new star id
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            String mId = rs.getString("maxId");
            int maxId = Integer.parseInt(mId.substring(2));
            maxId++;
            String newId = "nm" + String.format("%07d", maxId);

            // Add the new star to the stars table
            query = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newId);
            preparedStatement.setString(2, name);
            //preparedStatement.setInt(3, Integer.parseInt(birthYear));
            if (birthYear != null && !birthYear.isEmpty()) {
                preparedStatement.setInt(3, Integer.parseInt(birthYear));
            } else {
                preparedStatement.setNull(3, java.sql.Types.INTEGER);
            }
            preparedStatement.executeUpdate();


            // Get the new star's information
            query = "SELECT * from stars where id = '" + newId + "'";
            PreparedStatement ps = connection.prepareStatement(query);
            rs = ps.executeQuery(query);
            rs.next();

            // Create a JsonObject based on the data we retrieve from stars
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", rs.getString("id"));


            // After successfully adding the star, construct a JSON response
            jsonObject.addProperty("status", "success");
            jsonObject.addProperty("message", "Star added successfully.");
            jsonObject.addProperty("star_id", newId); // Include the new star ID in the response

            out.write(jsonObject.toString());
            response.setStatus(200);

            rs.close();
            statement.close();
            preparedStatement.close();
            ps.close();
            connection.close();
        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();

        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }
}
