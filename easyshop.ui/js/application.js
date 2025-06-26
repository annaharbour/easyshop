// show the login form in a modal dialog
// this function is called when the user clicks the login button
function showLoginForm()
{
    templateBuilder.build('login-form', {}, 'login');
}

// hide the login form in a modal dialog
// this function is called when the user clicks the cancel button or after a successful login
function hideModalForm()
{
    templateBuilder.clear('login');
}

// retrieve the user credentials from the form and call the login service
function login()
{
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    userService.login(username, password);
    hideModalForm()
}

// show the image detail form in a modal dialog
// this function is called when the user clicks on an image in the product list
function showImageDetailForm(product, imageUrl)
{
    const imageDetail = {
        name: product,
        imageUrl: imageUrl
    };

    templateBuilder.build('image-detail',imageDetail,'login')
}

// hide the image detail form in a modal dialog
// this function is called when the user clicks the close button in the image detail form
function loadHome()
{
    templateBuilder.build('home',{},'main')

    productService.search();
    categoryService.getAllCategories(loadCategories);
}


// show the profile form in a modal dialog
// this function is called when the user clicks the profile button
function editProfile()
{
    profileService.loadProfile();
}

// hide the profile form in a modal dialog
// this function is called when the user clicks the cancel button or after a successful profile update
function saveProfile()
{
    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const phone = document.getElementById("phone").value;
    const email = document.getElementById("email").value;
    const address = document.getElementById("address").value;
    const city = document.getElementById("city").value;
    const state = document.getElementById("state").value;
    const zip = document.getElementById("zip").value;

    const profile = {
        firstName,
        lastName,
        phone,
        email,
        address,
        city,
        state,
        zip
    };

    profileService.updateProfile(profile);
}

// show the cart page in the main content area
// this function is called when the user clicks the cart button
function showCart()
{
    cartService.loadCartPage();
}

// clear the cart and reload the cart page
// this function is called when the user clicks the clear cart button
function clearCart()
{
    cartService.clearCart();
    cartService.loadCartPage();
}

// set the selected category filter and reload the product list
// this function is called when the user selects a category from the dropdown
function setCategory(control)
{
    productService.addCategoryFilter(control.value);
    productService.search();

}

// setColor sets the selected color filter and updates the product list
// it is called when the user selects a color from the color filter
function setColor(control)
{
    productService.addColorFilter(control.value);
    productService.search();

}

// setMinPrice sets the minimum price filter and updates the product list
// it is called when the user changes the value of the min price slider
function setMinPrice(control)
{
    // const slider = document.getElementById("min-price");
    const label = document.getElementById("min-price-display")
    label.innerText = control.value;

    const value = control.value != 0 ? control.value : "";
    productService.addMinPriceFilter(value)
    productService.search();

}

// setMaxPrice sets the maximum price filter and updates the product list
// it is called when the user changes the value of the max price slider
function setMaxPrice(control)
{
    // const slider = document.getElementById("min-price");
    const label = document.getElementById("max-price-display")
    label.innerText = control.value;

    const value = control.value != 1500 ? control.value : "";
    productService.addMaxPriceFilter(value)
    productService.search();

}

// closeError closes the error message after 3 seconds
// this function is called when the user clicks the close button on an error message
function closeError(control)
{
    setTimeout(() => {
        control.click();
    },3000);
}

// Initialize the TemplateBuilder when the DOM is fully loaded
// This ensures that the templateBuilder variable is available globally
document.addEventListener('DOMContentLoaded', () => {

    loadHome();
});
