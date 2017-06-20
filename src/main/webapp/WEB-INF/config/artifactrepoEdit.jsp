<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Edit <s:property value="repository.name" /></title>
</head>
<body>
    <s:form action="artifactrepoEdit" validate="true">
        <s:hidden name="repositoryId" value="%{repositoryId}" />
        <s:textfield key="repository.name" label="Name" />
        <s:textfield name="newLocation" value="%{repository.configuration.location}" label="Location" />
        <s:select key="cacheParser" list="availableCacheParsers" label="Cache Parser" />
        <s:submit value="Save Changes" />
    </s:form>
</body>
</html>