$(document).ready(function() {
    $("#form").submit(function(e) {
        e.preventDefault();

        let formData = $(this).serialize();

        AppNavigator.login({
            formData: formData,
            success: () => {
                AppNavigator.openApp({
                    success: function(response) {
                        AppNavigator.loadingPage();
                    },
                    error: function(xhr, status, error) {
                        if (xhr.status === 401) {
                            if(xhr.getResponseHeader('AccessToken-Expired') === 'true'){
                                AppNavigator.updateRefreshToken(xhr.getResponseHeader('Location'));
                            } else {
                                window.location.href = xhr.getResponseHeader('Location');
                            }
                        } 
                    }
                })
            }, 
            error: function (xhr, status, error) {
                if (xhr.status === 401) {
                    setAlertVisible("Invalid username or password. Please try again.");
                }
            }
        })
    });

    $("#register").click(function (e) {
        AppNavigator.registerPage()
    });
});


function setAlertVisible(string) {
    document.querySelector("#login_error").querySelector('p').innerHTML = string;
    document.querySelector("#login_error").classList.add("visible");
    document.querySelector("#submit").disabled = true;

    Array.from(document.querySelectorAll("input")).forEach(function(element) {
        element.classList.add("highlighted")
    });

    document.querySelector("#username").addEventListener("input", typed, {once: true});
    document.querySelector("#password").addEventListener("input", typed, {once: true});
}


function typed() {
    Array.from(document.querySelectorAll("input")).forEach(function(element) {
        element.classList.remove("highlighted")
    });
    document.querySelector("#submit").disabled = false;
    document.querySelector("#login_error").classList.remove("visible");
    document.querySelector("#username").removeEventListener("input", typed);
    document.querySelector("#password").removeEventListener("input", typed); 
}