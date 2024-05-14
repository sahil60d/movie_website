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
import java.sql.DatabaseMetaData;

// Declaring a WebServlet called DatabaseMetadataServlet, which maps to url "/api/metadata"
@WebServlet(name = "DatabaseMetadataServlet", urlPatterns = "/api/metadata")
public class DatabaseMetadataServlet extends HttpServlet {

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("application/json"); // Response mime type

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
            JsonArray jsonArray = new JsonArray();
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("tableName", tableName);
                ResultSet columns = metaData.getColumns(null, null, tableName, null);
                JsonArray columnArray = new JsonArray();
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    JsonObject columnObject = new JsonObject();
                    columnObject.addProperty("columnName", columnName);
                    columnObject.addProperty("columnType", columnType);
                    columnArray.add(columnObject);
                }
                jsonObject.add("columns", columnArray);
                jsonArray.add(jsonObject);
            }
            PrintWriter out = response.getWriter();
            out.write(jsonArray.toString());
            response.setStatus(200);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }
}