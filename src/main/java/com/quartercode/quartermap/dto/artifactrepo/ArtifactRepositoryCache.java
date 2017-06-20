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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Data;

@Data
public class ArtifactRepositoryCache {

    private long                             generationTimestamp;
    private final Set<Artifact>              artifacts       = new HashSet<>();

    // Quick access cache
    private final Map<String, Set<Artifact>> idArtifactCache = new HashMap<>();

    public Set<Artifact> getArtifacts(String groupId, String artifactId) {

        return getArtifacts(groupId + ":" + artifactId);
    }

    public Set<Artifact> getArtifacts(String id) {

        if (idArtifactCache.containsKey(id)) {
            return idArtifactCache.get(id);
        } else {
            synchronized (this) {
                Set<Artifact> artifacts = new HashSet<>();
                for (Artifact artifact : this.artifacts) {
                    if (id.equals(artifact.getGroupId() + ":" + artifact.getArtifactId())) {
                        artifacts.add(artifact);
                    }
                }

                idArtifactCache.put(id, Collections.unmodifiableSet(artifacts));
                return artifacts;
            }
        }
    }

}
