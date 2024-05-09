// This script uses jQuery to make an AJAX request to the CartServlet

// Loads the cart items from the server and displays them in a table when page is loaded
$(document).ready(function() {
    // This function runs when the page is loaded
    getCartItems();
});

// Function to get cart items from the server and display them in a table
function getCartItems() {
    jQuery.ajax({
        method: "GET",
        url: "api/cart", // The URL to CartServlet
        success: function(response) {
            // Assuming the servlet returns a JSON array of cart items
            var cartTableBody = $("#movie_table_body");
            cartTableBody.empty(); // Clear existing contents

            // get array of cart items from the response object cart
            var cart = JSON.parse(response);

            if (response.length === 0) {
                cartTableBody.append("<tr><td colspan='4'>Your cart is empty.</td></tr>");
            } else {

                // Calculate the total price of the items in the cart
                var totalPrice = 0;

                for(var i = 0; i < cart.length; i++) {
                    let rowHTML = "";
                    rowHTML += "<tr>";
                    rowHTML += "<td>" + cart[i]["title"] + "</td>";
                    rowHTML += "<td>" + cart[i]["price"] + "</td>";

                    rowHTML += "<td>" +
                        "<button onclick='changeQuantity(\"" + cart[i]["title"] + "\", \"-\")'>-</button>" +
                        " " + cart[i]["quantity"] + " " +
                        "<button onclick='changeQuantity(\"" + cart[i]["title"] + "\", \"+\")'>+</button>" +
                        "</td>";

                    rowHTML += "<td>" + cart[i]["total"] + "</td>";

                    // Delete button
                    rowHTML += "<td><button onclick='deleteItem(\"" + cart[i]["title"] + "\")'>Delete</button></td>";

                    rowHTML += "</tr>";

                    cartTableBody.append(rowHTML);

                    // Update the total price
                    totalPrice += parseFloat(cart[i]["total"]);

                }

                // Optionally, append a row for the total price
                cartTableBody.append("<tr><th colspan='3'>Total</th><td>" + totalPrice.toFixed(2) + "</td></tr>");
            }
        },
        error: function(error) {
            // Handle any errors here
            console.error("There was an error retrieving the cart items: ", error);
        }
    });
}

// Function to change the quantity of an item in the cart
function changeQuantity(title, change) {
    jQuery.ajax({
        method: "POST",
        url: "api/cart",
        data: {
            title: title,
            change: change
        },
        success: function(response) {
            // Refresh the cart items table
            getCartItems();
        },
        error: function(error) {
            // Handle any errors here
            console.error("There was an error changing the quantity: ", error);
        }
    });
}

// Function to delete an item from the cart
function deleteItem(title) {
    jQuery.ajax({
        method: "POST",
        url: "api/cart",
        data: {
            title: title,
            change: "delete"
        },
        success: function(response) {
            // Refresh the cart items table
            getCartItems();
        },
        error: function(error) {
            // Handle any errors here
            console.error("There was an error deleting the item: ", error);
        }
    });
}