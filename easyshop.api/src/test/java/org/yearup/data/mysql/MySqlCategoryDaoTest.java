package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

class MySqlCategoryDaoTest extends BaseDaoTestClass {
    private MySqlCategoryDao dao;

    @BeforeEach
    public void setup() {
        dao = new MySqlCategoryDao(dataSource);
    }

    @Test
    void getById() {
        int categoryId = 1;
        Category expected = new Category() {{
            setCategoryId(1);
            setName("Electronics");
            setDescription("Explore the latest gadgets and electronic devices.");
        }};

        var actual = dao.getById(categoryId);

        assertEquals(expected.getName(), actual.getName(), "Returns correct product");
        assertEquals(expected.getDescription(), actual.getDescription(), "Returns correct product");
    }

    @Test
    void testUpdate() {
        Category category = new Category();
        category.setName("UpdateTest");
        category.setCategoryId(1);
        category.setDescription("Before update");

        Category created = dao.create(category);
        int id = created.getCategoryId();

        created.setName("UpdatedName");
        created.setDescription("After update");
        dao.update(id, created);

        Category updated = dao.getById(id);
        assertEquals("UpdatedName", updated.getName());
        assertEquals("After update", updated.getDescription());
    }

    @Test
    void testDelete() {
        Category category = new Category();
        category.setName("DeleteTest");
        category.setCategoryId(1);
        category.setDescription("To be deleted");

        Category created = dao.create(category);
        int id = created.getCategoryId();

        assertNotNull(dao.getById(id));
        dao.delete(id);

        assertNull(dao.getById(id));
    }
}