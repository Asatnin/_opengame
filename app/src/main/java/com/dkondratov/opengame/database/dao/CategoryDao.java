package com.dkondratov.opengame.database.dao;

import com.dkondratov.opengame.model.Category;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by andrew on 09.04.2015.
 */
public class CategoryDao extends BaseDaoImpl<Category, String> {

    public CategoryDao(ConnectionSource connectionSource, Class<Category> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Category> getAllCategories() throws SQLException {
        return queryForAll();
    }

    public void updateCategories(List<Category> categories) throws SQLException {
        delete(getAllCategories());
        for (Category category : categories) {
            create(category);
        }
    }
}
