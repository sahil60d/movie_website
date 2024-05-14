package XMLParsing;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXStarParser extends DefaultHandler {

    private List<StarEntry> stars;
    private int maxId;

    // Database connection info
    String jdbcURL = "jdbc:mysql://localhost:3306/moviedb";
    String username = "mytestuser";
    String password = "My6$Password";

    // get max id from stars table
    private int getMaxId() {
        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            // Get the max id from the stars table (nm0000001, nm0000002, ...
            String query = "SELECT max(id) as maxId from stars";

            PreparedStatement statement = connection.prepareStatement(query);

            // Generate the new star id
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            String mId = rs.getString("maxId");
            int maxId = Integer.parseInt(mId.substring(2));
            return maxId;
            //String newId = "nm" + String.format("%07d", maxId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    public List<StarEntry> getStars() {
        return stars;
    }

    private String tempVal;

    // to maintain context
    private StarEntry tempStar;

    public SAXStarParser() {
        stars = new ArrayList<StarEntry>();
        maxId = getMaxId();
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
            sp.parse("stanford-movies/actors63.xml", this);

        } catch (SAXException | ParserConfigurationException | IOException se) {
            se.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("No of Stars '" + stars.size() + "'.");

        Iterator<StarEntry> it = stars.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    // Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            // create a new instance of employee
            tempStar = new StarEntry();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("actor")) {
            // check if starId is given, not if create one
            if (tempStar.getId() == null) {
                maxId++;
                tempStar.setId("nm" + String.format("%07d", maxId));
            }

            // add it to the list
            stars.add(tempStar);
        } else if (qName.equalsIgnoreCase("stagename")) {
            tempStar.setName(tempVal);
        } else if (qName.equalsIgnoreCase("dob")) {
            try {
                tempStar.setBirthYear(Integer.parseInt(tempVal));
            } catch (NumberFormatException e) {
                tempStar.setBirthYear(0);
            }
        }
    }

    public static void main(String[] args) {
        SAXStarParser spe = new SAXStarParser();
        spe.runExample();
    }
}
