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
import java.sql.*;

// Declaring a WebServlet called AddMovieServlet, which maps to url "/api/add-movie"
@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/add-movie")
public class AddMovieServlet extends HttpServlet {

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        String movieTitle = request.getParameter("movieTitle");
        int movieYear = Integer.parseInt(request.getParameter("movieYear"));
        String movieDirector = request.getParameter("movieDirector");
        String starName = request.getParameter("starName");
        String genreName = request.getParameter("genreName");

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement statement = conn.prepareStatement("CALL add_movie(?, ?, ?, ?, ?)");
            statement.setString(1, movieTitle);
            statement.setInt(2, movieYear);
            statement.setString(3, movieDirector);
            statement.setString(4, starName);
            statement.setString(5, genreName);

            statement.execute();

            // Get new movie info
            String query = "SELECT * FROM movies WHERE title = ? AND year = ? AND director = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, movieTitle);
            ps.setInt(2, movieYear);
            ps.setString(3, movieDirector);
            ResultSet rs = ps.executeQuery();
            rs.next();

            // Create a JsonObject based on the data we retrieve from the database, get the movie id, genre id, and star id
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("movieId", rs.getString("id"));

            String genreQuery = "SELECT id FROM genres WHERE name = ?";
            PreparedStatement genreStatement = conn.prepareStatement(genreQuery);
            genreStatement.setString(1, genreName);
            ResultSet genreRs = genreStatement.executeQuery();
            genreRs.next();
            responseJsonObject.addProperty("genreId", genreRs.getString("id"));

            String starQuery = "SELECT id FROM stars WHERE name = ?";
            PreparedStatement starStatement = conn.prepareStatement(starQuery);
            starStatement.setString(1, starName);
            ResultSet starRs = starStatement.executeQuery();
            starRs.next();
            responseJsonObject.addProperty("starId", starRs.getString("id"));

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "Movie added successfully");
            out.write(responseJsonObject.toString());
            response.setStatus(200);

            statement.close();
            conn.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Movie could not be added");
            out.write(responseJsonObject.toString());
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}