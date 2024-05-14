// Script for adding a movie

function handleAddMovieForm(event) {
    event.preventDefault();
    console.error('Add Movie');

    let title = document.getElementById('movieTitle').value;
    let year = document.getElementById('movieYear').value;
    let director = document.getElementById('movieDirector').value;
    let star = document.getElementById('starName').value;
    let genre = document.getElementById('genreName').value;


    jQuery.ajax( {
        dataType: "json",
        url: "../api/add-movie",
        method: "POST",
        data: {
            movieTitle: title,
            movieYear: year,
            movieDirector: director,
            starName: star,
            genreName: genre
        },
        success: function(response) {
            console.log("handleResult: ", response);
            let resultMessageElement = jQuery("#resultMessage");
            resultMessageElement.empty();
            resultMessageElement.append("<p>Added Movie: movieId: " + response.movieId +
                ", genreId: " + response.genreId +
                ", starId: " + response.starId + "</p>");
        },
        error: function(error) {
            console.error('Error in adding movie:', error);
            let resultMessageElement = jQuery("#resultMessage");
            resultMessageElement.empty();
            resultMessageElement.append("<p>Error in adding movie</p>");
        }
    });
}

document.getElementById('addMovieForm').addEventListener('submit', handleAddMovieForm);