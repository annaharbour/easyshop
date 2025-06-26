package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yearup.models.Order;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import static org.junit.jupiter.api.Assertions.*;

class MySqlOrderDaoTest extends BaseDaoTestClass {
    private MySqlShoppingCartDao cartDao;
    private MySqlProfileDao profileDao;
    private MySqlOrderDao orderDao;

    @BeforeEach
    public void setup() {
        cartDao = new MySqlShoppingCartDao(dataSource);
        profileDao = new MySqlProfileDao(dataSource);
        orderDao = new MySqlOrderDao(dataSource, profileDao, cartDao);
    }


    @Test
    void checkout() {
        ShoppingCart cart = cartDao.getByUserId(1);
        assertFalse(cart.getItems().isEmpty());
        orderDao.checkout(1);
        assertTrue(cartDao.getByUserId(1).getItems().isEmpty());
    }
}