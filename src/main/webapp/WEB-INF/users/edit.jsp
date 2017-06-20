<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Edit <s:property value="user.name" /></title>
</head>
<body>
    <s:form action="edit" validate="true">
        <s:hidden name="userId" value="%{userId}" />
        <s:textfield key="user.name" label="Username" />
        <s:textfield key="user.email" label="E-Mail Address" />
        <s:if test="#session['authenticated'].role.authorizations['ADMIN']">
            <s:select key="user.role" list="availableRoles" label="Role" />
        </s:if>
        <s:else>
            <s:select value="%{user.role}" list="availableRoles" label="Role" disabled="true" />
        </s:else>
        <s:submit value="Save Changes" />
    </s:form>
</body>
</html>