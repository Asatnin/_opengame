package com.dkondratov.opengame.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dkondratov.opengame.database.dao.CategoryDao;
import com.dkondratov.opengame.database.dao.EventDao;
import com.dkondratov.opengame.database.dao.FieldDao;
import com.dkondratov.opengame.model.Category;
import com.dkondratov.opengame.model.Event;
import com.dkondratov.opengame.model.Field;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import static com.j256.ormlite.table.TableUtils.createTable;
import static com.j256.ormlite.table.TableUtils.createTableIfNotExists;

/**
 * Created by andrew on 09.04.2015.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private final static String DATABASE_NAME = "open_game.sqlite";
    private final static int DATABASE_VERSION = 1;

    //DAO instances
    private CategoryDao categoryDao;
    private FieldDao fieldDao;
    private EventDao eventDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            createTableIfNotExists(connectionSource, Category.class);
            createTableIfNotExists(connectionSource, Field.class);
            createTableIfNotExists(connectionSource, Event.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    }

    public CategoryDao getCategoryDao() throws SQLException {
        if (categoryDao == null) {
            categoryDao = new CategoryDao(getConnectionSource(), Category.class);
        }
        return categoryDao;
    }

    public FieldDao getFieldDao() throws SQLException {
        if (fieldDao == null) {
            fieldDao = new FieldDao(getConnectionSource(), Field.class);
        }
        return fieldDao;
    }

    public EventDao getEventDao() throws SQLException {
        if (eventDao == null) {
            eventDao = new EventDao(getConnectionSource(), Event.class);
        }
        return eventDao;
    }

    @Override
    public void close() {
        super.close();
    }
}
