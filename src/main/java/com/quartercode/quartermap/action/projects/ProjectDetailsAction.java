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

package com.quartercode.quartermap.action.projects;

import java.util.ArrayList;
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
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings ("serial")
@Getter
@Setter
public class ProjectDetailsAction extends ActionSupport {

    @Getter (AccessLevel.NONE)
    private final ProjectService                 projectService                 = new ProjectService();
    @Getter (AccessLevel.NONE)
    private final ArtifactRepositoryCacheService artifactRepositoryCacheService = new ArtifactRepositoryCacheService();

    private String                               projectId;
    private Project                              project;

    private ProjectStats                         stats;
    private Artifact                             stable;
    private Artifact                             latest;

    @Override
    public String execute() throws Exception {

        project = projectService.getProjectByName(projectId);

        if (project == null) {
            // Invalid projectId
            return ERROR;
        }

        ArtifactMapping artifactMapping = project.getConfiguration().getArtifact();
        Set<Artifact> fetchedArtifacts = artifactRepositoryCacheService.getArtifacts(artifactMapping.getGroupId(), artifactMapping.getArtifactId());
        if (fetchedArtifacts == null) {
            // Currently fetching artifacts
            return "fetch";
        }

        List<Artifact> artifacts = new ArrayList<>(fetchedArtifacts);

        if (!artifacts.isEmpty()) {
            Artifact[] stableAndLatest = ArtifactUtil.getStableAndLatest(artifacts);
            stable = stableAndLatest[0];
            latest = stableAndLatest[1];
        }

        stats = new ProjectStats();
        stats.setArtifactCount(artifacts.size());

        return SUCCESS;
    }

    @Data
    private static class ProjectStats {

        private int artifactCount;

    }

}
