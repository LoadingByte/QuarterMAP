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

import java.net.URL;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import com.opensymphony.xwork2.ActionSupport;
import com.quartercode.quartermap.dto.artifactrepo.Artifact;
import com.quartercode.quartermap.dto.artifactrepo.ArtifactResult;
import com.quartercode.quartermap.dto.artifactrepo.ClassifierType;
import com.quartercode.quartermap.dto.project.Project;
import com.quartercode.quartermap.service.ArtifactRepositoryCacheService;
import com.quartercode.quartermap.service.ProjectService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings ("serial")
@Getter
@Setter
public class ProjectArtifactAction extends ActionSupport {

    @Getter (AccessLevel.NONE)
    private final ProjectService                 projectService                 = new ProjectService();
    @Getter (AccessLevel.NONE)
    private final ArtifactRepositoryCacheService artifactRepositoryCacheService = new ArtifactRepositoryCacheService();

    private String                               projectId;
    private int                                  build;

    private Artifact                             artifact;
    private SortedSet<ArtifactResult>            binaries;
    private URL                                  jenkinsBuildLocation;

    @Override
    public String execute() throws Exception {

        Project project = projectService.getProjectByName(projectId);
        if (project == null) {
            // Invalid projectId
            return ERROR;
        }

        Set<Artifact> fetchedArtifacts = artifactRepositoryCacheService.getArtifacts(project.getConfiguration().getArtifact().toString());
        if (fetchedArtifacts == null) {
            // Currently fetching artifacts
            return "fetch";
        }

        for (Artifact fetchedArtifact : fetchedArtifacts) {
            if (fetchedArtifact.getBuildNumber() == build) {
                artifact = fetchedArtifact;
                break;
            }
        }

        if (artifact == null) {
            // Invalid build
            return ERROR;
        }

        binaries = new TreeSet<>();
        for (ArtifactResult result : artifact.getResults()) {
            if (result.getClassifier().getType() == ClassifierType.BINARY) {
                binaries.add(result);
            }
        }

        jenkinsBuildLocation = new URL(project.getConfiguration().getJenkinsJob() + "/" + artifact.getBuildNumber());

        return SUCCESS;
    }

}
