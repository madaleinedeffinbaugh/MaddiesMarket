let titleInput = document.getElementById("title");
let descriptionInput = document.getElementById("description");
let priceInput = document.getElementById("price");
let conditionInput = document.getElementById("condition");
let cityInput = document.getElementById("city");
let stateInput = document.getElementById("state");

let titleFeedback = document.getElementById("titleFeedback");
let descriptionFeedback = document.getElementById("descriptionFeedback");
let priceFeedback = document.getElementById("priceFeedback");
let conditionFeedback = document.getElementById("conditionFeedback");
let cityFeedback = document.getElementById("cityFeedback");
let stateFeedback = document.getElementById("stateFeedback");

const priceRegex = /^\d+(\.\d{1,2})?$/

addEventListeners();

let submitBtn = document.getElementById("edit");
let listingForm = document.getElementById("listingForm");
let picker;
let photoDiv = document.getElementById("photoList");
let photoUrlList = [];
let photoSection = document.getElementById("photoSection");

let oldPhotoSection = document.getElementById("oldPhotoSection");
let removePhotosList = [];

for (let i = 0; i < categoryIds.length; i++) {
    let currentCat = categoryIds[i];
    let selectStatement = 'button[value="' + currentCat + '"]';
    let buttonWithCurrentCategory = document.querySelector(selectStatement);
    buttonWithCurrentCategory.classList.remove("btn-outline-secondary");
    buttonWithCurrentCategory.classList.add("btn-secondary");
}


submitBtn.addEventListener("click", function (event) {
    event.preventDefault();
    let validAttempt = checkInputs();
    if (validAttempt) {
        let selectedCategories = [];
        let selected = document.querySelectorAll('.btn-secondary');
        for (let i = 0; i < selected.length; i++) {
            selectedCategories.push(selected[i].value);
        }

        const categoriesInput = document.getElementById('categoriesInput');
        const photosInput = document.getElementById('photosInput');
        const photosRemoveInput = document.getElementById('photosRemove');

        categoriesInput.value = JSON.stringify(selectedCategories);
        photosInput.value = JSON.stringify(photoUrlList);
        photosRemoveInput.value = JSON.stringify(removePhotosList);
        listingForm.submit();
    }
});

let catBtns = document.querySelectorAll(".categoryButtons");
for (let i = 0; i < catBtns.length; i++) {
    document.getElementById("" + i).addEventListener('click', function () {
        if (this.classList.contains("btn-outline-secondary")) {

            this.classList.remove("btn-outline-secondary");
            this.classList.add("btn-secondary");
        } else {
            this.classList.remove("btn-secondary");
            this.classList.add("btn-outline-secondary");
        }
    })
}

function updatePhotoList() {
    let htmlString = "";
    for (let i = 0; i < photoUrlList.length; i++) {
        htmlString +=
            '<div class="d-flex flex-column align-items-center m-1"><img src="' +
            photoUrlList[i] +
            '" alt="Uploaded Image" height="100">' +
            '<button class="button-remove removePhoto" data-index="' +
            i +
            '">Remove</button></div>';
    }
    if (photoUrlList.length > 0) {
        photoSection.classList.remove("d-none");
    } else {
        photoSection.classList.add("d-none");
    }
    $('#photoList').html(htmlString);
    attachRemovePhotoListeners();
}

function attachRemovePhotoListeners() {
    const removeButtons = document.querySelectorAll(".removePhoto");
    removeButtons.forEach((button) => {
        button.addEventListener("click", function () {
            const index = this.getAttribute("data-index");
            photoUrlList.splice(index, 1);
            updatePhotoList();
        });
    });
}

let confirmationPromptModal = document.getElementById('deleteConfirmationPrompt');
let confirmationModal = document.getElementById('deleteConfirmation');

function removePhotoClick(button) {
    let id = button.getAttribute("id");
    removePhotosList.push(id);
    let div = document.getElementById("photoDiv" + id);
    div.classList.add("d-none");
}

let deleteBtn = document.getElementById("deleteConfirmBtn");
deleteBtn.addEventListener("click", function (event) {
    event.preventDefault();
    let adId = document.getElementById("adId");
    deleteRequest(adId.value);
});


function deleteRequest(id) {
    fetch('/deleteAd/' + id, {
        method: 'GET',
    })
        .then(response => {
            if (response.ok) {
                // Handle success message from the server
                return response.text(); // This will read the response as text
            } else {
                // Handle other response statuses if needed
                throw new Error('Deletion failed');
            }
        })
        .then(message => {
            $(confirmationPromptModal).modal('hide');
            $(confirmationModal).modal('show');
        })
        .catch(error => {
            // Handle errors if any
            throw new Error('Deletion failed');
        });
}

let okayBtn = document.getElementById("okayBtn");
okayBtn.addEventListener("click", function () {
    window.location.href = "/ads";
})

function checkTitle() {
    let validInput = false;
    if (titleInput.value.trim() === "") {
        titleInput.classList.remove("is-valid")
        titleFeedback.classList.remove("valid-feedback")
        titleInput.classList.add("is-invalid")
        titleFeedback.classList.add("invalid-feedback")
        titleFeedback.innerText = "Title cannot be left blank";
    } else {
        titleInput.classList.remove("is-invalid")
        titleFeedback.classList.remove("invalid-feedback")
        titleInput.classList.add("is-valid")
        titleFeedback.classList.add("valid-feedback")
        titleFeedback.innerText = "Looks good!";
        validInput = true;
    }
    return validInput;
}

