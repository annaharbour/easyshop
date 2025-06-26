


function loadCategories(categories)
{
    // This function populates the category select dropdown with options
    // It expects an array of category objects with properties: categoryId and name
    const select = document.getElementById('category-select');

    // Clear existing options
    categories.forEach(c => {
        const option = document.createElement('option');
        option.setAttribute('value', c.categoryId);
        option.innerText = c.name;
        select.appendChild(option);
    })
}

// Initialize the TemplateBuilder when the DOM is fully loaded
document.addEventListener('DOMContentLoaded', () => {
})
