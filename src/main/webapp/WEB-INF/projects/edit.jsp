<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Edit <s:property value="project.name" /></title>
</head>
<body>
    <s:form action="edit" validate="true">
        <s:hidden name="projectId" value="%{projectId}" />
        <s:textfield key="project.name" label="Project Name" />
        <s:textfield key="project.configuration.artifact.groupId" label="Group ID" />
        <s:textfield key="project.configuration.artifact.artifactId" label="Artifact ID" />
        <tr>
            <td colspan="2"><hr /></td>
        </tr>
        <s:textfield key="newSourceRepository" value="%{project.configuration.sourceRepository}" label="Source Repository" />
        <s:textfield key="newJenkinsJob" value="%{project.configuration.jenkinsJob}" label="Jenkins URL" />
        <s:textfield key="newSonarJob" value="%{project.configuration.sonarJob}" label="Sonar URL" />
        <s:submit value="Save Changes" />
    </s:form>
</body>
</html>