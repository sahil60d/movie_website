// employee login source

// Path: WebContent/_dashboard/employee-login.js
let login_form = $("#login_form");

/**
 * Handle the data returned by EmployeeLoginServlet
 * @param resultDataString jsonObject
 */

function handleEmployeeLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle employee login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to employee-dashboard.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("employee-dashboard.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */

function submitEmployeeLoginForm(formSubmitEvent) {
    console.log("submit employee login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));

    $.ajax(
        "../api/employee-login", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: login_form.serialize(),
            success: handleEmployeeLoginResult
        }
    );
}

// Bind the submit action of the form to a handler function
login_form.submit(submitEmployeeLoginForm);