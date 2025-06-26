package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class MySqlShoppingCartDaoTest extends BaseDaoTestClass {
    private MySqlShoppingCartDao dao;
    private MySqlProductDao productDao;

    @BeforeEach
    public void setup() {
        dao = new MySqlShoppingCartDao(dataSource);
        productDao = new MySqlProductDao(dataSource);
    }

    @Test
    void getByUserId() {
        ShoppingCart cart = dao.getByUserId(1);
        assertNotNull(cart);
        assertEquals(5, cart.getItems().size(), "The cart should contain 5 items.");
    }

    @Test
    void addProduct() {
        ShoppingCart initialCart = dao.getByUserId(2);
        assertNotNull(initialCart);
        assertTrue(initialCart.getItems().isEmpty(), "The cart should initially be empty.");

        int productId = 1;
        ShoppingCart updatedCart = dao.addProduct(1, productId);

        assertNotNull(updatedCart);
        assertTrue(updatedCart.contains(productId), "The cart should contain the added product.");
    }

    @Test
    void update() {
        int userId = 2;
        int productId = 1;
        Product product = productDao.getById(productId);
        int newQuantity = 3;

        ShoppingCart initialCart = dao.getByUserId(userId);
        assertTrue(initialCart.getItems().isEmpty());
        dao.addProduct(userId, productId);

        dao.update(userId, productId, newQuantity);
        BigDecimal expectedTotal = product.getPrice()
                .multiply(BigDecimal.valueOf(newQuantity))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal actualTotal = dao.getByUserId(userId)
                .getTotal()
                .setScale(2, RoundingMode.HALF_UP);

        assertEquals(expectedTotal, actualTotal, "The updated total should be quantity × product price.");
    }



    @Test
    void clear() {
        int userId = 1;
        ShoppingCart initialCart = dao.getByUserId(userId);
        assertFalse(initialCart.getItems().isEmpty());

        dao.clear(userId);
        ShoppingCart clearedCart = dao.getByUserId(userId);
        assertTrue(clearedCart.getItems().isEmpty(), "The cart should be empty after clearing.");
    }
}