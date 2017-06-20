/*
 * This file is part of QuarterMAP.
 * Copyright (c) 2014 QuarterCode <http://quartercode.com/>
 *
 * QuarterMAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QuarterMAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with QuarterMAP. If not, see <http://www.gnu.org/licenses/>.
 */

package com.quartercode.quartermap.service;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import com.quartercode.quartermap.QuarterMAP;
import com.quartercode.quartermap.util.ReflectionUtil;
import com.quartercode.quartermap.util.SessionUtil;

public class EntityService {

    public EntityService() {

    }

    public <T> List<T> getAllEntities(Class<T> entityType) {

        Session session = QuarterMAP.getSessionFactory().openSession();
        Query query = session.createQuery("FROM " + entityType.getName());
        List<T> entities = ReflectionUtil.castAndPopulate(query.list(), new ArrayList<T>(), entityType);
        session.close();

        return entities;
    }

    public <T> List<T> getEntitiesByProperty(Class<T> entityType, String property, Object value) {

        Session session = QuarterMAP.getSessionFactory().openSession();
        Query query = session.createQuery("FROM " + entityType.getName() + " WHERE " + property + " = '" + value + "'");
        List<T> entities = ReflectionUtil.castAndPopulate(query.list(), new ArrayList<T>(), entityType);
        session.close();

        return entities;
    }

    public <T> T getFirstEntityByProperty(Class<T> entityType, String property, Object value) {

        List<T> entities = getEntitiesByProperty(entityType, property, value);
        return entities.isEmpty() ? null : entities.get(0);
    }

    public void addEntity(Object entity) {

        Session session = QuarterMAP.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(entity);
        SessionUtil.commitTransaction(session);
    }

    public void removeEntity(Object entity) {

        Session session = QuarterMAP.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(entity);
        SessionUtil.commitTransaction(session);
    }

    public void updateEntity(Object entity) {

        Session session = QuarterMAP.getSessionFactory().openSession();
        session.beginTransaction();
        session.update(entity);
        SessionUtil.commitTransaction(session);
    }

}
