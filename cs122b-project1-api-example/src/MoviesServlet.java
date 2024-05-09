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

        // Get the selected search keys from the request
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        String genre = request.getParameter("genre");
        String titleStart = request.getParameter("titleStart");
        String sort1 = request.getParameter("sort1");
        String order1 = request.getParameter("order1");
        String sort2 = request.getParameter("sort2");
        String order2 = request.getParameter("order2");

        // pagination parameters
        String limit = request.getParameter("limit");
        String currentPage = request.getParameter("currentPage");
        int offset = currentPage == null ? 0 : Integer.parseInt(currentPage) * Integer.parseInt(limit);


        //String query = "SELECT m.id, m.title, m.year, m.director, r.rating from movies m, ratings r where m.id = r.movieId order by r.rating desc limit 20";
        // Start constructing the SQL query
        StringBuilder queryBuilder = new StringBuilder("SELECT m.id, m.title, m.year, m.director, r.rating FROM movies m JOIN ratings r on r.movieid = m.id WHERE 1=1");

        // Check if the search parameters are not null and not empty, then append to the query
        if (title != null && !title.isEmpty()) {
            queryBuilder.append(" AND m.title LIKE ?");
        }
        if (year != null && !year.isEmpty()) {
            queryBuilder.append(" AND m.year LIKE ?");
        }
        if (director != null && !director.isEmpty()) {
            queryBuilder.append(" AND m.director LIKE ?");
        }
        if (star != null && !star.isEmpty()) {
            queryBuilder.append(" AND EXISTS (SELECT * FROM stars s JOIN stars_in_movies sim ON s.id = sim.starId WHERE sim.movieId = m.id AND s.name LIKE ?)");
        }
        if (genre != null && !genre.isEmpty()) {
            queryBuilder.append(" AND EXISTS (SELECT * FROM genres g JOIN genres_in_movies gim ON g.id = gim.genreId WHERE gim.movieId = m.id AND g.name LIKE ?)");
        }
        if (titleStart != null && !titleStart.isEmpty()) {
            if (titleStart.equals("*")) {
                queryBuilder.append(" AND m.title REGEXP '^[^A-Za-z0-9]'");
            } else {
                queryBuilder.append(" AND m.title LIKE ?");
            }
        }
        if (sort1 != null && !sort1.isEmpty() && order1 != null && !order1.isEmpty()) {
            queryBuilder.append(" ORDER BY ").append(sort1).append(" ").append(order1);
        }
        if (sort2 != null && !sort2.isEmpty() && order2 != null && !order2.isEmpty()) {
            queryBuilder.append(", ").append(sort2).append(" ").append(order2);
        }
        if (limit != null && !limit.isEmpty()) {
            queryBuilder.append(" LIMIT ?");
        }
        if (currentPage != null && !currentPage.isEmpty()) {
            queryBuilder.append(" OFFSET ?");
        }

        // Convert StringBuilder to String
        String query = queryBuilder.toString();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            int i = 1;

            if (title != null && !title.isEmpty()) {
                statement.setString(i++, "%" + title + "%");
            }
            if (year != null && !year.isEmpty()) {
                statement.setString(i++, "%" + year + "%");
            }
            if (director != null && !director.isEmpty()) {
                statement.setString(i++, "%" + director + "%");
            }
            if (star != null && !star.isEmpty()) {
                statement.setString(i++, "%" + star + "%");
            }
            if (genre != null && !genre.isEmpty()) {
                statement.setString(i++, "%" + genre + "%");
            }
            if (titleStart != null && !titleStart.isEmpty()) {
                if (!titleStart.equals("*")) {
                    statement.setString(i++, titleStart + "%");
                }
            }
            if (limit != null && !limit.isEmpty()) {
                statement.setInt(i++, Integer.parseInt(limit));
            }
            if (currentPage != null && !currentPage.isEmpty()) {
                statement.setInt(i++, offset);
            }

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // add search query to response
            //JsonObject searchQuery = new JsonObject();
            //searchQuery.addProperty("SearchQuery", query);
            //jsonArray.add(searchQuery);

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

                String genresQuery = "SELECT * from genres_in_movies as gim, genres as g where gim.genreId = g.id and gim.movieId = '" + movie_id + "' limit 3";

                PreparedStatement genreStatement = conn.prepareStatement(genresQuery);

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

                String starsQuery = "SELECT * from stars as s, stars_in_movies as sim where s.id = sim.starId and sim.movieId = '" + movie_id + "' limit 3";

                PreparedStatement starStatement = conn.prepareStatement(starsQuery);

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

