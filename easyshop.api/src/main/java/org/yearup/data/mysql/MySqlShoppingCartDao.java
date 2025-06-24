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
                String updateSql = """
                            UPDATE shopping_cart
                            SET quantity = quantity + 1
                            WHERE user_id = ? AND product_id = ?;
                        """;
                try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
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
