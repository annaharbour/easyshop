package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

// add the annotations to make this a REST controller
@RestController
// add the annotation to make this controller the endpoint for the following url
// http://localhost:8080/categories
@RequestMapping("categories")
// add annotation to allow cross site origin requests
@CrossOrigin
public class CategoriesController {
    private CategoryDao categoryDao;
    private ProductDao productDao;


    // create an Autowired controller to inject the categoryDao and ProductDao
    @Autowired
    public CategoriesController(ProductDao productDao, CategoryDao categoryDao)
    {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }


    // add the appropriate annotation for a get action
    @GetMapping("")
    @PreAuthorize("permitAll()")
    public List<Category> getAll() {
        return categoryDao.getAllCategories();
    }

    // add the appropriate annotation for a get action
    @GetMapping("/id/{id}")
    @PreAuthorize("permitAll()")
    public Category getById(@PathVariable int id) {
        return categoryDao.getById(id);
    }

    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    @GetMapping("{categoryId}")
    @PreAuthorize("permitAll()")
    public List<Product> getProductsById(@PathVariable int categoryId) {
        // TODO: get a list of product by categoryId
        return null;
    }

    // add annotation to call this method for a POST action
    @PostMapping("{categoryId}")
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Category addCategory(@RequestBody Category category) {
        // insert the category
        return null;
    }

    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    @PutMapping("{categoryId}")
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateCategory(@PathVariable int id, @RequestBody Category category) {
        // TODO: update the category by id
    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    @DeleteMapping("{categoryId}")
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCategory(@PathVariable int id) {
        // TODO: delete the category by id
    }
}
