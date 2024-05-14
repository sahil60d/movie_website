package XMLParsing;

public class MovieEntry {
    // Movie Fields
    private String id;
    private String title;
    private int year;
    private String director;
    private String genre;
    private String star;

    // GenreInMovie Fields
    private String genreId;
    private String movieId;

    // Constructor
    public MovieEntry() {
    }

    public MovieEntry(String id, String title, int year, String director, String genre) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.genre = genre;
        this.star = star;
    }

    public MovieEntry(String genreId, String movieId) {
        this.genreId = genreId;
        this.movieId = movieId;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public String getGenre() {
        return genre;
    }

    public String getStar() {
        return star;
    }

    public String getGenreId() {
        return genreId;
    }

    public String getMovieId() {
        return movieId;
    }

    // toString
    @Override
    public String toString() {
        return "MovieEntry{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", director='" + director + '\'' +
                ", genre='" + genre + '\'' +
                ", star='" + star + '\'' +
                '}';
    }
}