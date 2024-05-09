// Description: This file contains the JavaScript code for the browse page.

let currentPage = 1; // initialize current page number

/******************* Data Handling *******************/

// Function to display search results
function handleSearchResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    // clear search results
    clearSearchResults();

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < resultData.length; i++) {

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

        // Add to cart button
        rowHTML += "<th>";
        rowHTML += '<button onclick="addToCart(\'' + resultData[i]["movie_id"] + '\')">Add to Cart</button>';
        rowHTML += "</th>";


        //rowHTML += "<th>" + resultData[i]["movie_genres"][0]["genre_name"] + "</th>";
        //rowHTML += "<th>" + resultData[i]["movie_stars"][0]["star_name"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

// Clears result display
function clearSearchResults() {
    jQuery("#movie_table_body").empty();
}

// Handles data from search option
function handleSearch(event) {
    event.preventDefault(); // Prevent the form from submitting in the traditional way
    console.error('Search submitted!');
    // Collect search parameters
    let title = document.getElementById('search_title').value;
    let year = document.getElementById('search_year').value;
    let director = document.getElementById('search_director').value;
    let star = document.getElementById('search_star').value;

    // set default sort parameters
    let primarySort = "";
    let primaryOrder = "";
    let secondarySort = "";
    let secondaryOrder = "";
    let limit = 25;

    const sortParams = getSavedSort();

    if (sortParams != null) {
        primarySort = sortParams.sort1;
        primaryOrder = sortParams.order1;
        secondarySort = sortParams.sort2;
        secondaryOrder = sortParams.order2;
        limit = sortParams.limit;
    }

    // Send the search parameters to the server using AJAX
    jQuery.ajax({
        dataType: "json", // Setting return data type
        url: "api/movies", // The endpoint on server for searching
        method: 'GET',
        data: {
            title: title,
            year: year,
            director: director,
            star: star,
            sort1: primarySort,
            order1: primaryOrder,
            sort2: secondarySort,
            order2: secondaryOrder,
            limit: limit
        },
        success: function(response) {
            // If the search is successful, display the results
            handleSearchResult(response);

            // save search parameters
            saveSearchParams({
                title: title,
                year: year,
                director: director,
                star: star,
                genre: "",
                titleStart: ""
            });
        },
        error: function(error) {
            // Handle any errors that occur during the search
            console.error('Search failed:', error);
        }
    });
}

// Sort movies
function sortMovies() {
    console.error('Sorting movies!');

    // get sort order
    let sortOrder = document.getElementById('sortField').value;

    // get limit
    let limit = document.getElementById('limitField').value;

    switch (sortOrder) {
        case 'titleAratingA':
            primarySort = 'm.title';
            secondarySort = 'r.rating';
            primaryOrder = 'ASC';
            secondaryOrder = 'ASC';
            break;
        case 'titleAratingD':
            primarySort = 'm.title';
            secondarySort = 'r.rating';
            primaryOrder = 'ASC';
            secondaryOrder = 'DESC';
            break;
        case 'titleDratingA':
            primarySort = 'm.title';
            secondarySort = 'r.rating';
            primaryOrder = 'DESC';
            secondaryOrder = 'ASC';
            break;
        case 'titleDratingD':
            primarySort = 'm.title';
            secondarySort = 'r.rating';
            primaryOrder = 'DESC';
            secondaryOrder = 'DESC';
            break;
        case 'ratingAtitleA':
            primarySort = 'r.rating';
            secondarySort = 'm.title';
            primaryOrder = 'ASC';
            secondaryOrder = 'ASC';
            break;
        case 'ratingAtitleD':
            primarySort = 'r.rating';
            secondarySort = 'm.title';
            primaryOrder = 'ASC';
            secondaryOrder = 'DESC';
            break;
        case 'ratingDtitleA':
            primarySort = 'r.rating';
            secondarySort = 'm.title';
            primaryOrder = 'DESC';
            secondaryOrder = 'ASC';
            break;
        case 'ratingDtitleD':
            primarySort = 'r.rating';
            secondarySort = 'm.title';
            primaryOrder = 'DESC';
            secondaryOrder = 'DESC';
            break;
    }

    const searchParams = getSavedSearch();

    if (searchParams != null) {
        title = searchParams.title;
        year = searchParams.year;
        director = searchParams.director;
        star = searchParams.star;
        genre = searchParams.genre;
        titleStart = searchParams.titleStart;
    }

    // send sort parameters to the server using AJAX
    jQuery.ajax({
        dataType: "json",
        url: "api/movies",
        method: 'GET',
        data: {
            title: title,
            year: year,
            director: director,
            star: star,
            genre: genre,
            titleStart: titleStart,
            sort1: primarySort,
            order1: primaryOrder,
            sort2: secondarySort,
            order2: secondaryOrder,
            limit: limit
        },
        success: function(response) {
            // If the search is successful, display the results
            handleSearchResult(response);

            // save sort parameters
            saveSortParams({
                sort1: primarySort,
                order1: primaryOrder,
                sort2: secondarySort,
                order2: secondaryOrder,
                limit: limit
            });
        },
        error: function(error) {
            // Handle any errors that occur during the search
            console.error('Search failed:', error);
        }
    });
}

// Handle pagination
function goToPage(page) {
    console.error('Going to page ' + page);

    currentPage = page;
    if (currentPage < 1) {
        currentPage = 1;
    }

    // get search parameters
    const searchParams = getSavedSearch();

    if (searchParams != null) {
        title = searchParams.title;
        year = searchParams.year;
        director = searchParams.director;
        star = searchParams.star;
        genre = searchParams.genre;
        titleStart = searchParams.titleStart;
    } else {
        title = "";
        year = "";
        director = "";
        star = "";
        genre = "";
        titleStart = "";
    }

    // get sort parameters
    const sortParams = getSavedSort();

    if (sortParams != null) {
        primarySort = sortParams.sort1;
        primaryOrder = sortParams.order1;
        secondarySort = sortParams.sort2;
        secondaryOrder = sortParams.order2;
        limit = sortParams.limit;
    } else {
        primarySort = "m.title";
        primaryOrder = "r.rating";
        secondarySort = "ASC";
        secondaryOrder = "ASC";
        limit = 25;
    }

    // send search parameters to the server using AJAX
    jQuery.ajax({
        dataType: "json",
        url: "api/movies",
        method: 'GET',
        data: {
            title: title,
            year: year,
            director: director,
            star: star,
            genre: genre,
            titleStart: titleStart,
            sort1: primarySort,
            order1: primaryOrder,
            sort2: secondarySort,
            order2: secondaryOrder,
            limit: limit,
            currentPage: currentPage
        },
        success: function(response) {
            // If the search is successful, display the results
            handleSearchResult(response);

            // save page number
            savePageNumber(currentPage);
        },
        error: function(error) {
            // Handle any errors that occur during the search
            console.error('Search failed:', error);
        }
    });
}

// Add to cart function. Adds movie id to session storage
function addToCart(movieId) {
    console.error('Adding to cart: ' + movieId);

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

/******************* Session Storage *******************/

// save last search query
function saveSearchParams(searchParams) {
    sessionStorage.setItem('searchParams', JSON.stringify(searchParams));
}

// get last search query
function getSavedSearch() {
    let searchParams = sessionStorage.getItem('searchParams');
    return searchParams ? JSON.parse(searchParams) : null;
}

// save sort parameters
function saveSortParams(sortParams) {
    sessionStorage.setItem('sortParams', JSON.stringify(sortParams));
}

// get sort parameters
function getSavedSort() {
    let sortParams = sessionStorage.getItem('sortParams');
    return sortParams ? JSON.parse(sortParams) : null;
}

// save page number
function savePageNumber(page) {
    sessionStorage.setItem('page', page);
}

// get page number
function getSavedPageNumber() {
    let page = sessionStorage.getItem('page');
    return page ? page : 1;
}

/******************* Initialization *******************/

// Check to see if genre is specified in the URL
$(document).ready(function() {
    var urlParams = new URLSearchParams(window.location.search);
    var genre = urlParams.get('genre');

    if (genre != null) {
        console.log('Genre: ' + genre);
        jQuery.ajax({
            dataType: "json",
            url: "api/movies",
            method: 'GET',
            data: {
                genre: genre
            },
            success: function(response) {
                // If the search is successful, display the results
                handleSearchResult(response);

                // save search parameters
                saveSearchParams({
                    title: "",
                    year: "",
                    director: "",
                    star: "",
                    genre: genre,
                    titleStart: ""
                });
            },
            error: function(error) {
                // Handle any errors that occur during the search
                console.error('Search failed:', error);
            }
        });
    }
})

// Check to see if there are saved parameters
$(document).ready(function() {
    // check if page has been loaded before
    if (!sessionStorage.getItem('pageLoaded')) {
        console.log('First time loading page');
        sessionStorage.setItem('pageLoaded', true);
    } else {
        console.log('Page has been loaded before');

        const searchParams = getSavedSearch();
        const sortParams = getSavedSort();
        const page = getSavedPageNumber();

        if (searchParams != null) {
            document.getElementById('search_title').value = searchParams.title;
            document.getElementById('search_year').value = searchParams.year;
            document.getElementById('search_director').value = searchParams.director;
            document.getElementById('search_star').value = searchParams.star;
        }

        if (sortParams != null) {
            document.getElementById('sortField').value = sortParams.sort1 + sortParams.order1 + sortParams.sort2 + sortParams.order2;
            document.getElementById('limitField').value = sortParams.limit;
        }

        goToPage(page);
    }
})

/******************* Event Listeners *******************/

// Attach event listener to the search form
document.getElementById('search_form').addEventListener('submit', handleSearch);
document.querySelectorAll('.browse-link').forEach(function(link) {
    link.addEventListener('click', function(event) {
        event.preventDefault();

        // Get values from the link
        let browseType = link.getAttribute('data-browse');
        let browseValue = link.getAttribute('data-value');

        let genre = "";
        let titleStart = "";

        if (browseType == 'genre') {
            genre = browseValue;
        } else {
            titleStart = browseValue;
        }

        // set default sort parameters
        let primarySort = "";
        let primaryOrder = "";
        let secondarySort = "";
        let secondaryOrder = "";
        let limit = 25;

        // get sort parameters
        const sortParams = getSavedSort();

        if (sortParams != null) {
            primarySort = sortParams.sort1;
            primaryOrder = sortParams.order1;
            secondarySort = sortParams.sort2;
            secondaryOrder = sortParams.order2;
            limit = sortParams.limit;
        }

        // send search parameters to the server using AJAX
        jQuery.ajax({
            dataType: "json",
            url: "api/movies",
            method: 'GET',
            data: {
                titleStart: titleStart,
                genre: genre,
                sort1: primarySort,
                order1: primaryOrder,
                sort2: secondarySort,
                order2: secondaryOrder,
                limit: limit
            },
            success: function(response) {
                // If the search is successful, display the results
                handleSearchResult(response);

                // save search parameters
                saveSearchParams({
                    title: "",
                    year: "",
                    director: "",
                    star: "",
                    genre: genre,
                    titleStart: titleStart
                });

            },
            error: function(error) {
                // Handle any errors that occur during the search
                console.error('Search failed:', error);
            }
        });
    })
})