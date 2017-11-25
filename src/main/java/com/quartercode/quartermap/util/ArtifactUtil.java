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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import com.quartercode.quartermap.dto.artifactrepo.Artifact;
import com.quartercode.quartermap.dto.artifactrepo.ArtifactResult;
import com.quartercode.quartermap.dto.artifactrepo.Classifier;
import com.quartercode.quartermap.dto.artifactrepo.ClassifierType;
import com.quartercode.quartermap.dto.artifactrepo.FileType;
import com.quartercode.quartermap.dto.artifactrepo.ReleaseChannel;
import com.quartercode.quartermap.dto.artifactrepo.Version;

public class ArtifactUtil {

    private static final Pattern SNAPSHOT_VERSION_PATTERN = Pattern.compile("^(.+)-[0-9]{8}\\.[0-9]{6}-([0-9]+)$");

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

    /*
     * Returns null if the supplied version string turns out to be invalid.
     */
    public static Version parseVersion(String versionString) {

        try {
            // Default version attributes
            String majorMinorRevision;
            ReleaseChannel channel;
            int channelIteration;

            // If the version string matches Maven's snapshot version pattern
            Matcher snapshotMatcher = SNAPSHOT_VERSION_PATTERN.matcher(versionString);
            if (snapshotMatcher.matches()) {
                majorMinorRevision = snapshotMatcher.group(1);
                channel = ReleaseChannel.SNAPSHOT;
                channelIteration = Integer.parseInt(snapshotMatcher.group(2));
            } else {
                String[] versionParts = versionString.split("-");

                // Part 1: major.minor.revision
                majorMinorRevision = versionParts[0];

                // Part 2: Optional release channel
                if (versionParts.length >= 2) {
                    channel = ReleaseChannel.valueOf(versionParts[1].toUpperCase());
                } else {
                    channel = ReleaseChannel.RELEASE;
                }

                // Part 3: Optional release channel iteration
                if (versionParts.length >= 3) {
                    channelIteration = Integer.parseInt(versionParts[2]);
                } else {
                    channelIteration = -1;
                }
            }

            // Finally, parse the main major.minor.revision part
            int major = 0, minor = 0, revision = 0;
            String[] mmrParts = majorMinorRevision.split("\\.");
            major = Integer.parseInt(mmrParts[0]);
            if (mmrParts.length >= 2) {
                minor = Integer.parseInt(mmrParts[1]);
            }
            if (mmrParts.length >= 3) {
                revision = Integer.parseInt(mmrParts[2]);
            }

            // Build the version object from the gathered data and return it
            return new Version(major, minor, revision, channel, channelIteration);
        } catch (RuntimeException e) {
            // In case of non-semver version formatting which even this method can't decipher, abort and return null
            return null;
        }
    }

    public static ArtifactResult parseArtifactResult(URL resultLocation, String versionString) {

        String resultName = FilenameUtils.getName(resultLocation.getPath());

        FileType resultFileType = FileType.OTHER;
        for (FileType fileType : FileType.values()) {
            if (resultName.toLowerCase().endsWith("." + fileType.getExtension())) {
                resultFileType = fileType;
                break;
            }
        }

        String classifierString = StringUtils.substringBetween(resultName, versionString, "." + resultFileType.getExtension());
        classifierString = StringUtils.isBlank(classifierString) ? "" : classifierString.substring(1); // remove leading hyphen
        Classifier resultClassifier = parseClassifier(classifierString);

        return new ArtifactResult(resultName, resultLocation, resultClassifier, resultFileType);
    }

    private static Classifier parseClassifier(String classifierString) {

        ClassifierType classifierType = ClassifierType.BINARY;
        if (classifierString.equalsIgnoreCase("sources")) {
            classifierType = ClassifierType.SOURCES;
        } else if (classifierString.equalsIgnoreCase("javadoc")) {
            classifierType = ClassifierType.JAVADOC;
        }

        return new Classifier(classifierString, classifierType);
    }

    private ArtifactUtil() {

    }

}
