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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.io.FileUtils;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class Initializer implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Initializer.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {

        // Database configuration
        try {
            Properties dbConfig = loadConfig("db.properties");
            QuarterMAP.init(dbConfig);
        } catch (IOException e) {
            LOGGER.fatal("Can't load a config file", e);
        }
    }

    private static Properties loadConfig(String name) throws IOException {

        File configFile = new File(System.getProperty("user.home"), "quartermap/config/" + name);

        // Copy config if it doesn't exist yet
        if (!configFile.exists()) {
            InputStream resource = QuarterMAP.class.getResourceAsStream("/config/" + name);
            FileUtils.copyInputStreamToFile(resource, configFile);
        }

        // Read config file
        Properties config = new Properties();
        try (InputStream configFileStream = new FileInputStream(configFile)) {
            config.load(configFileStream);
            return config;
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

        // Do nothing
    }

}
