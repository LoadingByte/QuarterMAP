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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.quartercode.quartermap.dto.artifactrepo.Artifact;
import com.quartercode.quartermap.dto.artifactrepo.ArtifactRepositoryCache;
import com.quartercode.quartermap.dto.artifactrepo.Version;
import com.quartercode.quartermap.util.ArtifactUtil;
import com.quartercode.quartermap.util.HttpUtil;

public class Nexus3RepositoryCacheParser implements RepositoryCacheParser {

    private static final Logger  LOGGER             = LoggerFactory.getLogger(Nexus3RepositoryCacheParser.class);

    private static final Pattern URL_PATTERN        = Pattern.compile("^(.*)\\/repository\\/(.+)$");
    private static final Pattern LEGACY_URL_PATTERN = Pattern.compile("^(.*)\\/content\\/(?:repositories|groups)\\/(.+)$");

    public Nexus3RepositoryCacheParser() {

    }

    @Override
    public ArtifactRepositoryCache parse(URL location) throws RepositoryParseException {

        ArtifactRepositoryCache cache = new ArtifactRepositoryCache();

        // Parse URL for application context path and repository name
        String contextPath, repositoryId;
        String path = location.getPath();
        Matcher pathMatcher;
        if ( (pathMatcher = LEGACY_URL_PATTERN.matcher(path)).matches()) {
            contextPath = pathMatcher.group(1);
            repositoryId = StringUtils.stripEnd(pathMatcher.group(2), "/");
        } else if ( (pathMatcher = URL_PATTERN.matcher(path)).matches()) {
            contextPath = pathMatcher.group(1);
            repositoryId = StringUtils.stripEnd(pathMatcher.group(2), "/");
        } else {
            throw new RepositoryParseException("Configured URL '" + location + "' is not a valid Nexus 3 repository URL");
        }

        // Fetch list of all components using the REST API
        String continuationToken = null;
        do {
            // Download the current page
            JSONObject page = fetchComponentListPage(location.getProtocol(), location.getHost(), contextPath, repositoryId, continuationToken);

            // Get the continuation token for the next page, or null if there is no next page
            continuationToken = page.get("continuationToken") != null ? page.get("continuationToken").toString() : null;

            for (Object componentRaw : (JSONArray) page.get("items")) {
                JSONObject component = (JSONObject) componentRaw;

                // Parse the artifact and add it to the cache
                Artifact artifact = parseArtifact(component);
                if (artifact != null) {
                    cache.getArtifacts().add(artifact);
                }
            }
        } while (continuationToken != null);

        return cache;
    }

    private JSONObject fetchComponentListPage(String protocol, String host, String contextPath, String repositoryId, String continuationToken) throws RepositoryParseException {

        // Build the API endpoint URL to fetch the JSON from
        URL apiEndpoint;
        try {
            String path = contextPath + "/service/siesta/rest/beta/components?repositoryId=" + repositoryId;
            if (continuationToken != null) {
                path += "&continuationToken=" + continuationToken;
            }
            apiEndpoint = new URL(protocol, host, path);
        } catch (MalformedURLException e) {
            throw new RepositoryParseException("Can't build URL for API endpoint with context path '" + contextPath + "' and repository ID '" + repositoryId + "'", e);
        }

        // Open a connection to the API endpoint URL
        HttpURLConnection conn;
        try {
            conn = HttpUtil.resolveRedirects((HttpURLConnection) apiEndpoint.openConnection());

            // Make sure the response code is 200 OK
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new RepositoryParseException("Server responded with HTTP '" + responseCode + "' at API endpoint '" + apiEndpoint + "'");
            }
        } catch (IOException e) {
            throw new RepositoryParseException("Can't open connection to API endpoint '" + apiEndpoint + "'", e);
        }

        // Fetch the entire response text
        String responseText;
        try (Scanner responseScanner = new Scanner(conn.getInputStream(), "UTF-8")) {
            responseText = responseScanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RepositoryParseException("Can't fetch text from API endpoint '" + apiEndpoint + "'", e);
        }

        // Parse the response
        Object responseJson = JSONValue.parse(responseText);
        if (responseJson instanceof JSONObject) {
            return (JSONObject) responseJson;
        } else {
            throw new RepositoryParseException("Server returned an unknown JSON value at API endpoint '" + apiEndpoint + "'");
        }
    }

    private Artifact parseArtifact(JSONObject component) throws RepositoryParseException {

        // Gather group ID and artifact ID
        String groupId = component.get("group").toString();
        String artifactId = component.get("name").toString();

        // Parse version
        String versionString = component.get("version").toString();
        Version version = ArtifactUtil.parseVersion(versionString);
        // If the version string is invalid, just skip the artifact
        if (version == null) {
            return null;
        }

        Artifact artifact = new Artifact(groupId, artifactId, version);

        // Now, find all artifact results (assets) which are provided by the artifact
        for (Object assetRaw : (JSONArray) component.get("assets")) {
            JSONObject asset = (JSONObject) assetRaw;

            // Skip the asset if it's just a POM, signature, or checksum
            String assetUrl = asset.get("downloadUrl").toString();
            if (assetUrl.endsWith(".pom") || assetUrl.endsWith(".asc") || assetUrl.endsWith(".md5") || assetUrl.endsWith(".sha1")) {
                continue;
            }

            // Parse the artifact result
            URL resultLocation;
            try {
                resultLocation = new URL(assetUrl);
            } catch (MalformedURLException e) {
                throw new RepositoryParseException("Invalid artifact result url '" + assetUrl + "'", e);
            }
            artifact.getResults().add(ArtifactUtil.parseArtifactResult(resultLocation, versionString));
        }

        return artifact;
    }

}
