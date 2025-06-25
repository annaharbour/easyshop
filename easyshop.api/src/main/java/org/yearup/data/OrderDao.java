package org.yearup.data;

import org.yearup.models.Order;

public interface OrderDao {
    Order checkout(int userId);

    Order getOrderById(int orderId);
}
