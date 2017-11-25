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

package com.quartercode.quartermap.action.root;

import java.net.URL;
import org.apache.commons.codec.digest.DigestUtils;
import com.quartercode.quartermap.action.DynamicRedirectAction;
import com.quartercode.quartermap.dto.artifactrepo.ArtifactRepository;
import com.quartercode.quartermap.dto.config.Config;
import com.quartercode.quartermap.dto.project.Project;
import com.quartercode.quartermap.dto.user.Role;
import com.quartercode.quartermap.dto.user.User;
import com.quartercode.quartermap.service.ArtifactRepositoryService;
import com.quartercode.quartermap.service.ConfigService;
import com.quartercode.quartermap.service.ProjectService;
import com.quartercode.quartermap.service.UserService;
import com.quartercode.quartermap.service.parser.NexusRepositoryCacheParser;
import com.quartercode.quartermap.util.HashUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings ("serial")
@Getter
@Setter
public class SetupAction extends DynamicRedirectAction {

    @Getter (AccessLevel.NONE)
    private final UserService               userService               = new UserService();
    @Getter (AccessLevel.NONE)
    private final ConfigService             configService             = new ConfigService();
    @Getter (AccessLevel.NONE)
    private final ProjectService            projectService            = new ProjectService();
    @Getter (AccessLevel.NONE)
    private final ArtifactRepositoryService artifactRepositoryService = new ArtifactRepositoryService();

    @Override
    public String execute() throws Exception {

        if (userService.getUsers().size() == 0) {
            // Generate default user
            User defaultUser = new User();
            defaultUser.setName("admin");
            defaultUser.setSalt(HashUtil.generateSalt());
            defaultUser.setPassword(DigestUtils.sha256Hex("admin" + defaultUser.getSalt()));
            defaultUser.setRole(Role.ADMIN);

            userService.addUser(defaultUser);

            // --------------------------------------------------------------------

            Config config = configService.getConfig();
            // TODO: Fill this with values
            configService.updateConfig(config);

            // --------------------------------------------------------------------

            Project project = new Project();

            project.setName("QuarterBukkit");
            project.getConfiguration().getArtifact().setGroupId("com.quartercode");
            project.getConfiguration().getArtifact().setArtifactId("quarterbukkit-package");
            project.getConfiguration().setSourceRepository(new URL("https://github.com/QuarterCode/QuarterBukkit"));
            project.getConfiguration().setContinuousIntegrationJob(new URL("http://ci.quartercode.com/job/QuarterBukkit"));

            projectService.addProject(project);

            // --------------------------------------------------------------------

            ArtifactRepository repository = new ArtifactRepository();

            repository.setName("QuarterCode Public");
            repository.getConfiguration().setLocation(new URL("http://repo.quartercode.com/content/groups/public/"));
            repository.getConfiguration().setCacheParser(NexusRepositoryCacheParser.class);

            artifactRepositoryService.addRepository(repository);
        }

        return SUCCESS;
    }

}
