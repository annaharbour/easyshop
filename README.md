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

### 🔎 Categories Feature

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

### 🪲 Products Bug 1

Users reported that the product search functionality was returning incorrect
results. Tested the search logic to find and fix the bug.

```
    public List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String color)
    {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products " +
                "WHERE (category_id = ? OR ? = -1) " +
                "   AND (price >= ? OR ? = -1) " +
                "   AND (price <= ? OR ? = -1) " +
                "   AND (color = ? OR ? = '') ";

        categoryId = categoryId == null ? -1 : categoryId;
        minPrice = minPrice == null ? new BigDecimal("-1") : minPrice;
        maxPrice = maxPrice == null ? new BigDecimal("1500") : maxPrice;
        color = color == null ? "" : color;

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);
            statement.setInt(2, categoryId);
            statement.setBigDecimal(3, minPrice);
            statement.setBigDecimal(4, minPrice);
            statement.setBigDecimal(5, maxPrice);
            statement.setBigDecimal(6, maxPrice);
            statement.setString(7, color);
            statement.setString(8, color);

            ResultSet row = statement.executeQuery();

            while (row.next())
            {
                Product product = mapRow(row);
                products.add(product);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }

        return products;
    }

```

### 🐛 Products Bug 2
Some users also noticed that some of the products seemed to be duplicated.
For example, a laptop is listed 3 times, and it appeared to be the same product, but
there were slight differences, such as the description or the price.

```
public void update(int productId, Product product)
    {
        String sql = "UPDATE products" +
                " SET name = ? " +
                "   , price = ? " +
                "   , category_id = ? " +
                "   , description = ? " +
                "   , color = ? " +
                "   , image_url = ? " +
                "   , stock = ? " +
                "   , featured = ? " +
                " WHERE product_id = ?;";

        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, product.getName());
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getCategoryId());
            statement.setString(4, product.getDescription());
            statement.setString(5, product.getColor());
            statement.setString(6, product.getImageUrl());
            statement.setInt(7, product.getStock());
            statement.setBoolean(8, product.isFeatured());
            statement.setInt(9, productId);

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
```

### 🛒 Shopping Cart
New Feature
Logged-in users should have the ability to add items to their shopping cart, get their shopping cart, and clear their shopping cart. This is a new
feature that has not yet been implemented. 
```
@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        String sql = """
                SELECT s.quantity, p.* From shopping_cart as s
                Join products as p On s.product_id = p.product_id
                WHERE s.user_id= ?;
                """;
        ShoppingCart shoppingCart = new ShoppingCart();
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ShoppingCartItem shoppingCartItem = mapRow(resultSet);
                shoppingCart.add(shoppingCartItem);
            }
            return shoppingCart;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ShoppingCart addProduct(int userId, int productId) {
        ShoppingCart cart = getByUserId(userId);

        try (Connection connection = getConnection()) {
            if (cart.contains(productId)) {
                String sql = """
                            UPDATE shopping_cart
                            SET quantity = quantity + 1
                            WHERE user_id = ? AND product_id = ?;
                        """;
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, userId);
                    statement.setInt(2, productId);
                    statement.executeUpdate();
                }
            } else {
                String insertSql = """
                            INSERT INTO shopping_cart (user_id, product_id, quantity)
                            VALUES (?, ?, 1);
                        """;
                try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                    statement.setInt(1, userId);
                    statement.setInt(2, productId);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return getByUserId(userId);
    }


    @Override
    public ShoppingCart update(int userId, int productId, int quantity) {
        try (Connection connection = getConnection()) {
            if (quantity == 0) {
                String deleteSql = """
                    DELETE FROM shopping_cart
                    WHERE user_id = ? AND product_id = ?;
                    """;
                try (PreparedStatement statement = connection.prepareStatement(deleteSql)) {
                    statement.setInt(1, userId);
                    statement.setInt(2, productId);
                    statement.executeUpdate();
                }
            } else {
                // Update the quantity if it's greater than 0
                String updateSql = """
                    UPDATE shopping_cart
                    SET quantity = ?
                    WHERE user_id = ? AND product_id = ?;
                    """;
                try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
                    statement.setInt(1, quantity);
                    statement.setInt(2, userId);
                    statement.setInt(3, productId);
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return getByUserId(userId);
    }


    // After
    @Override
    public void clear(int userId) {
        String sql = """
                DELETE FROM shopping_cart
                WHERE user_id = ?;
                """;

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected static ShoppingCartItem mapRow(ResultSet row) throws SQLException {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();

        int productId = row.getInt("product_id");
        String name = row.getString("name");
        BigDecimal price = row.getBigDecimal("price");
        int categoryId = row.getInt("category_id");
        String description = row.getString("description");
        String color = row.getString("color");
        int stock = row.getInt("stock");
        boolean isFeatured = row.getBoolean("featured");
        String imageUrl = row.getString("image_url");
        int quantity = row.getInt("quantity");

        Product product = new Product(productId, name, price, categoryId, description, color, stock, isFeatured,
                imageUrl);
        shoppingCartItem.setProduct(product);
        shoppingCartItem.setQuantity(quantity);

        return shoppingCartItem;
    }
}
```
### 💸 Checkout
When a user is ready to check out, their shopping cart converts into an
order which is entered into the database and the cart is cleared. 

