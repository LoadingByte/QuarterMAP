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

package com.quartercode.quartermap.dto.user;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    DEVELOPER ("Developer"), PROJECT_ADMIN ("Project Admin"), ADMIN ("Admin");

    static {

        // Fill authorizations map
        for (Role role : values()) {
            Map<Role, Boolean> authorizations = new HashMap<>();
            for (Role otherRole : values()) {
                authorizations.put(otherRole, role.ordinal() >= otherRole.ordinal());
            }
            role.authorizations = Collections.unmodifiableMap(authorizations);
        }

    }

    private final String       displayName;
    private Map<Role, Boolean> authorizations;

}
