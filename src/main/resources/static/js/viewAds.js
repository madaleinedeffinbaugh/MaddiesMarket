function handleClick(element){
    let id = element.getAttribute("id");
    window.location.href = "/viewAd/" + id;
}

document.getElementById('searchByCategory').addEventListener('change', function() {
    let selectedValue = this.value; // Retrieve the selected value
    if(selectedValue !== "normal") {
        window.location.href = "/ads-by-category?categoryId=" + selectedValue;
    }
});