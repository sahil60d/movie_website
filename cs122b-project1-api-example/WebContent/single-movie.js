/**
 * Retrieve the movie id from the URL
 * @param target String
 * @returns {*}
 */
function getMovieByName(target) {
    // Get the request URL
    let url = window.location.href;
    // Encode the target parameter name
    target = target.replace(/[\[\]]/g, "\\$&");
    // Create a regex to find the parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    // If there is no result, return null
    if (!results) return null;
    // If there is no result, return an empty string
    if (!results[2]) return '';
    // Return the parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handle the data returned by the API
 * @param resultData jsonObject
 */

function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let movieInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<p>Movie Title: " + resultData[0]["movie_title"] + "</p>" +
        "<p>Movie Year: " + resultData[0]["movie_year"] + "</p>" +
        "<p>Movie Director: " + resultData[0]["movie_director"] + "</p>" +
        "<p>Movie Rating: " + resultData[0]["movie_rating"] + "</p>");

    console.log("handleMovieResult: populating movie table from resultData");

    // Populate the movie info table
    // Find the empty table body by id "movie_info_table_body"
    let movieInfoTableBodyElement = jQuery("#movie_info_table_body");

    // Iterate through resultData
    for (let i = 0; i < resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject to create table rows
        let rowHTML = "";
        rowHTML += "<tr>";

        rowHTML += "<th>";
        /*
        for (let j = 0; j < resultData[i]["movie_genres"].length; j++) {
            rowHTML += resultData[i]["movie_genres"][j]["genre_name"];
            if (j < resultData[i]["movie_genres"].length - 1) {
                rowHTML += ", ";
            }
        } */

        for (let j = 0; j < resultData[i]["movie_genres"].length; j++) {
            rowHTML += '<a href = "browse.html?genre=' + resultData[i]["movie_genres"][j]["genre_name"] + '">'
                + resultData[i]["movie_genres"][j]["genre_name"]
                + '</a>';
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

        // Add to cart button
        rowHTML += "<th>";
        rowHTML += '<button onclick="addToCart(\'' + resultData[i]["movie_id"] + '\')">Add to Cart</button>';
        rowHTML += "</th>";

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieInfoTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Get id from URL
let movieId = getMovieByName('id');

// Makes the HTTP GET request and registers on success callback function handleMovieResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

// Add to cart function
// Add to cart function. Adds movie id to session storage
function addToCart(movieId) {
    console.log('Adding to cart: ' + movieId);

    jQuery.ajax({
        method: 'POST',
        url: 'api/cart',
        data: {
            movieId: movieId
        },
        success: function(response) {
            console.log('Added to cart:', response);
        },
        error: function(error) {
            console.error('Add to cart failed:', error);
        }
    });
}

