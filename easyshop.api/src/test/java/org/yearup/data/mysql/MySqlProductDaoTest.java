package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yearup.models.Product;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MySqlProductDaoTest extends BaseDaoTestClass {
    private MySqlProductDao dao;

    @BeforeEach
    public void setup() {
        dao = new MySqlProductDao(dataSource);
    }


    @Test
    public void testGetById() {
        int productId = 1;
        Product expected = new Product() {{
            setProductId(1);
            setName("Smartphone");
            setPrice(new BigDecimal("499.99"));
            setCategoryId(1);
            setDescription("A powerful and feature-rich smartphone for all your communication needs.");
            setColor("Black");
            setStock(50);
            setFeatured(false);
            setImageUrl("smartphone.jpg");
        }};

        var actual = dao.getById(productId);

        assertEquals(expected.getPrice(), actual.getPrice(), "Returns correct product");
    }

    @Test
    public void testUpdate() {
        Product product = new Product();
        product.setName("UpdateTest");
        product.setPrice(new BigDecimal("15.00"));
        product.setCategoryId(1);
        product.setDescription("Before update");
        product.setColor("Blue");
        product.setStock(5);
        product.setFeatured(false);
        product.setImageUrl("update.jpg");

        Product created = dao.create(product);
        int id = created.getProductId();

        created.setName("UpdatedName");
        created.setPrice(new BigDecimal("20.00"));
        created.setDescription("After update");
        created.setColor("Green");
        created.setStock(8);
        created.setFeatured(true);
        created.setImageUrl("updated.jpg");

        dao.update(id, created);

        Product updated = dao.getById(id);
        assertEquals("UpdatedName", updated.getName());
        assertEquals(new BigDecimal("20.00"), updated.getPrice());
        assertEquals("After update", updated.getDescription());
        assertEquals("Green", updated.getColor());
        assertEquals(8, updated.getStock());
        assertTrue(updated.isFeatured());
        assertEquals("updated.jpg", updated.getImageUrl());
    }

    @Test
    public void testDelete() {
        Product product = new Product();
        product.setName("DeleteTest");
        product.setPrice(new BigDecimal("12.00"));
        product.setCategoryId(1);
        product.setDescription("To be deleted");
        product.setColor("Yellow");
        product.setStock(3);
        product.setFeatured(false);
        product.setImageUrl("delete.jpg");

        Product created = dao.create(product);
        int id = created.getProductId();

        assertNotNull(dao.getById(id));

        dao.delete(id);

        assertNull(dao.getById(id));
    }

    @Test
    public void testListByCategoryId() {
        int categoryId = 1;
        List<Product> products = dao.listByCategoryId(categoryId);
        assertNotNull(products);

        for (Product p : products) {
            assertEquals(categoryId, p.getCategoryId());
        }
    }

    @Test
    public void testSearch() {
        // Insert known product for search
        Product product = new Product();
        product.setName("SearchTest");
        product.setPrice(new BigDecimal("30.00"));
        product.setCategoryId(2);
        product.setDescription("Search test product");
        product.setColor("Black");
        product.setStock(10);
        product.setFeatured(false);
        product.setImageUrl("search.jpg");
        dao.create(product);

        // Search by categoryId
        List<Product> results = dao.search(2, null, null, null);
        assertFalse(results.isEmpty());
        for (Product p : results) {
            assertEquals(2, p.getCategoryId());
        }

//        Search by minPrice and maxPrice
        results = dao.search(null, new BigDecimal("80.00"), new BigDecimal("90.00"), null);
        assertTrue(results.stream().allMatch(p -> p.getPrice().compareTo(new BigDecimal("90.00")) <= 0 && p.getPrice().compareTo(new BigDecimal("80.00")) >= 0),
                "All products should have a price above or equal to minPrice and below or equal to maxPrice.");

        // Search by color
        results = dao.search(null, null, null, "Black");
        assertNotNull(results);
        for (Product p : results) {
            assertEquals("Black", p.getColor());
        }

        // Search with all parameters null (should return all products)
        results = dao.search(null, null, null, null);
        assertNotNull(results);
        assertFalse(results.isEmpty());


    }
}