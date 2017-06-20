<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><s:text name="title" /> - <decorator:title /></title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/data/styles/layout.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/data/styles/style.css" />
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/data/images/favicon.png" />
<s:head />
<decorator:head />
</head>
<body>
    <div id="container">
        <div id="header">QuarterCode Artifacts</div>
        <div id="navigation">
            <ul>
                <li><s:a action="index" namespace="/">Home</s:a></li>
                <li><s:a action="list" namespace="/projects">Projects</s:a></li>
                <s:if test="#session['authenticated'] != null">
                    <li><s:a action="list" namespace="/users">Users</s:a></li>
                    <li><s:a action="list" namespace="/config">Config</s:a></li>
                </s:if>
            </ul>
        </div>
        <div id="content">
            <h1>
                <decorator:title />
            </h1>
            <decorator:body />
        </div>
        <div id="footer">
            QuarterMAP - Developed by LoadingByte -
            <s:if test="#session['authenticated'] != null">
                <span>Logged in as <s:property value="#session['authenticated'].name" /> - <s:a action="logout" namespace="/">Logout</s:a></span>
            </s:if>
            <s:else>
                <s:url action="login_input" namespace="/" var="loginUrl">
                    <s:param name="nextAction">
                        <s:property value="#request['struts.actionMapping'].name" />
                    </s:param>
                    <s:param name="nextNamespace">
                        <s:property value="#request['struts.actionMapping'].namespace" />
                    </s:param>
                </s:url>
                <s:a href="%{loginUrl}">Login</s:a>
            </s:else>
        </div>
    </div>
</body>
</html>