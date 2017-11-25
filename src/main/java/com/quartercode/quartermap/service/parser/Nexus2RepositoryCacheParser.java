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

package com.quartercode.quartermap.service.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.quartercode.quartermap.dto.artifactrepo.Artifact;
import com.quartercode.quartermap.dto.artifactrepo.ArtifactRepositoryCache;
import com.quartercode.quartermap.dto.artifactrepo.ArtifactResult;
import com.quartercode.quartermap.dto.artifactrepo.Classifier;
import com.quartercode.quartermap.dto.artifactrepo.ClassifierType;
import com.quartercode.quartermap.dto.artifactrepo.FileType;
import com.quartercode.quartermap.dto.artifactrepo.ReleaseChannel;
import com.quartercode.quartermap.dto.artifactrepo.Version;

public class Nexus2RepositoryCacheParser implements RepositoryCacheParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(Nexus2RepositoryCacheParser.class);

    public Nexus2RepositoryCacheParser() {

    }

    @Override
    public ArtifactRepositoryCache parse(URL location) throws RepositoryParseException {

        ArtifactRepositoryCache cache = new ArtifactRepositoryCache();

        Queue<String> urls = Collections.asLifoQueue(new LinkedList<String>());
        urls.add(location.toString());

        // Loop variables
        Map<String, Set<String>> linkCache = new HashMap<>();

        while (urls.size() > 0) {
            String currentUrl = urls.remove();
            Document document = null;
            try {
                document = getDocument(currentUrl);
            } catch (IOException e) {
                throw new RepositoryParseException("Can't fetch site '" + currentUrl + "'", e);
            }

            try {
                // Parse links
                if (currentUrl.endsWith("/")) {
                    linkCache.put(currentUrl, new HashSet<String>());
                    for (Element element : document.select("td > a")) {
                        String link = element.attr("abs:href");
                        linkCache.get(currentUrl).add(link);

                        // Scan pom in the next iteration
                        if (link.endsWith(".pom")) {
                            urls.add(link);
                        }
                        // Go deeper into the tree
                        else if (link.endsWith("/") && link.contains(currentUrl)) {
                            urls.add(link);
                        }
                    }
                }

                // Parse pom
                if (currentUrl.endsWith(".pom")) {
                    // Parse groupId and artifactId
                    String groupId = document.select("project > groupId").text();
                    if (groupId.isEmpty()) {
                        // Use parent groupId if no groupId is available
                        groupId = document.select("project > parent > groupId").text();
                    }
                    String artifactId = document.select("project > artifactId").text();

                    // Parse version
                    String versionString = document.select("project > version").text();
                    if (versionString.isEmpty()) {
                        // Use parent version if no version is available
                        versionString = document.select("project > parent > version").text();
                    }
                    Version version = parseVersion(versionString, currentUrl);

                    // If the version string is invalid, just continue with the next artifact
                    if (version == null) {
                        continue;
                    }

                    Artifact artifact = new Artifact(groupId, artifactId, version);

                    // Fetch results
                    String currentDir = currentUrl.substring(0, currentUrl.lastIndexOf("/") + 1);
                    String currentArtifactFilePrefix = currentUrl.substring(0, currentUrl.lastIndexOf(".pom"));
                    String currentArtifactFilenamePrefix = StringUtils.substringAfterLast(currentArtifactFilePrefix, "/");
                    for (String link : linkCache.get(currentDir)) {
                        if (!link.endsWith(".pom") && !link.endsWith(".asc") && !link.endsWith(".md5") && !link.endsWith(".sha1") && link.startsWith(currentArtifactFilePrefix)) {
                            try {
                                URL resultLocation = new URL(link);
                                String resultName = resultLocation.getPath().substring(resultLocation.getPath().lastIndexOf("/") + 1);

                                FileType resultFileType = null;
                                for (FileType fileType : FileType.values()) {
                                    if (resultName.toLowerCase().endsWith("." + fileType.getExtension())) {
                                        resultFileType = fileType;
                                        break;
                                    }
                                }

                                String classifierString = StringUtils.substringBetween(resultName, currentArtifactFilenamePrefix, "." + resultFileType.getExtension());
                                classifierString = StringUtils.isBlank(classifierString) ? "" : classifierString.substring(1);
                                Classifier classifier = parseClassifier(classifierString);

                                artifact.getResults().add(new ArtifactResult(resultName, resultLocation, classifier, resultFileType));
                            } catch (MalformedURLException e) {
                                throw new RepositoryParseException("Invalid artifact result url '" + link + "'", e);
                            }
                        }
                    }

                    cache.getArtifacts().add(artifact);
                }
            } catch (RuntimeException e) {
                throw new RepositoryParseException("Unexpected exception while parsing; Invalid repository?", e);
            }
        }

        return cache;
    }

    private Document getDocument(String url) throws IOException {

        int maxTries = 5;
        int tries = 0;

        while (true) {
            try {
                return Jsoup.connect(url).timeout(10 * 1000).get();
            } catch (IOException e) {
                LOGGER.error("Can't connect to '#0' (try #1, error '#2')", url, String.valueOf(tries), e.toString());
                if (tries < maxTries) {
                    continue;
                } else {
                    LOGGER.error("Retrying after 2 seconds of sleep");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e2) {
                        // Ignore
                    }
                    tries = 0;
                }
            }
        }
    }

    /*
     * Returns null if the supplied version string turns out to be invalid.
     */
    private Version parseVersion(String versionString, String url) {

        try {
            String[] versionParts1 = versionString.split("-");

            int major = 0;
            int minor = 0;
            int revision = 0;
            if (versionParts1.length >= 1) {
                String[] versionParts2 = versionParts1[0].split("\\.");
                if (versionParts2.length >= 1) {
                    major = Integer.parseInt(versionParts2[0]);
                }
                if (versionParts2.length >= 2) {
                    minor = Integer.parseInt(versionParts2[1]);
                }
                if (versionParts2.length >= 3) {
                    revision = Integer.parseInt(versionParts2[2]);
                }
            }

            ReleaseChannel channel = ReleaseChannel.RELEASE;
            if (versionParts1.length >= 2) {
                channel = ReleaseChannel.valueOf(versionParts1[1].toUpperCase());
            }

            int channelIteration = 1;
            if (channel == ReleaseChannel.RELEASE) {
                channelIteration = -1;
            } else if (channel == ReleaseChannel.SNAPSHOT) {
                channelIteration = Integer.parseInt(StringUtils.substringAfterLast(StringUtils.substringBeforeLast(url, "."), "-"));
            } else if (versionParts1.length >= 3) {
                channelIteration = Integer.parseInt(versionParts1[2]);
            }

            return new Version(major, minor, revision, channel, channelIteration);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private Classifier parseClassifier(String classifierString) {

        ClassifierType classifierType = ClassifierType.BINARY;
        if (classifierString.equals("sources")) {
            classifierType = ClassifierType.SOURCES;
        } else if (classifierString.equals("javadoc")) {
            classifierType = ClassifierType.JAVADOC;
        }

        return new Classifier(classifierString, classifierType);
    }

}
