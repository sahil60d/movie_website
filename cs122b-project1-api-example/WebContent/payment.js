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
            first_name: firstName,
            last_name: lastName,
            credit_card: creditCard,
            expiration: expiration
        },
        success: (resultData) => handlePaymentResponse(resultData) // Setting callback function to handle data returned successfully by the PaymentServlet
    });
}

// Listen for submit event
document.getElementById("payment_form").addEventListener("submit", handlePaymentResult);