function checkDescription() {
    let validInput = false;
    if (descriptionInput.value.trim() === "") {
        descriptionInput.classList.remove("is-valid")
        descriptionFeedback.classList.remove("valid-feedback")
        descriptionInput.classList.add("is-invalid")
        descriptionFeedback.classList.add("invalid-feedback")
        descriptionFeedback.innerText = "Description cannot be left blank";
    } else {
        descriptionInput.classList.remove("is-invalid")
        descriptionFeedback.classList.remove("invalid-feedback")
        descriptionInput.classList.add("is-valid")
        descriptionFeedback.classList.add("valid-feedback")
        descriptionFeedback.innerText = "Looks good!";
        validInput = true;
    }
    return validInput;
}

function checkCity() {
    let validInput = false;
    if (cityInput.value.trim() === "") {
        cityInput.classList.remove("is-valid")
        cityFeedback.classList.remove("valid-feedback")
        cityInput.classList.add("is-invalid")
        cityFeedback.classList.add("invalid-feedback")
        cityFeedback.innerText = "City cannot be left blank";
    } else {
        cityInput.classList.remove("is-invalid")
        cityFeedback.classList.remove("invalid-feedback")
        cityInput.classList.add("is-valid")
        cityFeedback.classList.add("valid-feedback")
        cityFeedback.innerText = "Looks good!";
        validInput = true;
    }
    return validInput;
}

function checkPrice() {
    let validInput = false;
    if (priceInput.value.trim() === "") {
        priceInput.classList.remove("is-valid")
        priceFeedback.classList.remove("valid-feedback")
        priceInput.classList.add("is-invalid")
        priceFeedback.classList.add("invalid-feedback")
        priceFeedback.innerText = "Please enter a valid price";
    } else if (!priceRegex.test(priceInput.value)) {
        priceInput.classList.remove("is-valid")
        priceFeedback.classList.remove("valid-feedback")
        priceInput.classList.add("is-invalid")
        priceFeedback.classList.add("invalid-feedback")
        priceFeedback.innerText = "Please enter a valid price";
    } else {
        priceInput.classList.remove("is-invalid")
        priceFeedback.classList.remove("invalid-feedback")
        priceInput.classList.add("is-valid")
        priceFeedback.classList.add("valid-feedback")
        priceFeedback.innerText = "";
        validInput = true;
    }
    return validInput;
}

function checkCondition() {
    let validInput = false;
    const selectedCondition = conditionInput.value.trim();

    if (selectedCondition === "") {
        conditionInput.classList.remove("is-valid");
        conditionFeedback.classList.remove("valid-feedback");
        conditionInput.classList.add("is-invalid");
        conditionFeedback.classList.add("invalid-feedback");
        conditionFeedback.innerText = "Please select a condition.";
    } else {
        conditionInput.classList.remove("is-invalid");
        conditionFeedback.classList.remove("invalid-feedback");
        conditionInput.classList.add("is-valid");
        conditionFeedback.classList.add("valid-feedback");
        conditionFeedback.innerText = "";
        validInput = true;
    }
    return validInput;
}

function checkState() {
    let validInput = false;
    const selectedState = stateInput.value.trim();

    if (selectedState === "") {
        stateInput.classList.remove("is-valid");
        stateFeedback.classList.remove("valid-feedback");
        stateInput.classList.add("is-invalid");
        stateFeedback.classList.add("invalid-feedback");
        stateFeedback.innerText = "Please select a state.";
    } else {
        stateInput.classList.remove("is-invalid");
        stateFeedback.classList.remove("invalid-feedback");
        stateInput.classList.add("is-valid");
        stateFeedback.classList.add("valid-feedback");
        stateFeedback.innerText = "";
        validInput = true;
    }
    return validInput;
}

function addEventListeners() {
    titleInput.addEventListener("input", function () {
        checkTitle();
    });

    descriptionInput.addEventListener("input", function () {
        checkDescription();
    });

    cityInput.addEventListener("input", function () {
        checkCity();
    });

    priceInput.addEventListener("input", function () {
        checkPrice();
    });

    stateInput.addEventListener('change', function () {
        checkState();
    })

    conditionInput.addEventListener('change', function () {
        checkCondition()
    });
}

function checkInputs() {
    let titleOK = checkTitle();
    let descriptionOK = checkDescription();
    let priceOK = checkPrice();
    let cityOK = checkCity();
    let conditionOK = checkCondition();
    let stateOK = checkState();
    return titleOK && descriptionOK && priceOK && cityOK && conditionOK && stateOK;
}

window.addEventListener('DOMContentLoaded', function () {
    const apiKey = FILESTACK_KEY;
    const client = filestack.init(apiKey);

    const options = {
        accept: ["image/*"],
        onUploadDone: (res) => {
            const url = res.filesUploaded[0].url;
            console.log("Uploaded image URL: " + url);
            photoUrlList.push(url);
            updatePhotoList();
        },

    };


    document.getElementById("uploadPhoto").addEventListener("click", function (e) {
        e.preventDefault();
        picker = client.picker(options);
        client.picker(options).open();
    });

});

