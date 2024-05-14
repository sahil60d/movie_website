// Scipt for adding a star to the database

function handleAddStarForm(event) {
    event.preventDefault();
    console.error('Add Star');

    let name = document.getElementById('starName').value;
    let birthYear = document.getElementById('starBirthYear').value;

    if (birthYear === "") {
        birthYear = null;
    }

    jQuery.ajax( {
        dataType: "json",
        url: "../api/add-star",
        method: "POST",
        data: {
            name: name,
            birthYear: birthYear
        },
        success: function(response) {
            console.log("handleResult: ", response);
            let resultMessageElement = jQuery("#resultMessage");
            resultMessageElement.empty();
            resultMessageElement.append("<p>Added Star with ID: " + response.id + "</p>");
        },
        error: function(error) {
            console.error('Error in adding star:', error);
            let resultMessageElement = jQuery("#resultMessage");
            resultMessageElement.empty();
            resultMessageElement.append("<p>Error in adding star</p>");
        }
    });
}


document.getElementById('addStarForm').addEventListener('submit', handleAddStarForm);