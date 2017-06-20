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

package com.quartercode.quartermap.dto.artifactrepo;

import java.net.URL;
import org.apache.struts2.json.annotations.JSON;
import lombok.Data;
import lombok.Getter;

@Data
public class ArtifactResult implements Comparable<ArtifactResult> {

    private final String     name;
    @Getter (onMethod = @__ ({ @JSON (serialize = false) }))
    private final URL        location;
    @Getter (onMethod = @__ ({ @JSON (name = "location") }))
    private final String     locationString;
    private final Classifier classifier;
    private final FileType   fileType;

    public ArtifactResult(String name, URL location, Classifier classifier, FileType fileType) {

        this.name = name;
        this.location = location;
        locationString = location.toExternalForm();
        this.classifier = classifier;
        this.fileType = fileType;
    }

    @Override
    public int compareTo(ArtifactResult o) {

        if (classifier.compareTo(o.getClassifier()) == 0) {
            return fileType.compareTo(o.getFileType());
        } else {
            return classifier.compareTo(o.getClassifier());
        }
    }

}
