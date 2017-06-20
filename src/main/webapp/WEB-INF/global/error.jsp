<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Error</title>
</head>
<body>
    <p>
        Sorry, but there was something wrong with your request :(
        <br />
        We weren't able to handle it.
    </p>
    <s:if test="#request['javax.servlet.forward.query_string'] != null">
        <p>
            <s:a href="%{#request['struts.request_uri'] + '?' + #request['javax.servlet.forward.query_string']}">Try again</s:a>
            &nbsp;
            <s:a href="%{#request['struts.request_uri']}">Try without parameters</s:a>
        </p>
    </s:if>
    <s:else>
        <p>
            <s:a href="%{#request['struts.request_uri']}">Try again</s:a>
        </p>
    </s:else>
</body>
</html>