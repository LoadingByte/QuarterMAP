<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Finished!</title>
</head>
<body>
    <p>
        You managed to successfully set up the
        <s:text name="title" />
        web service!
    </p>
    <p>
        What about
        <s:a action="login_input">logging in</s:a>
        with the default credentials
        <code>admin</code>
        and the super secret password
        <code>admin</code>
        and changing them before others do so?
    </p>
</body>
</html>