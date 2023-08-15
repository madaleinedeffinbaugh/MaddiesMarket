let firstNameInput = document.getElementById("first_name");
let lastNameInput = document.getElementById("last_name");
let usernameInput = document.getElementById("username");
let emailInput = document.getElementById("email");
let passwordInput = document.getElementById("password");
let passwordConfirmInput = document.getElementById("confirmPassword");


let firstNameFeedback = document.getElementById("firstNameFeedback");
let lastNameFeedback = document.getElementById("lastNameFeedback");
let usernameFeedback = document.getElementById("usernameFeedback");
let emailFeedback = document.getElementById("emailFeedback");
let newPasswordFeedback = document.getElementById("passwordFeedback");
let passwordConfirmFeedback = document.getElementById("confirmPasswordFeedback");


const usernameRegex = /^[a-zA-Z0-9_]{1,30}$/;
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const passwordRegex = /^(?=.*[0-9])(?=.*[!@#$%&])[a-zA-Z0-9!@#$%&]{8,20}$/;

let signUpForm = document.getElementById("signUpForm");
let signUpButton = document.getElementById("signUpBtn");


addEventListeners();

if(usernameTaken === true) {
    usernameInput.classList.add("is-invalid")
    usernameFeedback.classList.add("invalid-feedback")
    usernameFeedback.innerText = "Username is taken.";

}

if(emailTaken === true) {
    emailInput.classList.add("is-invalid")
    emailFeedback.classList.add("invalid-feedback")
    emailFeedback.innerText = "Email is taken.";
}



signUpButton.addEventListener("click", function(event){
    event.preventDefault();
    let validAttempt = checkInputs();
    if(validAttempt){
       signUpForm.submit();
    }
});


function checkInputs(){
    let firstNameOK = checkFirstName();
    let lastNameOk = checkLastName();
    let emailOk = checkEmail();
    let usernameOK = checkUsername();
    let passwordOK = checkPassword();
    let passwordConfirmOK = checkPasswordConfirm();
    return firstNameOK && lastNameOk && emailOk && usernameOK && passwordOK && passwordConfirmOK;
}


function checkFirstName() {
    let validInput = false;
    if (firstNameInput.value.trim() === "") {
        firstNameInput.classList.remove("is-valid")
        firstNameFeedback.classList.remove("valid-feedback")
        firstNameInput.classList.add("is-invalid")
        firstNameFeedback.classList.add("invalid-feedback")
        firstNameFeedback.innerText = "First name cannot be left blank";
    } else {
        firstNameInput.classList.remove("is-invalid")
        firstNameFeedback.classList.remove("invalid-feedback")
        firstNameInput.classList.add("is-valid")
        firstNameFeedback.classList.add("valid-feedback")
        firstNameFeedback.innerText = "Looks good!";
        validInput = true;
    }
    return validInput;
}
function checkLastName() {
    let validInput = false;
    if (lastNameInput.value.trim() === "") {
        lastNameInput.classList.remove("is-valid")
        lastNameFeedback.classList.remove("valid-feedback")
        lastNameInput.classList.add("is-invalid")
        lastNameFeedback.classList.add("invalid-feedback")
        lastNameFeedback.innerText = "Last name cannot be left blank";
    } else {
        lastNameInput.classList.remove("is-invalid")
        lastNameFeedback.classList.remove("invalid-feedback")
        lastNameInput.classList.add("is-valid")
        lastNameFeedback.classList.add("valid-feedback")
        lastNameFeedback.innerText = "Looks good!";
        validInput = true;
    }
    return validInput;
}
function checkUsername() {
    let validInput = false;
    if (usernameInput.value.trim() === "") {
        usernameInput.classList.remove("is-valid")
        usernameFeedback.classList.remove("valid-feedback")
        usernameInput.classList.add("is-invalid")
        usernameFeedback.classList.add("invalid-feedback")
        usernameFeedback.innerText = "Username cannot be left blank";
    } else if (!usernameRegex.test(usernameInput.value)) {
        usernameInput.classList.remove("is-valid")
        usernameFeedback.classList.remove("valid-feedback")
        usernameInput.classList.add("is-invalid")
        usernameFeedback.classList.add("invalid-feedback")
        usernameFeedback.innerText = "username should contain only letters, numbers, and underscores.";
    } else {
        usernameInput.classList.remove("is-invalid")
        usernameFeedback.classList.remove("invalid-feedback")
        usernameInput.classList.add("is-valid")
        usernameFeedback.classList.add("valid-feedback")
        usernameFeedback.innerText = "Looks good!";
        validInput = true;
    }
    return validInput;
}
function checkEmail() {
    let validInput = false;
    if (emailInput.value.trim() === "") {
        emailInput.classList.remove("is-valid")
        emailFeedback.classList.remove("valid-feedback")
        emailInput.classList.add("is-invalid")
        emailFeedback.classList.add("invalid-feedback")
        emailFeedback.innerText = "Email cannot be left balnk ";
    } else if (!emailRegex.test(emailInput.value)) {
        emailInput.classList.remove("is-valid")
        emailFeedback.classList.remove("valid-feedback")
        emailInput.classList.add("is-invalid")
        emailFeedback.classList.add("invalid-feedback")
        emailFeedback.innerText = "Emails should be in correct email format (xxx@xxx.xxx) ";
    } else {
        emailInput.classList.remove("is-invalid")
        emailFeedback.classList.remove("invalid-feedback")
        emailInput.classList.add("is-valid")
        emailFeedback.classList.add("valid-feedback")
        emailFeedback.innerText = "Looks good!";
        validInput = true;
    }
    return validInput;
}
function checkPassword() {
    let validInput = false;
    if (passwordInput.value.trim() === "") {
        passwordInput.classList.remove("is-valid")
        newPasswordFeedback.classList.remove("valid-feedback")
        passwordInput.classList.add("is-invalid")
        newPasswordFeedback.classList.add("invalid-feedback")
        newPasswordFeedback.innerText = "Password cannot be left blank";
    } else if (!passwordRegex.test(passwordInput.value)) {
        passwordInput.classList.remove("is-valid")
        newPasswordFeedback.classList.remove("valid-feedback")
        passwordInput.classList.add("is-invalid")
        newPasswordFeedback.classList.add("invalid-feedback")
        newPasswordFeedback.innerText = "Passwords must be 8-10 characters long, contain at least 1 uppercase and 1 lower case letter, and a special symbol (!@#$%&)";
    } else {
        passwordInput.classList.remove("is-invalid")
        newPasswordFeedback.classList.remove("invalid-feedback")
        passwordInput.classList.add("is-valid")
        newPasswordFeedback.classList.add("valid-feedback")
        newPasswordFeedback.innerText = "Looks Good!";
        validInput = true;
    }
    return validInput;
}
function checkPasswordConfirm() {
    let validInput = false;
    if (passwordConfirmInput.value.trim() === "") {
        passwordConfirmInput.classList.remove("is-valid")
        passwordConfirmFeedback.classList.remove("valid-feedback")
        passwordConfirmInput.classList.add("is-invalid")
        passwordConfirmFeedback.classList.add("invalid-feedback")
        passwordConfirmFeedback.innerText = "Password cannot be left blank";
    } else if (passwordInput.value !== passwordConfirmInput.value) {
        passwordConfirmInput.classList.remove("is-valid")
        passwordConfirmFeedback.classList.remove("valid-feedback")
        passwordConfirmInput.classList.add("is-invalid")
        passwordConfirmFeedback.classList.add("invalid-feedback")
        passwordConfirmFeedback.innerText = "Passwords do not match";
    } else {
        passwordConfirmInput.classList.remove("is-invalid")
        passwordConfirmFeedback.classList.remove("invalid-feedback")
        passwordConfirmInput.classList.add("is-valid")
        passwordConfirmFeedback.classList.add("valid-feedback")
        passwordConfirmFeedback.innerText = "Passwords match";
        validInput = true;
    }
    return validInput;
}
function addEventListeners() {
    firstNameInput.addEventListener("input", function () {
        checkFirstName();
    });

    lastNameInput.addEventListener("input", function () {
        checkLastName();
    });

    usernameInput.addEventListener("input", function () {
        checkUsername();
    });

    emailInput.addEventListener("input", function () {
        checkEmail();
    });

    passwordInput.addEventListener("input", function () {
        checkPassword();
    });

    passwordConfirmInput.addEventListener("input", function () {
        checkPasswordConfirm();
    });
    
}