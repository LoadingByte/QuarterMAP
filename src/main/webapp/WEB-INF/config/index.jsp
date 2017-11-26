<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Config</title>
</head>
<body>
    <h2>Artifact Repositories</h2>
    <table>
        <tr>
            <th>Repository</th>
            <th>Host</th>
            <th>Cache Parser</th>
        </tr>
        <s:iterator value="artifactRepositories">
            <tr>
                <td><s:property value="name" /></td>
                <td><s:property value="configuration.location.host" /></td>
                <td><s:property value="configuration.cacheParserName" /></td>
                <td><s:a action="artifactrepoEdit_input?repositoryId=%{name}">Edit</s:a></td>
                <td><s:a action="artifactrepoRemove?repositoryId=%{name}">Remove</s:a></td>
            </tr>
        </s:iterator>
    </table>
    <s:a action="artifactrepoAdd">New Artifact Repository</s:a>
</body>
</html>