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

package com.quartercode.quartermap.dto.artifactrepo;

import lombok.Data;

@Data
public class Classifier implements Comparable<Classifier> {

    private final String         name;
    private final ClassifierType type;

    public String getDisplayName() {

        return name.isEmpty() ? "main" : name;
    }

    @Override
    public int compareTo(Classifier o) {

        if (type.compareTo(o.getType()) == 0) {
            return name.compareTo(o.getName());
        } else {
            return type.compareTo(o.getType());
        }
    }

}
