<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Change <s:property value="user.name" />&#039;s Password
</title>
</head>
<body>
    <s:form action="changePassword" validate="true">
        <s:hidden name="userId" value="%{userId}" />
        <s:password key="newPassword" label="New Password" />
        <s:password key="newPasswordRepetition" label="Repeat Password" />
        <s:submit value="Set New Password" />
    </s:form>
</body>
</html>