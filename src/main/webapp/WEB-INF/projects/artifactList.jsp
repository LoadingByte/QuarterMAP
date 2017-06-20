<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Artifact List</title>
<style type="text/css">
#artifactList {
    width: 40em;
}

#content tr.artifactRow td {
    padding: 0.5em 2em;
}

#content tr.artifactRow {
    text-align: center;
    transition: background-color 0.25s ease-in-out;
}

#content tr.artifactRow.snapshot {
    background-color: #ffcccc;
}

#content tr.artifactRow.snapshot:hover {
    background-color: #ffaaaa;
}

#content tr.artifactRow.alpha {
    background-color: #ffdcaa;
}

#content tr.artifactRow.alpha:hover {
    background-color: #ffc879;
}

#content tr.artifactRow.beta {
    background-color: #fff99f;
}

#content tr.artifactRow.beta:hover {
    background-color: #fff836;
}

#content tr.artifactRow.release {
    background-color: #aaffaa;
}

#content tr.artifactRow.release:hover {
    background-color: #66ff66;
}

#content input,select {
    font-size: 0.9em;
}

.pagebanner,.pagelinks {
    display: none; /* Hide page banner and default link section */
}

.pagecontrols {
    text-align: center;
    list-style-type: none;
}

.pagecontrols li {
    display: inline-block;
    font-size: 1.2em;
}

.pagecontrols li a {
    text-decoration: none;
}
</style>
</head>
<body>
    <!-- Main content -->
    <div class="center">
        <p class="highlighted">
            Artifact:
            <s:property value="artifactId" />
        </p>
        <s:if test="%{artifacts.size != 0}">
            <display:table name="artifacts" id="artifactList" pagesize="20" requestURI=""
                decorator="com.quartercode.quartermap.action.projects.ProjectArtifactListAction$ArtifactListTableDecorator">
                <display:setProperty name="paging.banner.full">
                    <ul class="pagecontrols">
                        <li><a href="{1}" title="First">&lt;&lt;</a></li>
                        <li><a href="{2}" title="Previous">&lt;</a></li>
                        <li>{0}</li>
                        <li><a href="{3}" title="Next">&gt;</a></li>
                        <li><a href="{4}" title="Last">&gt;&gt;</a></li>
                    </ul>
                </display:setProperty>
                <display:setProperty name="paging.banner.first">
                    <ul class="pagecontrols">
                        <li>&lt;&lt;</li>
                        <li>&lt;</li>
                        <li>{0}</li>
                        <li><a href="{3}" title="Next">&gt;</a></li>
                        <li><a href="{4}" title="Last">&gt;&gt;</a></li>
                    </ul>
                </display:setProperty>
                <display:setProperty name="paging.banner.last">
                    <ul class="pagecontrols">
                        <li><a href="{1}" title="First">&lt;&lt;</a></li>
                        <li><a href="{2}" title="Previous">&lt;</a></li>
                        <li>{0}</li>
                        <li>&gt;</li>
                        <li>&gt;&gt;</li>
                    </ul>
                </display:setProperty>
                <display:column title="Version" property="version" />
                <display:column title="Type" property="version"
                    decorator="com.quartercode.quartermap.action.projects.ProjectArtifactListAction$TypeColumnDecorator" />
                <display:column title="" property="version"
                    decorator="com.quartercode.quartermap.action.projects.ProjectArtifactListAction$DetailsColumnDecorator" />
            </display:table>
        </s:if>
        <s:else>
            <p>Sorry, but there don't seem to be any artifacts matching your filters :(</p>
        </s:else>
    </div>
    <!-- Sidebar -->
    <div class="sidebar">
        <h2 style="margin-top: 0;">Filters</h2>
        <s:form id="filters" method="get">
            <s:hidden name="projectId" value="%{projectId}" />
            <s:select key="cycle" list="availableCycles" label="Cycle" />
            <s:select key="channel" list="availableChannels" label="Channel" />
            <s:submit value="Search" />
        </s:form>
    </div>
</body>
</html>