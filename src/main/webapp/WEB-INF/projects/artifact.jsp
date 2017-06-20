<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Artifact '<s:property value="artifact.artifactId" /> <s:property value="artifact.version" />'
</title>
<style type="text/css">
#resultList {
    margin: 0;
    padding: 0;
    list-style-type: none;
}

#resultList li {
    padding-top: 0.1em;
    padding-bottom: 0.1em;
}
</style>
</head>
<body>
    <!-- Main content -->
    <div class="center">
        <table>
            <tr>
                <th>Artifact</th>
                <td><s:property value="artifact.groupId" />:<s:property value="artifact.artifactId" /></td>
            </tr>
            <tr>
                <th>Version</th>
                <td><s:property value="artifact.version" /></td>
            </tr>
            <tr>
                <th>Build</th>
                <td><a href="<s:property value="jenkinsBuildLocation" />" target="_blank">#<s:property value="artifact.buildNumber" /></a></td>
            </tr>
            <tr>
                <th>Type</th>
                <td><s:if test="%{artifact.version.revision > 0}">
                        <span>Hotfix</span>
                    </s:if> <s:property value="artifact.version.channel.displayName" /> <s:if test="%{artifact.version.channelIteration > 0}">
                        <s:property value="artifact.version.channelIteration" />
                    </s:if></td>
            </tr>
            <tr>
                <th>Results</th>
                <td><s:if test="%{artifact.results.size != 0}">
                        <ul id="resultList">
                            <s:iterator value="artifact.results">
                                <li><s:property value="classifier.type.displayName" /> (<s:property value="classifier.displayName" />): <s:a href="%{location}">
                                        <s:property value="name" />
                                    </s:a></li>
                            </s:iterator>
                        </ul>
                    </s:if> <s:else>
                        <span>This artifact doesn't have any results</span>
                    </s:else></td>
            </tr>
        </table>
    </div>
    <!-- Sidebar -->
    <s:if test="%{binaries.size() != 0}">
        <div class="sidebar">
            <h2 style="margin-top: 0;">Binaries</h2>
            <s:iterator value="binaries">
                <s:a href="%{location}" title="Download the binary (recommended)" cssClass="boxlink green" cssStyle="margin-bottom: 1em;">
                    <span class="highlighted"><s:property value="fileType.displayName" /> Binary (<s:property value="classifier.displayName" />)</span>
                </s:a>
            </s:iterator>
            <p>Download one of these if you just want an executable program.</p>
        </div>
    </s:if>
</body>
</html>