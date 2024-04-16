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
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called MoviesServlet, which maps to url "/api/movies"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

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

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();

            String query = "SELECT m.id, m.title, m.year, m.director, r.rating from movies m, ratings r where m.id = r.movieId order by r.rating desc limit 20";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

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

                JsonArray genresArray = new JsonArray();

                Statement genreStatement = conn.createStatement();

                String genresQuery = "SELECT * from genres_in_movies as gim, genres as g where gim.genreId = g.id and gim.movieId = '" + movie_id + "' limit 3";

                ResultSet genreRs = genreStatement.executeQuery(genresQuery);

                while (genreRs.next()) {
                    String genre_name = genreRs.getString("name");
                    JsonObject genreObject = new JsonObject();
                    genreObject.addProperty("genre_name", genre_name);
                    genresArray.add(genreObject);
                }

                jsonObject.add("movie_genres", genresArray);

                genreRs.close();
                genreStatement.close();

                JsonArray starsArray = new JsonArray();

                Statement starStatement = conn.createStatement();

                String starsQuery = "SELECT * from stars as s, stars_in_movies as sim where s.id = sim.starId and sim.movieId = '" + movie_id + "' limit 3";

                ResultSet starRs = starStatement.executeQuery(starsQuery);

                while (starRs.next()) {
                    String star_id = starRs.getString("id");
                    String star_name = starRs.getString("name");
                    JsonObject starObject = new JsonObject();
                    starObject.addProperty("star_id", star_id);
                    starObject.addProperty("star_name", star_name);
                    starsArray.add(starObject);
                }

                jsonObject.add("movie_stars", starsArray);

                starRs.close();
                starStatement.close();

                jsonArray.add(jsonObject);
            }

            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

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
}

