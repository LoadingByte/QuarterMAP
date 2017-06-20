<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Project List</title>
</head>
<body>
    <table>
        <tr>
            <th>Project</th>
        </tr>
        <s:iterator value="projects">
            <tr>
                <td><s:property value="name" /></td>
                <td><s:a action="details?projectId=%{name}">Details</s:a></td>
                <s:if test="#session['authenticated'].role.authorizations['ADMIN']">
                    <td><s:a action="edit_input?projectId=%{name}">Edit</s:a></td>
                    <td><s:a action="remove?projectId=%{name}">Remove</s:a></td>
                </s:if>
            </tr>
        </s:iterator>
    </table>
    <s:if test="#session['authenticated'].role.authorizations['ADMIN']">
        <s:a action="add">New Project</s:a>
    </s:if>
</body>
</html>