<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Login</title>
</head>
<body>
    <s:form action="login" validate="true">
        <s:hidden name="nextAction" value="%{nextAction}" />
        <s:hidden name="nextNamespace" value="%{nextNamespace}" />
        <s:textfield key="username" label="Username" />
        <s:password key="password" label="Password" />
        <s:submit value="Login" />
    </s:form>
</body>
</html>