package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    public MySqlShoppingCartDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        String sql= """
                SELECT s.quantity, p.* From shopping_cart as s
                Join products as p On s.product_id = p.product_id
                WHERE s.user_id= ?;
                """;
        ShoppingCart shoppingCart = new ShoppingCart();
        try (Connection connection = getConnection()){
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                ShoppingCartItem shoppingCartItem = mapRow(resultSet);
                shoppingCart.add(shoppingCartItem);
            }
            return shoppingCart;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }    }

    @Override
    public ShoppingCart addProduct(int userId, int productId) {
        return null;
    }

    @Override
    public void removeProduct(int userId, int productId) {

    }

    @Override
    public void clearCart(int userId) {

    }

    protected static ShoppingCartItem mapRow(ResultSet row) throws SQLException
    {
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

        Product product= new Product(productId, name, price, categoryId, description, color, stock, isFeatured, imageUrl);
        shoppingCartItem.setProduct(product);
        shoppingCartItem.setQuantity(quantity);

        return shoppingCartItem;
    }
}