***Order Model Constructor***
```
    public Order(int orderId, int userId, String date, String address, String city, String state, String zip,
                 BigDecimal shippingAmount) {
        this.orderId = orderId;
        this.userId = userId;
        this.date = date;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.shippingAmount = shippingAmount;
    }
```
***Order Item Model Constructor***
```
 public OrderLineItem(int orderLineItemId, int orderId, int productId, BigDecimal salesPrice, int quantity,
                         BigDecimal discount) {
        this.orderLineItemId = orderLineItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.salesPrice = salesPrice;
        this.quantity = quantity;
        this.discount = discount;
    }

```
***Order MySQL Dao***
```
@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {
    private final MySqlShoppingCartDao shoppingCartDao;
    private final MySqlProfileDao profileDao;

    public MySqlOrderDao(DataSource dataSource, MySqlProfileDao profileDao, MySqlShoppingCartDao shoppingCartDao) {
        super(dataSource);
        this.profileDao = profileDao;
        this.shoppingCartDao = shoppingCartDao;
    }

    @Override
    public Order getOrderById(int orderId) {
        String sql = """
                SELECT o.order_id, o.user_id, o.date, o.address, o.city, o.state, o.zip, o.shipping_amount,
                       oli.line_item_id, oli.product_id, oli.sales_price, oli.quantity, oli.discount
                FROM orders o
                LEFT JOIN order_line_items oli ON o.order_id = oli.order_id
                WHERE o.order_id = ?
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();

            Order order = null;
            List<OrderLineItem> items = new ArrayList<>();

            while (resultSet.next()) {
                if (order == null) {
                    order = new Order();
                    order.setOrderId(resultSet.getInt("order_id"));
                    order.setUserId(resultSet.getInt("user_id"));
                    order.setDate(resultSet.getString("date"));
                    order.setAddress(resultSet.getString("address"));
                    order.setCity(resultSet.getString("city"));
                    order.setState(resultSet.getString("state"));
                    order.setZip(resultSet.getString("zip"));
                    order.setShippingAmount(resultSet.getBigDecimal("shipping_amount"));
                }

                int productId = resultSet.getInt("product_id");
                if (resultSet.next()) {
                    OrderLineItem item = new OrderLineItem();
                    item.setOrderLineItemId(resultSet.getInt("line_item_id"));
                    item.setOrderId(resultSet.getInt("order_id"));
                    item.setProductId(productId);
                    item.setSalesPrice(resultSet.getBigDecimal("sales_price"));
                    item.setQuantity(resultSet.getInt("quantity"));
                    item.setDiscount(resultSet.getBigDecimal("discount"));
                    items.add(item);
                }
            }

            if (order != null) {
                order.setOrderLineItems(items);
            }

            return order;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Order checkout(int userId) {
        ShoppingCart cart = shoppingCartDao.getByUserId(userId);
        Profile profile = profileDao.getProfile(userId);
        if (cart == null || cart.getItems().isEmpty()) return null;

        String sql = """
                    INSERT INTO orders (user_id, date, address, city, state, zip, shipping_amount)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, userId);
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(3, profile.getAddress());
            statement.setString(4, profile.getCity());
            statement.setString(5, profile.getState());
            statement.setString(6, profile.getZip());
            statement.setBigDecimal(7, cart.getTotal().multiply(BigDecimal.valueOf(.03)));

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);

                    List<OrderLineItem> orderLineItems = new ArrayList<>();
                    for (ShoppingCartItem item : cart.getItems().values()) {
                        OrderLineItem orderLineItem = addOrderLines(item, orderId);
                        if (orderLineItem != null) {
                            orderLineItems.add(orderLineItem);
                        }
                    }

                    Order order = new Order();
                    order.setOrderId(orderId);
                    order.setUserId(userId);
                    order.setDate(LocalDateTime.now().toString());
                    order.setAddress(profile.getAddress());
                    order.setCity(profile.getCity());
                    order.setState(profile.getState());
                    order.setZip(profile.getZip());
                    order.setShippingAmount(cart.getTotal().multiply(BigDecimal.valueOf(.03)));
                    order.setOrderLineItems(orderLineItems);

                    shoppingCartDao.clear(userId);

                    return order;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    protected OrderLineItem addOrderLines(ShoppingCartItem item, int orderId) {
        String sql = """
                    INSERT INTO order_line_items (order_id, product_id, sales_price, quantity, discount)
                    VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            Product product = item.getProduct();

            BigDecimal salesPrice = product.getPrice().subtract(
                    product.getPrice().multiply(item.getDiscountPercent())
                            .multiply(BigDecimal.valueOf(item.getQuantity()))
            );
            BigDecimal discount = product.getPrice().multiply(
                    BigDecimal.valueOf(item.getQuantity())).multiply(item.getDiscountPercent());

            statement.setInt(1, orderId);
            statement.setInt(2, item.getProductId());
            statement.setBigDecimal(3, salesPrice);
            statement.setInt(4, item.getQuantity());
            statement.setBigDecimal(5, discount);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int lineOrderItemId = generatedKeys.getInt(1);

                    OrderLineItem orderLineItem = new OrderLineItem();
                    orderLineItem.setOrderLineItemId(lineOrderItemId);
                    orderLineItem.setOrderId(orderId);
                    orderLineItem.setProductId(item.getProductId());
                    orderLineItem.setQuantity(item.getQuantity());
                    orderLineItem.setSalesPrice(salesPrice);
                    orderLineItem.setDiscount(discount);
                    return orderLineItem;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
```
