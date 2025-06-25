package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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