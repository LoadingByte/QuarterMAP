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

package com.quartercode.quartermap.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.Validate;
import com.quartercode.quartermap.dto.artifactrepo.Artifact;
import com.quartercode.quartermap.dto.artifactrepo.ReleaseChannel;
import com.quartercode.quartermap.dto.artifactrepo.Version;

public class ArtifactUtil {

    public static Artifact[] getStableAndLatest(List<Artifact> artifacts) {

        Validate.isTrue(!artifacts.isEmpty(), "Artifact list cannot be empty");

        artifacts = new ArrayList<>(artifacts);
        Collections.sort(artifacts);
        Collections.reverse(artifacts);

        Artifact stable = null;
        Artifact latest = null;

        for (Artifact artifact : artifacts) {
            Version version = artifact.getVersion();

            if (stable == null && version.getChannel() == ReleaseChannel.RELEASE) {
                // Take latest release
                stable = artifact;
            }
            if (latest == null || version.getChannel().ordinal() > latest.getVersion().getChannel().ordinal()) {
                // Filter out releases & hotfix snapshots
                if (version.getChannel() != ReleaseChannel.RELEASE && version.getRevision() == 0) {
                    latest = artifact;
                }
            }

            if (version.getChannel() == ReleaseChannel.RELEASE && version.getRevision() == 0) {
                // Found release that is not a hotfix (start of current cycle releases) -> break
                break;
            }
        }

        if (latest == null && artifacts.get(0).getVersion().getChannel() != ReleaseChannel.RELEASE) {
            // Filter out hotfix snapshots
            if (artifacts.get(0).getVersion().getRevision() == 0) {
                latest = artifacts.get(0);
            }
        }

        return new Artifact[] { stable, latest };
    }

    private ArtifactUtil() {

    }

}
