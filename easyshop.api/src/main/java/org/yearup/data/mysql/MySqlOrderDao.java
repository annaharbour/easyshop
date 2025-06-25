package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {
    private final MySqlShoppingCartDao shoppingCartDao;
    private final MySqlProfileDao profileDao;

    public MySqlOrderDao(DataSource dataSource, MySqlProfileDao profileDao, MySqlShoppingCartDao shoppingCartDao) {
        super(dataSource);
        this.profileDao = profileDao;
        this.shoppingCartDao = shoppingCartDao;
    }

    public Order getOrderById(int orderId) {
        String sql = """
                SELECT * FROM orders
                WHERE order_id = ?;
                """;

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Order order = new Order();
                order.setOrderId(resultSet.getInt("order_id"));
                order.setUserId(resultSet.getInt("user_id"));
                order.setDate(resultSet.getString("date"));
                order.setAddress(resultSet.getString("address"));
                order.setCity(resultSet.getString("city"));
                order.setState(resultSet.getString("state"));
                order.setZip(resultSet.getString("zip"));
                order.setShippingAmount(resultSet.getBigDecimal("shipping_amount"));
                return order;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public Order checkout(int userId) {
        ShoppingCart cart = shoppingCartDao.getByUserId(
                userId);
        Profile profile = profileDao.getProfile(userId);
        if (cart == null || cart.getItems().isEmpty()) return null;

        String sql = """
                    INSERT INTO orders (user_id, date, address, city, state, zip, shipping_amount)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;


        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            statement.setString(2, String.valueOf(LocalDateTime.now()));
            statement.setString(3, profile.getAddress());
            statement.setString(4, profile.getCity());
            statement.setString(5, profile.getState());
            statement.setString(6, profile.getZip());
            statement.setBigDecimal(7, cart.getTotal().multiply(BigDecimal.valueOf(.03)));

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the generated keys
                ResultSet generatedKeys = statement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    // Retrieve the auto-incremented ID
                    int orderId = generatedKeys.getInt(1);
                    //Add order lines to database
                    for (ShoppingCartItem item : cart.getItems().values()) {
                        addOrderLines(item, orderId);
                    }
                    // get the newly inserted order
                    shoppingCartDao.clear(userId);
                    return getOrderById(orderId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return null;
    }

    protected void addOrderLines(ShoppingCartItem item, int orderId) {
        String sql = """
                    INSERT INTO order_line_items (order_id, product_id, sales_price, quantity, discount)
                    VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connection = getConnection()) {
            Product product = item.getProduct();
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setInt(1, orderId);
            statement.setInt(2, item.getProductId());
            statement.setInt(3, item.getQuantity());
            statement.setBigDecimal(4,
                    product.getPrice().subtract(product.getPrice().multiply(item.getDiscountPercent()).multiply(
                            BigDecimal.valueOf(item.getQuantity()))));
            statement.setBigDecimal(5, product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).multiply(
                    item.getDiscountPercent()));

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the generated keys
                ResultSet generatedKeys = statement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    // Retrieve the auto-incremented ID
                    int lineOrderItemId = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
