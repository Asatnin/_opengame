package com.dkondratov.opengame.database.dao;

import com.dkondratov.opengame.model.Event;
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
public class EventDao extends BaseDaoImpl<Event, String> {

    public EventDao(ConnectionSource connectionSource, Class<Event> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public List<Event> getAllEvents() throws SQLException {
        return queryForAll();
    }

}
