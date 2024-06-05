let login_form = $("#login_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */

function handleLoginResult(resultDataString) {
    //let resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    //console.log(resultDataJson);
    //console.log(resultDataJson["status"]);
    console.log(resultDataString);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataString["status"] === "success") {
        window.location.replace("browse.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataString["message"]);
        $("#login_error_message").text(resultDataString["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/", 2));

    $.ajax(
        "api/login", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: login_form.serialize(),
            success: handleLoginResult
        }
    );
}

// Bind the submit action of the form to a handler function
login_form.submit(submitLoginForm);
