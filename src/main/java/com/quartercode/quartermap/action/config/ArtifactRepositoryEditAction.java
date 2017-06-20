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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.quartercode.quartermap.dto.artifactrepo.ArtifactRepository;
import com.quartercode.quartermap.exception.CancelWithErrorException;
import com.quartercode.quartermap.service.ArtifactRepositoryService;
import com.quartercode.quartermap.service.parser.NexusRepositoryCacheParser;
import com.quartercode.quartermap.service.parser.RepositoryCacheParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings ("serial")
@Getter
@Setter
public class ArtifactRepositoryEditAction extends ActionSupport implements Preparable {

    @Getter (AccessLevel.NONE)
    private final ArtifactRepositoryService artifactRepositoryService = new ArtifactRepositoryService();

    private String                          repositoryId;
    private ArtifactRepository              repository;
    private String                          oldName;

    private Map<String, String>             availableCacheParsers;

    private String                          newLocation;
    private String                          cacheParser;

    @Override
    public void prepare() throws Exception {

        availableCacheParsers = new LinkedHashMap<>();
        // TODO: Make this dynamic
        availableCacheParsers.put(NexusRepositoryCacheParser.class.getName(), NexusRepositoryCacheParser.class.getSimpleName());

        repository = artifactRepositoryService.getRepositoryByName(repositoryId);

        if (repository == null) {
            // Invalid repositoryId
            throw new CancelWithErrorException();
        }

        oldName = repository.getName();
        cacheParser = repository.getConfiguration().getCacheParser().getName();
    }

    @Override
    public void validate() {

        // Workaround because struts 2 can't validate urls at the moment
        try {
            new URL(newLocation);
        } catch (MalformedURLException e) {
            addFieldError("newLocation", "Repository location is invalid");
        }

        // Workaround because struts 2 can't validate classes
        try {
            Class.forName(cacheParser);
        } catch (Exception e) {
            addFieldError("cacheParser", "Cache parser is invalid");
        }
    }

    // We need to cast the given cache parser class
    @SuppressWarnings ("unchecked")
    @Override
    public String execute() throws Exception {

        if (!oldName.toLowerCase().endsWith(repository.getName().toLowerCase()) && artifactRepositoryService.getRepositoryByName(repository.getName()) != null) {
            addFieldError("repository.name", "Repository name already taken");
            return INPUT;
        }

        // Workaround because struts 2 cannot parse urls at the moment
        repository.getConfiguration().setLocation(new URL(newLocation));
        // Workaround because struts 2 cannot parse classes
        repository.getConfiguration().setCacheParser((Class<? extends RepositoryCacheParser>) Class.forName(cacheParser));

        artifactRepositoryService.updateRepository(repository);

        return SUCCESS;
    }

}
