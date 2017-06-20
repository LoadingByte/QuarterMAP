<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Home</title>
</head>
<body>
    <!-- Static content -->
    <h2>Welcome!</h2>
    <p>
        This is the QuarterCode download site. It presents the built artifacts of out projects to end users. For doing that, this site queries our <a
            href="http://repo.quartercode.com" title="QuarterCode Maven Repository" target="_blank">public maven repository</a> and filters out the
        relevant artifacts. You can start browsing by clicking on the Projects tab in the navigation bar.
    </p>
    <p>Understanding which artifacts are presented and why can be a bit difficult. In order to offer all important files to users which just want
        to download the latest release or next cycle preview (alpha/beta), we added large color-coded buttons. There's a very basic rule for getting
        the latest release: Always click the big green buttons:</p>
    <div style="text-align: center;">
        <s:a action="index" namespace="/projects" title="Browse the project list" cssClass="boxlink green">
            <span class="highlighted">Browse Projects</span>
        </s:a>
    </div>
    <h2>Cycles ... what?</h2>
    <p>
        If you want to learn how our release system functions, you can read our wiki article <a
            href="http://quartercode.com/wiki/Branching_%26_Release_Model" title="Wiki article Branching &amp Release Model" target="_blank">Branching
            &amp; Release Model</a>. It explains the release system in every detail. However, the article is written for developers so it also covers the
        source repository model. If that's too much for you and you just want to learn when and why releases come out, you can try the section <a
            href="http://quartercode.com/wiki/Branching_%26_Release_Model#Compact_Release_Model" title="Wiki article Compact Release Model"
            target="_blank">Compact Release Model</a>.
    </p>
</body>
</html>