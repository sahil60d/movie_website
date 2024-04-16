/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movies.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +     // display movie_name for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";

        rowHTML += "<th>";
        for (let j = 0; j < resultData[i]["movie_genres"].length; j++) {
            	rowHTML += resultData[i]["movie_genres"][j]["genre_name"];
            	if (j < resultData[i]["movie_genres"].length - 1) {
            		rowHTML += ", ";
            	}
        }

        rowHTML += "</th>";

        rowHTML += "<th>";
        for (let j = 0; j < resultData[i]["movie_stars"].length; j++) {
                	rowHTML += '<a href="single-star.html?id=' + resultData[i]["movie_stars"][j]["star_id"] + '">'
                	+ resultData[i]["movie_stars"][j]["star_name"]
                	+ '</a>';
                	if (j < resultData[i]["movie_stars"].length - 1) {
                		rowHTML += ", ";
                	}
        }
        rowHTML += "</th>";


        //rowHTML += "<th>" + resultData[i]["movie_genres"][0]["genre_name"] + "</th>";
        //rowHTML += "<th>" + resultData[i]["movie_stars"][0]["star_name"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleMovieResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by MoviesServlet in Movies.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the MoviesServlet
});