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
import com.quartercode.quartermap.dto.user.User;

public class UserService {

    private final EntityService entityService;

    public UserService() {

        entityService = new EntityService();
    }

    public List<User> getUsers() {

        return entityService.getAllEntities(User.class);
    }

    public User getUserById(int id) {

        return entityService.getFirstEntityByProperty(User.class, "id", id);
    }

    public User getUserByName(String name) {

        return entityService.getFirstEntityByProperty(User.class, "name", name);
    }

    public void addUser(User user) {

        entityService.addEntity(user);
    }

    public void removeUser(User user) {

        entityService.removeEntity(user);
    }

    public void updateUser(User user) {

        entityService.updateEntity(user);
    }

}
