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

import java.util.Set;
import com.opensymphony.xwork2.ActionSupport;
import com.quartercode.quartermap.dto.artifactrepo.Artifact;
import com.quartercode.quartermap.dto.project.Project;
import com.quartercode.quartermap.service.ArtifactRepositoryCacheService;
import com.quartercode.quartermap.service.ProjectService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings ("serial")
@Getter
@Setter
public class ProjectArtifactAPI extends ActionSupport {

    @Getter (AccessLevel.NONE)
    private final ProjectService                 projectService                 = new ProjectService();
    @Getter (AccessLevel.NONE)
    private final ArtifactRepositoryCacheService artifactRepositoryCacheService = new ArtifactRepositoryCacheService();

    private String                               error;

    private String                               projectId;
    private String                               uversion;

    private Artifact                             artifact;

    @Override
    public String execute() throws Exception {

        Project project = projectService.getProjectByName(projectId);
        if (project == null) {
            // Invalid projectId
            error = "invalid-projectId";
            return ERROR;
        }

        Set<Artifact> fetchedArtifacts = artifactRepositoryCacheService.getArtifacts(project.getConfiguration().getArtifact().toString());
        if (fetchedArtifacts == null) {
            // Currently fetching artifacts
            error = "fetching-repository-indices";
            return ERROR;
        }

        for (Artifact fetchedArtifact : fetchedArtifacts) {
            if (fetchedArtifact.getUniqueVersionString().equals(uversion)) {
                artifact = fetchedArtifact;
                break;
            }
        }

        if (artifact == null) {
            // Invalid uversion
            error = "invalid-uversion";
            return ERROR;
        }

        return SUCCESS;
    }

}
