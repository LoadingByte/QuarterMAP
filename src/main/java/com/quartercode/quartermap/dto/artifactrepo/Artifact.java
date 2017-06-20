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

import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.struts2.json.annotations.JSON;
import lombok.Data;
import lombok.Getter;

@Data
public class Artifact implements Comparable<Artifact> {

    private final String                    groupId;
    private final String                    artifactId;
    @Getter (onMethod = @__ ({ @JSON (serialize = false) }))
    private final Version                   version;
    @Getter (onMethod = @__ ({ @JSON (name = "version") }))
    private final String                    versionString;

    @Getter (onMethod = @__ ({ @JSON (name = "build") }))
    private final int                       buildNumber;
    private final String                    branch;

    private final SortedSet<ArtifactResult> results = new TreeSet<>();

    public Artifact(String groupId, String artifactId, Version version, int buildNumber, String branch) {

        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        versionString = version.toString();
        this.buildNumber = buildNumber;
        this.branch = branch;
    }

    @Override
    public int compareTo(Artifact o) {

        return Integer.valueOf(buildNumber).compareTo(o.getBuildNumber());
    }

}
