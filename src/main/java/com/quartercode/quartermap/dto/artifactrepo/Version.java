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

import lombok.Data;

@Data
public class Version implements Comparable<Version> {

    private final int            major;
    private final int            minor;
    private final int            revision;
    private final ReleaseChannel channel;
    private final int            channelIteration;

    @Override
    public int compareTo(Version o) {

        if (major == o.getMajor()) {
            if (minor == o.getMinor()) {
                if (revision == o.getRevision()) {
                    if (channel == o.getChannel()) {
                        return Integer.valueOf(channelIteration).compareTo(o.getChannelIteration());
                    } else {
                        return Integer.valueOf(channel.ordinal()).compareTo(o.getChannel().ordinal());
                    }
                } else {
                    return Integer.valueOf(revision).compareTo(o.getRevision());
                }
            } else {
                return Integer.valueOf(minor).compareTo(o.getMinor());
            }
        } else {
            return Integer.valueOf(major).compareTo(o.getMinor());
        }
    }

    @Override
    public String toString() {

        String versionString = toStringWithoutChannel();

        if (channel != ReleaseChannel.RELEASE) {
            versionString += "-" + channel;
            if (channel != ReleaseChannel.SNAPSHOT) {
                versionString += "-" + channelIteration;
            }
        }

        return versionString.toString();
    }

    public String toStringWithoutChannel() {

        return major + "." + minor + "." + revision;
    }

}
