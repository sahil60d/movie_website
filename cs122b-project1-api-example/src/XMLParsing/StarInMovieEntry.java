package XMLParsing;

public class StarInMovieEntry {
    // Movie Info
    private String title;
    private String fid;

    // Star in Movie Info
    private String starId;
    private String movieId;
    private String name;

    // Constructor
    public StarInMovieEntry() {
    }

    public StarInMovieEntry(String title, String fid) {
        this.title = title;
        this.fid = fid;
    }

    public StarInMovieEntry(String starId, String movieId, String name) {
        this.starId = starId;
        this.movieId = movieId;
        this.name = name;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public void setStarId(String starId) {
        this.starId = starId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getFid() {
        return fid;
    }

    public String getStarId() {
        return starId;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getName() {
        return name;
    }

    //toString
    @Override
    public String toString() {
        return "StarEntry{" +
                "title='" + title + '\'' +
                ", fid='" + fid + '\'' +
                ", starId='" + starId + '\'' +
                ", movieId='" + movieId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
