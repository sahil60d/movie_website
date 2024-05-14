package XMLParsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXMovieParser extends DefaultHandler {

    // Use hashmap instead of list for efficiency
    private HashMap<String, MovieEntry> movies;

    public HashMap<String, MovieEntry> getMovies() {
        return movies;
    }

    private String tempVal;

    // to maintain context
    private MovieEntry tempMovie;

    public SAXMovieParser() {
        movies = new HashMap<String, MovieEntry>();
        parseDocument();
    }

    public void runExample() {
        parseDocument();
        printData();
    }

    private void parseDocument() {

        // get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            // get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            // parse the file and also register this class for call backs
            sp.parse("stanford-movies/mains243.xml", this);

        } catch (SAXException | ParserConfigurationException | IOException se) {
            se.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("No of Movies '" + movies.size() + "'.");

        Iterator<MovieEntry> it = movies.values().iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    // Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // reset
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            // create a new instance of employee
            tempMovie = new MovieEntry();
            //tempMovie.setMovieId(attributes.getValue("fid"));
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("film")) {
            // add it to the list
            movies.put(tempMovie.getId(), tempMovie);

        } else if (qName.equalsIgnoreCase("fid")) {
            tempMovie.setId(tempVal);
        } else if (qName.equalsIgnoreCase("t")) {
            if (tempVal != null && !tempVal.isEmpty()) {
                tempMovie.setTitle(tempVal);
            } else {
                tempMovie.setTitle("");
            }
        } else if (qName.equalsIgnoreCase("dirn")) {
            if (tempVal != null && !tempVal.isEmpty()) {
                tempMovie.setDirector(tempVal);
            } else {
                tempMovie.setDirector("");
            }
        } else if (qName.equalsIgnoreCase("cat")) {
            if (tempVal != null && !tempVal.isEmpty()) {
                tempMovie.setGenre(tempVal);
            } else {
                tempMovie.setGenre("Ctxx");
            }
        } else if (qName.equalsIgnoreCase("year")) {
            try {
                tempMovie.setYear(Integer.parseInt(tempVal));
            } catch (NumberFormatException e) {
                tempMovie.setYear(0);
            }
        }
    }

    public static void main(String[] args) {
        SAXMovieParser spe = new SAXMovieParser();
        spe.runExample();
    }

}
