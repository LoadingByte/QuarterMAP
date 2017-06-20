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

import java.net.MalformedURLException;
import java.net.URL;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.quartercode.quartermap.dto.project.Project;
import com.quartercode.quartermap.exception.CancelWithErrorException;
import com.quartercode.quartermap.service.ProjectService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings ("serial")
@Getter
@Setter
public class ProjectEditAction extends ActionSupport implements Preparable {

    @Getter (AccessLevel.NONE)
    private final ProjectService projectService = new ProjectService();

    private String               projectId;
    private Project              project;
    private String               oldName;

    private String               newSourceRepository;
    private String               newJenkinsJob;
    private String               newSonarJob;

    @Override
    public void prepare() throws Exception {

        project = projectService.getProjectByName(projectId);

        if (project == null) {
            // Invalid projectId
            throw new CancelWithErrorException();
        }

        oldName = project.getName();
    }

    @Override
    public void validate() {

        // Workaround because struts 2 can't validate urls at the moment

        try {
            new URL(newSourceRepository);
        } catch (MalformedURLException e) {
            addFieldError("newSourceRepository", "Source Repository is invalid");
        }

        try {
            new URL(newJenkinsJob);
        } catch (MalformedURLException e) {
            addFieldError("newJenkins", "Jenkins URL is invalid");
        }

        try {
            new URL(newSonarJob);
        } catch (MalformedURLException e) {
            addFieldError("newSonar", "Sonar URL is invalid");
        }
    }

    @Override
    public String execute() throws Exception {

        if (!oldName.toLowerCase().endsWith(project.getName().toLowerCase()) && projectService.getProjectByName(project.getName()) != null) {
            addFieldError("project.name", "Project name already taken");
            return INPUT;
        }

        // Workaround because struts 2 cannot parse urls at the moment
        project.getConfiguration().setSourceRepository(new URL(newSourceRepository));
        project.getConfiguration().setJenkinsJob(new URL(newJenkinsJob));
        project.getConfiguration().setSonarJob(new URL(newSonarJob));

        projectService.updateProject(project);

        return SUCCESS;
    }
}
