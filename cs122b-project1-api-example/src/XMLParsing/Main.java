package XMLParsing;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Create a hash map for all the categories/genres
        HashMap<String, String> cats = new HashMap<>();
        cats.put("Ctxx", "Uncategorized");
        cats.put("Actn", "Violence");
        cats.put("Advt", "Adventure");
        cats.put("AvGa", "Avant Garde");
        cats.put("Camp", "Now-Camp");
        cats.put("Cart", "Cartoon");
        cats.put("CnR", "Cops and Robbers");
        cats.put("Comd", "Comedy");
        cats.put("Disa", "Disaster");
        cats.put("Docu", "Documentary");
        cats.put("Dram", "Drama");
        cats.put("Epic", "Epic");
        cats.put("Faml", "Family");
        cats.put("Hist", "History");
        cats.put("Horr", "Horror");
        cats.put("Musc", "Musical");
        cats.put("Myst", "Mystery");
        cats.put("Noir", "Black");
        cats.put("Porn", "Pornography");
        cats.put("Romt", "Romantic");
        cats.put("ScFi", "Science Fiction");
        cats.put("Surl", "Surreal");
        cats.put("Susp", "Suspense");
        cats.put("West", "Western");


        // Database connection info
        String jdbcURL = "jdbc:mysql://localhost:3306/moviedb";
        String username = "mytestuser";
        String password = "My6$Password";

        // Initialize the parsers and parse the XML files
        SAXMovieParser movieParser = new SAXMovieParser();
        SAXStarParser starParser = new SAXStarParser();
        SAXStarInMovieParser starInMovieParser = new SAXStarInMovieParser();

        // Get the parsed data
        HashMap<String, MovieEntry> movies = movieParser.getMovies();
        List<StarEntry> stars = starParser.getStars();
        HashMap<String, StarInMovieEntry> starInMovies = starInMovieParser.getStarInMovies();

        // Add stars to movies
        Iterator<StarInMovieEntry> starInMovieIterator = starInMovies.values().iterator();
        while (starInMovieIterator.hasNext()) {
            StarInMovieEntry starInMovie = starInMovieIterator.next();

            if (movies.containsKey(starInMovie.getMovieId())) {
                MovieEntry movie = movies.get(starInMovie.getMovieId());
                //movie.setStar(stars.get(starInMovie.getStarId()).getName());
                // Add the star to the movie
                movie.setStar(starInMovie.getName());
            }
        }

        // Add starId to star from starInMovies
        Iterator<StarEntry> starIterator = stars.iterator();
        while (starIterator.hasNext()) {
            StarEntry star = starIterator.next();
            if (starInMovies.containsKey(star.getName())) {
                StarInMovieEntry starInMovie = starInMovies.get(star.getId());
                //star.setId(starInMovie.getStarId());
                starInMovies.get(star.getName()).setStarId(star.getId());
            }
        }

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            connection.setAutoCommit(false);

            String query = "INSERT IGNORE INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            for (MovieEntry movie : movies.values()) {
                // flag if movie is valid
                boolean valid = true;

                if (movie.getId() != null && !movie.getId().isEmpty()) {
                    statement.setString(1, movie.getId());
                } else {
                    valid = false;
                }
                if (movie.getTitle() != null && !movie.getTitle().isEmpty()) {
                    statement.setString(2, movie.getTitle());
                } else {
                    statement.setString(2, "N/A");
                }
                statement.setInt(3, movie.getYear());
                if (movie.getDirector() != null && !movie.getDirector().isEmpty()) {
                    statement.setString(4, movie.getDirector());
                } else {
                    statement.setString(4, "N/A");
                }
                if (valid) {
                    statement.addBatch();
                }
            }

            try {
                // Execute the batch
                statement.executeBatch();

                // Commit the transaction
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                connection.rollback();
            } finally {
                connection.setAutoCommit(true);
                statement.close();
            }

            // get existing genre names
            query = "SELECT * FROM genres";
            statement = connection.prepareStatement(query);
            ResultSet rst = statement.executeQuery();
            HashMap<String, String> genreNames = new HashMap<>();
            while (rst.next()) {
                genreNames.put(rst.getString("name"), rst.getString("name"));
            }
            statement.close();

            connection.setAutoCommit(false);
            // Insert genres if they don't exist
            query = "INSERT IGNORE INTO genres (name) VALUES (?)";
            statement = connection.prepareStatement(query);
            for (MovieEntry movie : movies.values()) {
                if (cats.containsKey(movie.getGenre())) {
                    movie.setGenre(cats.get(movie.getGenre()));
                } else {
                    movie.setGenre("Ctxx");
                }

                if (!genreNames.containsKey(movie.getGenre())) {
                    statement.setString(1, movie.getGenre());
                    statement.addBatch();
                    genreNames.put(movie.getGenre(), movie.getGenre());
                }
            }

            try {
                // Execute the batch
                statement.executeBatch();

                // Commit the transaction
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                connection.rollback();
            } finally {
                connection.setAutoCommit(true);
                statement.close();
            }

            // Get the genre ids
            query = "SELECT * FROM genres";
            statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            HashMap<String, String> genreIds = new HashMap<>();
            while (rs.next()) {
                genreIds.put(rs.getString("name"), rs.getString("id"));
            }
            statement.close();

            // Add genreId to movies
            Iterator<MovieEntry> movieIterator = movies.values().iterator();
            while (movieIterator.hasNext()) {
                MovieEntry movie = movieIterator.next();
                if (genreIds.containsKey(movie.getGenre())) {
                    movie.setGenreId(genreIds.get(movie.getGenre()));
                }
            }

            connection.setAutoCommit(false);
            // Insert genres_in_movies
            query = "INSERT IGNORE INTO genres_in_movies (genreId, movieId) VALUES (?, ?)";
            statement = connection.prepareStatement(query);
            for (MovieEntry movie : movies.values()) {
                // set flag
                boolean valid = true;

                statement.setString(1, movie.getGenreId());
                if (movie.getId() != null && !movie.getId().isEmpty()) {
                    statement.setString(2, movie.getId());
                } else {
                    valid = false;
                }
                if (valid) {
                    statement.addBatch();
                }
            }

            try {
                // Execute the batch
                statement.executeBatch();

                // Commit the transaction
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                connection.rollback();
            } finally {
                connection.setAutoCommit(true);
                statement.close();
            }

            connection.setAutoCommit(false);
            // Insert stars from stars list
            query = "INSERT IGNORE INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
            statement = connection.prepareStatement(query);
            for (StarEntry star : stars) {
                statement.setString(1, star.getId());
                statement.setString(2, star.getName());
                statement.setInt(3, star.getBirthYear());
                statement.addBatch();
            }

            try {
                // Execute the batch
                statement.executeBatch();

                // Commit the transaction
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                connection.rollback();
            } finally {
                connection.setAutoCommit(true);
                statement.close();
            }

            connection.setAutoCommit(false);
            // Insert stars_in_movies
            query = "INSERT IGNORE INTO stars_in_movies (starId, movieId) VALUES (?, ?)";
            statement = connection.prepareStatement(query);
            for (StarInMovieEntry starInMovie : starInMovies.values()) {
                // set flag
                boolean valid = true;

                if (starInMovie.getStarId() != null && !starInMovie.getStarId().isEmpty()) {
                    statement.setString(1, starInMovie.getStarId());
                } else {
                    valid = false;
                }

                if (starInMovie.getMovieId() != null && !starInMovie.getMovieId().isEmpty()) {
                    statement.setString(2, starInMovie.getMovieId());
                } else {
                    valid = false;
                }
                if (valid) {
                    statement.addBatch();
                }
            }

            try {
                // Execute the batch
                statement.executeBatch();

                // Commit the transaction
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                connection.rollback();
            } finally {
                connection.setAutoCommit(true);
                statement.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}