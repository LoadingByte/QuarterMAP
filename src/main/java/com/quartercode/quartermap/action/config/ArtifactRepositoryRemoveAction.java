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

package com.quartercode.quartermap.action.config;

import com.opensymphony.xwork2.ActionSupport;
import com.quartercode.quartermap.dto.artifactrepo.ArtifactRepository;
import com.quartercode.quartermap.service.ArtifactRepositoryService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings ("serial")
@Getter
@Setter
public class ArtifactRepositoryRemoveAction extends ActionSupport {

    @Getter (AccessLevel.NONE)
    private final ArtifactRepositoryService artifactRepositoryService = new ArtifactRepositoryService();

    private String                          repositoryId;

    @Override
    public String execute() throws Exception {

        ArtifactRepository repository = artifactRepositoryService.getRepositoryByName(repositoryId);
        if (repository == null) {
            return ERROR;
        }

        artifactRepositoryService.removeRepository(repository);

        return SUCCESS;
    }

}