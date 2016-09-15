package com.dkondratov.opengame.database.dao;

import com.dkondratov.opengame.model.Field;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by andrew on 22.05.2015.
 */
public class FieldDao extends BaseDaoImpl<Field, String> {

    public FieldDao(ConnectionSource connectionSource, Class<Field> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Field> getAllFields() throws SQLException {
        return queryForAll();
    }

    public void removeFieldByID(String fieldID) throws SQLException {
        QueryBuilder<Field, String> queryBuilder = queryBuilder();
        queryBuilder.where().eq("field_id", fieldID);
        PreparedQuery<Field> preparedQuery = queryBuilder.prepare();
        List<Field> fields = query(preparedQuery);
        fields.removeAll(fields);
    }

    public List<Field> getFavoriteFields() throws SQLException {
        QueryBuilder<Field, String> queryBuilder = queryBuilder();
        queryBuilder.where().eq("favorite", true);
        PreparedQuery<Field> preparedQuery = queryBuilder.prepare();
        List<Field> favoriteFields = query(preparedQuery);
        return favoriteFields;
    }

    public List<Field> getAllFieldsBySportId(String sportId) throws SQLException {
        QueryBuilder<Field, String> queryBuilder = queryBuilder();
        queryBuilder.where().eq("sport_id", sportId);
        PreparedQuery<Field> preparedQuery = queryBuilder.prepare();
        List<Field> favoriteFields = query(preparedQuery);
        return favoriteFields;
    }

    public List<Field> getFieldById(String fieldID) throws SQLException {
        QueryBuilder<Field, String> queryBuilder = queryBuilder();
        queryBuilder.where().eq("field_id", fieldID);
        PreparedQuery<Field> preparedQuery = queryBuilder.prepare();
        List<Field> fields = query(preparedQuery);
        return fields;
    }
}
