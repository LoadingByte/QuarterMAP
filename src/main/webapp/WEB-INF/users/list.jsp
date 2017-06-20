<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>User List</title>
</head>
<body>
    <table>
        <tr>
            <th>User</th>
            <th>E-Mail Address</th>
            <th>Role</th>
        </tr>
        <s:iterator value="users" var="user">
            <tr>
                <td><s:property value="name" /></td>
                <td><s:property value="email" /></td>
                <td><s:property value="role.displayName" /></td>
                <s:if test="#session['authenticated'].role.authorizations['ADMIN'] || #session['authenticated'].id == #user.id">
                    <td><s:a action="edit_input?userId=%{name}">Edit</s:a></td>
                    <td><s:a action="changePassword_input?userId=%{name}">Change Password</s:a></td>
                    <s:if test="#session['authenticated'].id != #user.id">
                        <td><s:a action="remove?userId=%{name}">Remove</s:a></td>
                    </s:if>
                </s:if>
            </tr>
        </s:iterator>
    </table>
    <s:if test="#session['authenticated'].role.authorizations['ADMIN']">
        <s:a action="add">New User</s:a>
    </s:if>
</body>
</html>