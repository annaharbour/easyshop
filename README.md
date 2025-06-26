# 🛍️ Easy Shop E-Commerce App

E-Commerce Application for EasyShop. Website uses Spring Boot API for the backend, MySQL database for data storage, and JS/JQuery for Frontend. Includes authentication, product search (category, price range, color), and shopping cart functionality implemented on frontend and backend. Additionally the backend includes protected admin functions of adding/updating products and categories, checkout and fetching previous orders by their id. Future versions will include these backend features and admin portal. Additional features yet to implement include stripe checkout, responsive design, product ratings and comment, removal of products from cart, saving products for later, and user management for the admin. 

## 👚 App in Action
![add](https://github.com/user-attachments/assets/88effb09-2d4f-458d-83b4-59cee9c622c8)

![sort-black-electronics-max](https://github.com/user-attachments/assets/7b79ef9b-830e-4665-8eef-8de75aacb53a)

![sort-black-electronics-min](https://github.com/user-attachments/assets/19ad8a63-af8a-4c04-b47b-ac27fc00e16e)

![Screenshot 2025-06-26 15 26 16](https://github.com/user-attachments/assets/16a7bf46-8341-48b2-9f66-64e69850feca)

![Screenshot 2025-06-26 15 26 10](https://github.com/user-attachments/assets/fcf1b8dc-1d60-4c1a-9e7f-fc99a6a769f4)

![Screenshot 2025-06-26 14 18 34](https://github.com/user-attachments/assets/f84e953b-6b27-41df-8db6-b5b2805e8ad5)

![Screenshot 2025-06-26 14 19 42](https://github.com/user-attachments/assets/861f90ae-49b1-4113-8985-facd04bbf0d6)


## 💻 API Endpoints

### Categories

| Method | Endpoint                       | Body     | Description                          |
|--------|--------------------------------|----------|--------------------------------------|
| GET    | `/categories`                 | None     | Get all categories                   |
| GET    | `/categories/{id}`            | None     | Get a category by ID                 |
| POST   | `/categories`                 | Category | Create a new category                |
| PUT    | `/categorids/{id}`            | Category | Update an existing category (Note: likely a typo in "categorids") |
| DELETE | `/categorids/{id}`            | None     | Delete a category by ID (Note: likely a typo in "categorids")     |

### Products

| Method | Endpoint                      | Body     | Description                          |
|--------|-------------------------------|----------|--------------------------------------|
| GET    | `/products`                   | None     | Get all products                     |
| GET    | `/products/{id}`              | None     | Get a product by ID                  |
| POST   | `/products`                   | Category | Create a new product (Body may be product instead of category) |
| PUT    | `/products/{id}`              | Category | Update a product (Body may be product instead of category) |
| DELETE | `/products/{id}`              | None     | Delete a product by ID               |

### Shopping Cart

| Method | Endpoint                        | Body     | Description                          |
|--------|----------------------------------|----------|--------------------------------------|
| GET    | `/cart`                          | None     | Get current user's shopping cart     |
| POST   | `/cart/products/{productId}`     | None     | Add product to the shopping cart     |
| PUT    | `/cart/products/{productId}`     | Yes      | Update cart item (e.g., quantity)    |
| DELETE | `/cart`                          | None     | Empty the shopping cart              |

### User Profile

| Method | Endpoint       | Body    | Description                         |
|--------|----------------|---------|-------------------------------------|
| GET    | `/profile`     | None    | Get the current user's profile      |
| PUT    | `/profile`     | Profile | Update the current user's profile   |


### Orders

| Method | Endpoint       | Body | Description                     |
|--------|----------------|------|---------------------------------|
| POST   | `/orders`      | None | Place a new order for the current user |

## 🫖 Additional Code Fixes

### Categories Feature

***Rest Controller***

```
@RestController
@RequestMapping("categories")

@CrossOrigin
public class CategoriesController {
    private CategoryDao categoryDao;
    private ProductDao productDao;

    @Autowired
    public CategoriesController(ProductDao productDao, CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }
```

***Get All Request***

```
    
    @GetMapping("")
    @PreAuthorize("permitAll()")
    public List<Category> getAll() {
        try {
            return categoryDao.getAllCategories();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
```
***Get Category By Id Request***

```
    @GetMapping("{categoryId}")
    @PreAuthorize("permitAll()")
//    get category by id
    public Category getById(@PathVariable("categoryId") int id) {
        try {
            Category category = categoryDao.getById(id);
            if (category == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
            }
            return category;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }

    }

```
***Get Products by Category***
```
    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    @GetMapping("{categoryId}/products")
    @PreAuthorize("permitAll()")
    public List<Product> getProductsById(@PathVariable int categoryId) {
        try {
            return productDao.listByCategoryId(categoryId);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
```
***Add Category***
```
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Category addCategory(@RequestBody Category category) {
        // insert the category
        {
            try {
                return categoryDao.create(category);
            } catch (Exception ex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
            }
        }
    }
```
***Update Category***
```
    @PutMapping("{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateCategory(@PathVariable("categoryId") int id, @RequestBody Category category) {
        // update the category by id
        try {
            if (categoryDao.getById(id) == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            categoryDao.update(id, category);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }

    }
```
***Delete Category***
```    
    @DeleteMapping("{categoryId}")
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("categoryId") int id) {
        // delete the category by id
        try {
            var category = categoryDao.getById(id);

            if (category == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            categoryDao.delete(id);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
```

### Products Bug 1

### Products Bug 2

### N
