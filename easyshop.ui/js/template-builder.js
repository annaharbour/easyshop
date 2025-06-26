let templateBuilder = {};

class TemplateBuilder
{
    // render a template with the given value and place it in the target element
    // if a callback is provided, it will be called after the template is rendered
    build(templateName, value, target, callback)
    {
        axios.get(`templates/${templateName}.html`)
            .then(response => {
                try
                {
                    const template = response.data;
                    const html = Mustache.render(template, value);
                    document.getElementById(target).innerHTML = html;

                    if(callback) callback();
                }
                catch(e)
                {
                    console.log(e);
                }
            })
    }

    // clear the target element
    clear(target)
    {
        document.getElementById(target).innerHTML = "";
    }

    // append a template with the given value to the target element
    append(templateName, value, target)
    {
        axios.get(`templates/${templateName}.html`)
             .then(response => {
                 try
                 {
                     const template = response.data;
                     const html = Mustache.render(template, value);

                     const element = this.createElementFromHTML(html);
                     const parent = document.getElementById(target);
                     parent.appendChild(element);
    // if the target is "errors", the element will be removed after 3 seconds
                     if(target == "errors")
                     {
                         setTimeout(() => {
                             parent.removeChild(element);
                         }, 3000);
                     }
                 }
                 catch(e)
                 {
                     console.log(e);
                 }
             })
    }

    // create an HTML element from a string
    // this is used to support multiple top-level nodes in the template
    createElementFromHTML(htmlString)
    {
        const div = document.createElement('div');
        div.innerHTML = htmlString.trim();

        // Change this to div.childNodes to support multiple top-level nodes.
        return div.firstChild;
    }

}

// Initialize the TemplateBuilder when the DOM is fully loaded
// This ensures that the templateBuilder variable is available globally
document.addEventListener('DOMContentLoaded', () => {
    templateBuilder = new TemplateBuilder();
});
