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

package com.quartercode.quartermap.dto.project;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

@Data
@Entity
public class Project implements Comparable<Project> {

    @Id
    @GeneratedValue
    private int                  id;

    private String               name;
    @Getter (AccessLevel.NONE)
    @Embedded
    private ProjectConfiguration configuration;

    public ProjectConfiguration getConfiguration() {

        // Workaround for Hibernate in order to keep it from nulling this embedded object
        if (configuration == null) {
            configuration = new ProjectConfiguration();
        }
        return configuration;
    }

    @Override
    public int compareTo(Project o) {

        return name.compareTo(o.getName());
    }

}
