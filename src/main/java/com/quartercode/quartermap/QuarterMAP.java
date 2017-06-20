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

package com.quartercode.quartermap;

import java.util.Properties;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class QuarterMAP {

    private static Properties     dbConfig;
    private static SessionFactory sessionFactory;

    public static Properties getDbConfig() {

        return dbConfig;
    }

    public static SessionFactory getSessionFactory() {

        return sessionFactory;
    }

    @SuppressWarnings ("deprecation")
    public static void init(Properties dbConfig) {

        QuarterMAP.dbConfig = dbConfig;

        Configuration configuration = new Configuration().configure();
        configuration.setProperty("hibernate.connection.url", dbConfig.getProperty("url"));
        configuration.setProperty("hibernate.connection.username", dbConfig.getProperty("username"));
        configuration.setProperty("hibernate.connection.password", dbConfig.getProperty("password"));
        configuration.setProperty("hibernate.connection.driver_class", dbConfig.getProperty("driver"));
        configuration.setProperty("hibernate.dialect", dbConfig.getProperty("dialect"));

        sessionFactory = configuration.buildSessionFactory();

    }

    private QuarterMAP() {

    }

}
