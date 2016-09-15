package com.dkondratov.opengame.database;

import android.util.Log;

import com.dkondratov.opengame.database.dao.CategoryDao;
import com.dkondratov.opengame.database.dao.EventDao;
import com.dkondratov.opengame.database.dao.FieldDao;
import com.dkondratov.opengame.model.Event;
import com.dkondratov.opengame.model.Field;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static com.dkondratov.opengame.database.HelperFactory.getHelper;

public class DatabaseManager {

    private static FieldDao fieldDao;
    private static CategoryDao categoryDao;
    private static EventDao eventDao;

    public static void writeFields(List<Field> fields) throws SQLException {
        for (Field field : fields) {
            fieldDao.createOrUpdate(field);
        }
    }

    public static void writeEvent(Event event) throws SQLException {
        eventDao.createIfNotExists(event);
    }

    public static void updateField(Field field) throws SQLException {
        fieldDao.update(field);
    }

    public static List<Field> getAllFieldsBySportId(String sportId) throws SQLException {
        return fieldDao.getAllFieldsBySportId(sportId);
    }

    public static List<Field> getAllFieldsByFieldId(String fieldID) throws SQLException {
        return fieldDao.getFieldById(fieldID);
    }

    public static void removeFieldFromFavourite(String fieldID) throws SQLException {
        fieldDao.removeFieldByID(fieldID);
    }

    public static List<Event> getAllEvents() throws SQLException {
        if (eventDao == null) {
            return null;
        } else {
            return eventDao.getAllEvents();
        }
    }

    public static List<Field> getFavoriteFields() throws SQLException {
        return fieldDao.getFavoriteFields();
    }

    static {
        try {
            fieldDao = getHelper().getFieldDao();
            categoryDao = getHelper().getCategoryDao();
            eventDao = getHelper().getEventDao();
        } catch (SQLException e) {
//            Log.e("DAOInstantiationError", e.getSQLState());
        }
    }
}
