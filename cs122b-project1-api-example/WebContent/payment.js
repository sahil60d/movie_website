// Script for payment

// Function to handle submit button
function handlePaymentResult(event) {
    event.preventDefault();

    // Get form data
    let firstName = document.getElementById("first_name").value;
    let lastName = document.getElementById("last_name").value;
    let creditCard = document.getElementById("credit_card").value;
    let expiration = document.getElementById("expiration").value;

    // Check if any field is empty
    if (firstName === "" || lastName === "" || creditCard === "" || expiration === "") {
        alert("Please fill out all fields.");
        return;
    }

    // Send form data to the server
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "POST",    // Setting request method
        url: "api/payment", // Setting request url, which is mapped by PaymentServlet in PaymentServlet.java
        data: {
            firstName: firstName,
            lastName: lastName,
            ccId: creditCard,
            expiration: expiration
        },
        // Callback function to be called when the request is successful
        success: function(response) {
            window.location.href = "confirmation.html";
        },
        // Callback function to be called when the request fails
        error: function(error) {
            console.error("There was an error submitting the payment form: ", error);
            // display an error message to the user
            alert("There was an error submitting the payment form. Please try again.");
        }
    });
}

// Listen for submit event
document.getElementById("payment_form").addEventListener("submit", handlePaymentResult);