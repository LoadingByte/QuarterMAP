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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.servlet.jsp.PageContext;
import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.decorator.TableDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.quartercode.quartermap.dto.artifactrepo.Artifact;
import com.quartercode.quartermap.dto.artifactrepo.ReleaseChannel;
import com.quartercode.quartermap.dto.artifactrepo.Version;
import com.quartercode.quartermap.dto.project.ArtifactMapping;
import com.quartercode.quartermap.dto.project.Project;
import com.quartercode.quartermap.exception.CancelWithErrorException;
import com.quartercode.quartermap.exception.CancelWithFetchException;
import com.quartercode.quartermap.service.ArtifactRepositoryCacheService;
import com.quartercode.quartermap.service.ProjectService;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings ("serial")
@Getter
@Setter
public class ProjectArtifactListAction extends ActionSupport implements Preparable {

    @Getter (AccessLevel.NONE)
    private final ProjectService                 projectService                 = new ProjectService();
    @Getter (AccessLevel.NONE)
    private final ArtifactRepositoryCacheService artifactRepositoryCacheService = new ArtifactRepositoryCacheService();

    private String                               projectId;
    private String                               cycle;
    private ReleaseChannel                       channel;

    private Map<String, String>                  availableCycles;
    private Map<ReleaseChannel, String>          availableChannels;

    private String                               artifactId;
    private List<Artifact>                       artifacts;

    @Override
    public void prepare() throws Exception {

        availableChannels = new LinkedHashMap<>();
        availableChannels.put(null, "All");
        for (ReleaseChannel channel : ReleaseChannel.values()) {
            availableChannels.put(channel, channel.getDisplayName());
        }

        Project project = projectService.getProjectByName(projectId);
        if (project == null) {
            // Invalid projectId
            throw new CancelWithErrorException();
        }

        ArtifactMapping artifactMapping = project.getConfiguration().getArtifact();
        artifactId = artifactMapping.toString();
        Set<Artifact> fetchedArtifacts = artifactRepositoryCacheService.getArtifacts(artifactMapping.toString());
        if (fetchedArtifacts == null) {
            // Currently fetching artifacts
            throw new CancelWithFetchException();
        }
        artifacts = new ArrayList<>(fetchedArtifacts);

        SortedSet<Cycle> cycles = new TreeSet<>(new Comparator<Cycle>() {

            @Override
            public int compare(Cycle o1, Cycle o2) {

                return -o1.compareTo(o2);
            }

        });
        for (Artifact artifact : artifacts) {
            Cycle cycle = new Cycle(artifact.getVersion().getMajor(), artifact.getVersion().getMinor());
            cycles.add(cycle);
        }
        availableCycles = new LinkedHashMap<>();
        availableCycles.put(null, "All");
        for (Cycle cycle : cycles) {
            availableCycles.put(cycle.toString(), cycle.toString());
        }
    }

    @Override
    public String execute() throws Exception {

        Cycle cycle = null;
        if (this.cycle != null) {
            cycle = Cycle.valueOf(this.cycle);
        }

        if (artifacts.size() > 0) {
            if (cycle != null || channel != null) {
                for (Artifact artifact : new ArrayList<>(artifacts)) {
                    if (cycle != null && !cycle.matches(artifact.getVersion())) {
                        artifacts.remove(artifact);
                    } else if (channel != null && artifact.getVersion().getChannel() != channel) {
                        artifacts.remove(artifact);
                    }
                }
            }

            Collections.sort(artifacts);
            Collections.reverse(artifacts);
        }

        // Workaround: Add session attribute "projectId" for accessing the id inside a DisplaytagColumnDecorator
        Map<String, Object> session = ActionContext.getContext().getSession();
        session.put("workaround-projectId", projectId);

        return SUCCESS;
    }

    @Data
    private static class Cycle implements Comparable<Cycle> {

        public static Cycle valueOf(String value) {

            String[] parts = value.split("\\.");
            if (parts.length == 3) {
                try {
                    return new Cycle(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                } catch (NumberFormatException e) {
                    return null;
                }
            } else {
                return null;
            }
        }

        private final int major;
        private final int minor;

        public boolean matches(Version version) {

            return version.getMajor() == major && version.getMinor() == minor;
        }

        @Override
        public int compareTo(Cycle o) {

            if (major == o.getMajor()) {
                return Integer.valueOf(minor).compareTo(o.getMinor());
            } else {
                return Integer.valueOf(major).compareTo(o.getMinor());
            }
        }

        @Override
        public String toString() {

            return major + "." + minor + ".x";
        }

    }

    public static class ArtifactListTableDecorator extends TableDecorator {

        @Override
        public String addRowClass() {

            return "artifactRow " + ((Version) evaluate("version")).getChannel().name().toLowerCase();
        }

    }

    public static class TypeColumnDecorator implements DisplaytagColumnDecorator {

        @Override
        public Object decorate(Object columnValue, PageContext pageContext, MediaTypeEnum media) throws DecoratorException {

            Version version = (Version) columnValue;
            String column = "";

            if (version.getRevision() > 0) {
                column += "Hotfix ";
            }
            column += version.getChannel().getDisplayName();
            if (version.getChannelIteration() > 0) {
                column += " " + version.getChannelIteration();
            }

            return column;
        }

    }

    public static class DetailsColumnDecorator implements DisplaytagColumnDecorator {

        @Override
        public Object decorate(Object columnValue, PageContext pageContext, MediaTypeEnum media) throws DecoratorException {

            Version version = (Version) columnValue;
            return "<a href=\"artifact?projectId=" + pageContext.getSession().getAttribute("workaround-projectId") + "&uversion=" + version.getUniqueString() + "\">Details</a>";
        }

    }

}
