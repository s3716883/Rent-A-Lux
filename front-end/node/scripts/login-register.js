var x = document.getElementById("login");
var y = document.getElementById("register");
var z = document.getElementById("btn");

const baseURL = "http://ec2-35-168-2-168.compute-1.amazonaws.com:7000/api/";
//email validation
const email = document.querySelector("#loginEmail");
const RegEmail = document.querySelector("#registerEmail");
const error = document.querySelector(".error-text");
const RegError = document.querySelector(".error-text-two");
let regExp = /^[^ ]+@[^ ]+\.[a-z]{2,3}$/;

function register(){
    x.style.left = "-400px";
    y.style.left = "50px";
    z.style.left = "110px";
}
function login(){
    x.style.left = "50px";
    y.style.left = "450px";
    z.style.left = "0px";
}

//email validation
function check(){
    if(email.value.match(regExp)){
        email.style.borderColor = "green";
        error.style.display = "none";

    }else{
        email.style.borderColor = "#e74c3c";
        error.style.display = "block";
    }
    if(email.value == ""){
        email.style.borderColor = "rgb(72, 158, 238)";
        error.style.display = "none";

    }

}
//validation in register email id
function checkReg(){
    if(RegEmail.value.match(regExp)){
        RegEmail.style.borderColor = "green";
        RegError .style.display = "none";

    }else{
        RegEmail.style.borderColor = "#e74c3c";
        RegError .style.display = "block";
    }
    if(RegEmail.value == ""){
        RegEmail.style.borderColor = "rgb(72, 158, 238)";
        RegError .style.display = "none";

    }

}

//This function pretty much says 'hey run this when someone pushes submit on the "Register" form'
$(function() {
    $('#register').submit(function() {
        //Clear the errors (makes it clear to the user that it is loading)
        $("#generic_error")[0].innerHTML = "";
        //We need to setup our formData which is pretty much a capsule of data that gets sent to the backend 
        //$("#ID")   <-- This is the format to use Jquery to get a HTML element
        formData = {email:      $("#email").val(), 
                    password:   $("#password").val(),
                    first_name: $("#first_name").val(),
                    last_name:  $("#last_name").val()}

        //Then we send a POST request to the server, using formData as data and making a callback function to be called when we get a response to our request
        $.ajax({
            type: 'POST',
            url: baseURL + 'user/create',
            data: formData,
            complete: registerCallback
        });
        //Returning false stops the web browser from redirecting the user (since they submitted a from, this is the default behavior)
        return false;
    }); 
})
//Then we have our callback function which gets called when our request gets responded to
function registerCallback(data){
    //We weed out all the data we dont care about
    data = data.responseJSON
    //If status is failed, then that means that something went wrong, so we print the errror (just into the generic error spot for now)
    if (data.status == "failed"){
        $("#generic_error")[0].innerHTML = data.message;
        console.log(data.message)
    }
    //if the status isnt failed then that means that the request accepted
    else{
        console.log("Made account")
        window.location.replace("/registered.html");    //Temp page for demo
    }
}

$(function() {
    $('#login').submit(function() {
        $("#login_error")[0].innerHTML = "";
        formData = {email:      $("#loginEmail").val(), 
                    password:   $("#loginPassword").val()}
        $.ajax({
            type: 'POST',
            url: baseURL + 'checkLogin',
            data: formData,
            complete: loginCallback
        });
        return false;
    }); 
})

function loginCallback(data){
    data = data.responseJSON
    console.log(data)
    if (data.status == "failed"){
        $("#login_error")[0].innerHTML = data.message;
        console.log(data.message)
    }
    else{
        localStorage.setItem("email", $("#loginEmail").val());
        localStorage.setItem("password",$("#loginPassword").val());
        localStorage.setItem("account_type",data.payload.account_type);
        window.location.replace("/landing"); //Temp page for demo
    }
}