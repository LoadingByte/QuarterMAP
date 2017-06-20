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

package com.quartercode.quartermap.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.quartercode.quartermap.dto.artifactrepo.Artifact;
import com.quartercode.quartermap.dto.artifactrepo.ArtifactRepository;
import com.quartercode.quartermap.dto.artifactrepo.ArtifactRepositoryCache;
import com.quartercode.quartermap.dto.artifactrepo.ArtifactRepositoryConfiguration;
import com.quartercode.quartermap.service.parser.RepositoryParseException;

public class ArtifactRepositoryCacheService {

    public static final long                                   VALID_CACHE_TIME       = 60 * 60 * 1000;

    private static final Logger                                LOGGER                 = LoggerFactory.getLogger(ArtifactRepositoryCacheService.class);

    private static final Map<Integer, ArtifactRepositoryCache> repositoryCaches       = new HashMap<>();
    private static final Set<Integer>                          generatingRepositories = new HashSet<>();

    // Synchronized: Make sure that only one thread calls the method at the same time
    private static synchronized void generateCache(final ArtifactRepository repository) {

        if (!generatingRepositories.contains(repository.getId())) {
            LOGGER.info("(Re)generating repository cache for '#0' ...", repository.getName());
            final long startTimestamp = System.currentTimeMillis();

            generatingRepositories.add(repository.getId());

            if (repositoryCaches.containsKey(repository.getId())) {
                // Make the deprecated cache usable again for the duration of generation of the new cache
                repositoryCaches.get(repository.getId()).setGenerationTimestamp(Long.MAX_VALUE);
            }

            new Thread() {

                @Override
                public void run() {

                    try {
                        ArtifactRepositoryConfiguration configuration = repository.getConfiguration();
                        ArtifactRepositoryCache generatedCache = configuration.getCacheParser().newInstance().parse(configuration.getLocation());
                        generatedCache.setGenerationTimestamp(System.currentTimeMillis());
                        repositoryCaches.put(repository.getId(), generatedCache);
                        generatingRepositories.remove(repository.getId());

                        LOGGER.info("Successfully generated repository cache for '#0' (#1ms)", repository.getName(), String.valueOf(System.currentTimeMillis() - startTimestamp));
                        return;
                    } catch (RepositoryParseException e) {
                        LOGGER.error("Error while generating repository cache for '#0'", e, repository.getName());
                    } catch (Exception e) {
                        LOGGER.error("Unexpected error while generating repository cache for '#0'", e, repository.getName());
                    }

                    LOGGER.error("Can't generate repository cache for '#0'", repository.getName());
                    if (repositoryCaches.get(repository.getId()) != null) {
                        // Error -> Make the cache valid again for another interval and then try again
                        LOGGER.warn("Making the cache valid for another #0min", VALID_CACHE_TIME / (1000 * 60));
                        repositoryCaches.get(repository.getId()).setGenerationTimestamp(System.currentTimeMillis());
                    }
                }
            }.start();
        }
    }

    public ArtifactRepositoryCacheService() {

    }

    public ArtifactRepositoryCache getCache(ArtifactRepository repository) {

        ArtifactRepositoryCache cache = repositoryCaches.get(repository.getId());
        if (cache == null || System.currentTimeMillis() - cache.getGenerationTimestamp() > VALID_CACHE_TIME) {
            generateCache(repository);
        }

        return cache;
    }

    public Set<Artifact> getArtifacts(String groupId, String artifactId) {

        return getArtifacts(groupId + ":" + artifactId);
    }

    public Set<Artifact> getArtifacts(String id) {

        return getArtifacts(new ArtifactRepositoryService().getRepositories(), id);
    }

    public Set<Artifact> getArtifacts(Collection<ArtifactRepository> repositories, String groupId, String artifactId) {

        return getArtifacts(repositories, groupId + ":" + artifactId);
    }

    public Set<Artifact> getArtifacts(Collection<ArtifactRepository> repositories, String id) {

        Set<Artifact> artifacts = new HashSet<>();

        for (ArtifactRepository repository : repositories) {
            ArtifactRepositoryCache cache = getCache(repository);
            if (cache == null) {
                // Abort if a cache is generating
                return null;
            } else {
                artifacts.addAll(cache.getArtifacts(id));
            }
        }

        return artifacts;
    }

}
