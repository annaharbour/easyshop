package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here
    ShoppingCart addProduct(int userId, int productId);
    ShoppingCart update(int userId, int productId, int quantity);
    void clear(int userId);
}
