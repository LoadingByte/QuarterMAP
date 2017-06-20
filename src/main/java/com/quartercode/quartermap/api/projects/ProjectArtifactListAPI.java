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

package com.quartercode.quartermap.api.projects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import com.opensymphony.xwork2.ActionSupport;
import com.quartercode.quartermap.dto.artifactrepo.Artifact;
import com.quartercode.quartermap.dto.project.ArtifactMapping;
import com.quartercode.quartermap.dto.project.Project;
import com.quartercode.quartermap.service.ArtifactRepositoryCacheService;
import com.quartercode.quartermap.service.ProjectService;
import com.quartercode.quartermap.util.ArtifactUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings ("serial")
@Getter
@Setter
public class ProjectArtifactListAPI extends ActionSupport {

    @Getter (AccessLevel.NONE)
    private final ProjectService                 projectService                 = new ProjectService();
    @Getter (AccessLevel.NONE)
    private final ArtifactRepositoryCacheService artifactRepositoryCacheService = new ArtifactRepositoryCacheService();

    private String                               error;

    private String                               projectId;

    private String                               artifactId;
    private Artifact                             stable;
    private Artifact                             latest;
    private List<Artifact>                       artifacts;

    @Override
    public String execute() throws Exception {

        Project project = projectService.getProjectByName(projectId);
        if (project == null) {
            // Invalid projectId
            error = "invalid-projectId";
            return ERROR;
        }

        ArtifactMapping artifactMapping = project.getConfiguration().getArtifact();
        artifactId = artifactMapping.toString();
        Set<Artifact> fetchedArtifacts = artifactRepositoryCacheService.getArtifacts(artifactMapping.toString());
        if (fetchedArtifacts == null) {
            // Currently fetching artifacts
            error = "fetching-repository-indices";
            return ERROR;
        }
        artifacts = new ArrayList<>(fetchedArtifacts);
        Collections.sort(artifacts);

        if (!artifacts.isEmpty()) {
            Artifact[] stableAndLatest = ArtifactUtil.getStableAndLatest(artifacts);
            stable = stableAndLatest[0];
            latest = stableAndLatest[1];
        }

        return SUCCESS;
    }

}
