document.addEventListener('DOMContentLoaded', fetchMetadata);

function goToAddStar() {
    window.location.href = 'add-star.html'; // Replace with the correct path to your Add Star page
}

function goToAddMovie() {
    window.location.href = 'add-movie.html'; // Replace with the correct path to your Add Movie page
}

function fetchMetadata() {
    $.ajax({
        url: '../api/metadata',
        method: 'GET',
        dataType: 'json',
        success: function(data) {
            displayMetadata(data);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.error('Error fetching metadata:', textStatus, errorThrown);
        }
    });
}

function displayMetadata(data) {
    const metadataDisplay = document.getElementById('metadataDisplay');
    metadataDisplay.textContent = JSON.stringify(data, null, 2);
}