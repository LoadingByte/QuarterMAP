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

import java.util.List;
import com.quartercode.quartermap.dto.config.Config;

public class ConfigService {

    private final EntityService entityService;

    public ConfigService() {

        entityService = new EntityService();
    }

    public Config getConfig() {

        List<Config> entities = entityService.getAllEntities(Config.class);
        if (entities.size() > 0) {
            return entities.get(0);
        } else {
            Config config = new Config();
            entityService.addEntity(config);
            return config;
        }
    }

    public void updateConfig(Config config) {

        entityService.updateEntity(config);
    }

}
