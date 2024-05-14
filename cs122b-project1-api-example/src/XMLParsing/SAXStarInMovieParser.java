package XMLParsing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXStarInMovieParser extends DefaultHandler {

    // Use hashmap instead of list for efficiency
    private HashMap<String, StarInMovieEntry> starInMovies;

    public HashMap<String, StarInMovieEntry> getStarInMovies() {
        return starInMovies;
    }

    private String tempVal;

    // to maintain context
    private StarInMovieEntry tempStarInMovie;

    public SAXStarInMovieParser() {
        starInMovies = new HashMap<String, StarInMovieEntry>();
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
            sp.parse("stanford-movies/casts124.xml", this);

        } catch (SAXException | ParserConfigurationException | IOException se) {
            se.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("No of Star in Movies '" + starInMovies.size() + "'.");
        Iterator it = starInMovies.entrySet().iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    // Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // reset
        tempVal = "";
        if (qName.equalsIgnoreCase("m")) {
            // create a new instance of starInMovie
            tempStarInMovie = new StarInMovieEntry();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("m")) {
            // add it to the list
            starInMovies.put(tempStarInMovie.getName(), tempStarInMovie);
        } else if (qName.equalsIgnoreCase("f")) {
            tempStarInMovie.setMovieId(tempVal);
        } else if (qName.equalsIgnoreCase("a")) {
            tempStarInMovie.setName(tempVal);
        }
    }

    public static void main(String[] args) {
        SAXStarInMovieParser spe = new SAXStarInMovieParser();
        spe.runExample();
    }
}
