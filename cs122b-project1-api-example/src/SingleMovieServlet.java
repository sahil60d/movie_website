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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 4L;

    // Create a dataSource which registered in web.xml
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

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            //String query = "SELECT m.id, m.title, m.year, m.director, r.rating from movies m, ratings r where m.id = r.movieId and m.id = ?";
            String query = "SELECT m.id, m.title, m.year, m.director, r.rating from movies m LEFT JOIN ratings r ON m.id = r.movieId where m.id = ?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_rating = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_rating", movie_rating);

                // Create a JsonArray to store the genres of the movie
                JsonArray genresArray = new JsonArray();

                // Construct a query to get the genres of the movie
                String genresQuery = "SELECT * from genres_in_movies as gim, genres as g " +
                        "where gim.genreId = g.id and gim.movieId = ?";

                // Declare our statement
                PreparedStatement genresStatement = conn.prepareStatement(genresQuery);
                genresStatement.setString(1, movie_id);

                // Perform the query
                ResultSet genresRS = genresStatement.executeQuery();

                // Iterate through each row of genresRS
                while (genresRS.next()) {
                    String genre_name = genresRS.getString("name");

                    // Create a JsonObject based on the data we retrieve from genresRS
                    JsonObject genreObject = new JsonObject();
                    genreObject.addProperty("genre_name", genre_name);

                    genresArray.add(genreObject);
                }

                jsonObject.add("movie_genres", genresArray);

                genresRS.close();
                genresStatement.close();

                // Create a JsonArray to store the stars of the movie
                JsonArray starsArray = new JsonArray();

                String starQuery = "SELECT * from stars as s, stars_in_movies as sim where s.id = sim.starId and sim.movieId = ?";

                PreparedStatement starStatement = conn.prepareStatement(starQuery);
                starStatement.setString(1, movie_id);

                ResultSet starsRS = starStatement.executeQuery();

                while (starsRS.next()) {
                    String star_id = starsRS.getString("id");
                    String star_name = starsRS.getString("name");

                    JsonObject starObject = new JsonObject();
                    starObject.addProperty("star_id", star_id);
                    starObject.addProperty("star_name", star_name);

                    starsArray.add(starObject);
                }

                jsonObject.add("movie_stars", starsArray);

                starsRS.close();
                starStatement.close();

                jsonArray.add(jsonObject);
            }

            rs.close();
            statement.close();

            // Lot to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}