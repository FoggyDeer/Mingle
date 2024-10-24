$(document).ready(function() {
    let passwordComplexityIndicator = document.querySelector("#password-complexity-indicator");
    passwordComplexityIndicator.setCopmlexity = function(complexity){
        this.complexity = complexity;
        let childs = this.children;

        for(let i = 0; i < childs.length; i++){
            childs[i].style.backgroundColor = null;
        }
        if(complexity >= 1){
            childs[0].style.backgroundColor = "#da483b";
        }
        if(complexity >= 2){
            childs[1].style.backgroundColor = "#ff9e0f";
        }
        if(complexity >= 3){
            childs[2].style.backgroundColor = "#ffc718";
        }
        if(complexity >= 4){
            childs[3].style.backgroundColor = "#22ca71";
        }
    }
    
    let username = document.querySelector("#username");
    username.setValid = function(valid){
        this.valid = valid;
        if(valid){
            document.querySelector("svg.valid").classList.add("visible");
        }else{
            document.querySelector("svg.invalid").classList.add("visible");
        }
    }
    username.valid = false
    username.taken = false

    $("#form").submit(function(e) {
        e.preventDefault();
        clearAllAlerts()
        if(!validateUserName() || !validatePasswords()){
            return;
        }

        let formData = $(this).find('input').not('#repeat-password').serialize();

        AppNavigator.register({
            formData: formData,
            success: AppNavigator.loadingPage,
            error: function (xhr, status, error) {
                if (xhr.status === 409) {
                    username.setValid(false)
                    username.taken = true
                    validateUserName()
                }
            }
        })
    });

    $("#back").click(function (e) { 
        window.location.href = "/Mingle/login";
    });

    $("#password").on("input", function(){
        $(this).val(this.value.match(/[a-zA-Z0-9]+/));
        passwordComplexityIndicator.setCopmlexity(getPasswordComplexity($(this).val()));
    });

    $("#repeat-password").on("input", function(){
        $(this).val(this.value.match(/[a-zA-Z0-9]+/));
    });

    $("#username").on("input", function(){
        document.querySelector("svg.valid").classList.remove("visible");
        document.querySelector("svg.invalid").classList.remove("visible");

        $(this).val(this.value.match(/[a-zA-Z0-9_][a-zA-Z0-9_\.\-]{0,24}/));
        
        if($(this).val().length < 3){
            username.setValid(false)
            return;
        }

        $.ajax({
            type: "POST",
            url: "/Mingle/register/userId",
            data: {username: $(this).val()},
            success: function (response) {
                username.setValid(true)
            },
            error: function (xhr) {
                if (xhr.status === 409) {
                    username.setValid(false)
                    username.taken = true
                }
            }
        });
    });
    $("#name").on("input", function(){
        $(this).val(this.value.match(/[a-zA-Z0-9_][a-zA-Z0-9_\.\-]{0,24}/));
    });
});

function validatePasswords(){

    if($("#password").val().length < 8){
        document.querySelector("#password-length-error").classList.add("visible");
        document.querySelector("#password").classList.add("highlighted");
        document.querySelector("#password").addEventListener("input", clearAllAlerts, {once: true});
        return false;
    }

    if(document.querySelector("#password-complexity-indicator").complexity < 3){
        document.querySelector("#password-complexity-error").classList.add("visible");
        document.querySelector("#password").classList.add("highlighted");
        document.querySelector("#password").addEventListener("input", clearAllAlerts, {once: true});
        return false;
    }
    
    if($("#repeat-password").val() != $("#password").val()){
        document.querySelector("#password-mismatch-error").classList.add("visible");
        document.querySelector("#repeat-password").classList.add("highlighted");
        document.querySelector("#repeat-password").addEventListener("input", clearAllAlerts, {once: true});
        return false;
    }
    return true;
}

function getPasswordComplexity(password){
    let complexity = 1;

    if(password.length >= 10){
        complexity++;
    }
    if(password.match(/[a-z]/) && password.match(/[A-Z]/)){
        complexity++;
    }
    if(password.match(/[0-9]/) && password.length > 13){
        complexity++;
    }

    return complexity;
}

function validateUserName(){
    let username = document.querySelector("#username");
    if(!username.valid){
        if(username.taken){
            document.querySelector("#username-taken-error").classList.add("visible");
            document.querySelector("#username").classList.add("highlighted");
            document.querySelector("#username").addEventListener("input", clearAllAlerts, {once: true});
        } else {
            document.querySelector("#username-length-error").classList.add("visible");
            document.querySelector("#username").classList.add("highlighted");
            document.querySelector("#username").addEventListener("input", clearAllAlerts, {once: true});
        }
        return false
    }
    return true;
}

function clearAllAlerts(){
    document.querySelector("#password-length-error").classList.remove("visible");
    document.querySelector("#password-complexity-error").classList.remove("visible");
    document.querySelector("#password-mismatch-error").classList.remove("visible");
    document.querySelector("#username-length-error").classList.remove("visible");
    document.querySelector("#username-taken-error").classList.remove("visible");

    document.querySelector("#password").classList.remove("highlighted");
    document.querySelector("#repeat-password").classList.remove("highlighted");
    document.querySelector("#username").classList.remove("highlighted");
}