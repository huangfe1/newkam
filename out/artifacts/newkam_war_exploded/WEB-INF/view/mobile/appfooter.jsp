<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/view/common/common.jsp"%>
<style>
    .footer {
        position: fixed;
        bottom: 0;
        background-color: #ffffff;
        width: 100%;
        max-width: 35.5em;
        overflow: hidden;
        height: 4em;
        z-index: 100;
    }

    .footer a {
        display: inline-block;
        width: 25%;
        float: left;
        text-align: center;
        padding: 0.5em 0 0.3em;
        cursor: pointer;
    }



    .footer a span{

        display: inline-block;
        width: 1.8em;
        height: 1.8em;
        padding: 0;
        margin: 0;
    }
    .footer a p{
        font-size: 1em;
        margin-top: 0.3em;
        color: #a5a5a5;
    }

    .footer a .xx{

        background: url("${ctx}/resources/mallimages/xx.png") no-repeat;
        background-size: 1.8em;
    }

    .footer a .txl{
        background: url("${ctx}/resources/mallimages/txl.png") no-repeat;
        background-size: 1.8em;
    }

    .footer a .fx{
        background: url("${ctx}/resources/mallimages/dbfx.png") no-repeat;
        background-size: 1.8em;
    }

    .footer a .me{
        background: url("${ctx}/resources/mallimages/me.png") no-repeat;
        background-size: 1.8em;
    }

    #badges span {
        display: inline-block;
        width: 1.5em;
        height: 1.5em;
        text-align: center;
        line-height: 1.5em;
        background: #FA4136;
        border-radius: 1em;
        color: white;
        position: absolute;
        left: 3.5em;
        top: 0em;
        display: none;
    }


</style>
<script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
<script src="http://ajax.microsoft.com/ajax/jquery.templates/beta1/jquery.tmpl.min.js"></script>
<script src="https://cdn.bootcss.com/sockjs-client/1.1.4/sockjs.min.js"></script>
<script type="text/javascript" src="${ctx}/resources/malljs/mysocket.js"></script>

<div class="footer">
    <a class="item" href="<c:url value="/mobile/chats.html"/>">
            <span class="xx" >&nbsp;</span>
            <p>消息</p>
    </a>

    <div id="badges">
        <span>1</span>
    </div>

    <a class="item" href="<c:url value="/mobile/contacts.html"/>">
            <span class="txl" >&nbsp;</span>
            <p>通讯录</p>
    </a>
    <a class="item" href="<c:url value="/mobile/find.html"/>">
            <span class="fx" >&nbsp;</span>
            <p>发现</p>
    </a>
    <a class="item" href="<c:url value='/mobile/my.html'/>">
            <span class="me" >&nbsp;</span>
            <p>我</p>
    </a>
</div>


<div style="display: none" class="audio">
    <audio id="errorAudio" src="http://dx.sc.chinaz.com/Files/DownLoad/sound1/201707/8906.mp3" >
        Your browser does not support the audio element.
    </audio>
</div>

<%--<script>--%>
    <%--$(function () {--%>

        <%--&lt;%&ndash;var mid = "${user.id}";&ndash;%&gt;--%>
        <%--&lt;%&ndash;var oid = "${toAgent.id}";&ndash;%&gt;--%>
        <%--&lt;%&ndash;var avatar = "${user.headimgurl}";&ndash;%&gt;--%>
        <%--&lt;%&ndash;var name = "${user.realName}";&ndash;%&gt;--%>
        <%--&lt;%&ndash;var code = "${user.agentCode}";&ndash;%&gt;--%>

<%--//        alert("---");--%>

        <%--var sockjs_url = "<c:url value='/messageHandler'/>";--%>
        <%--var sockjs = new SockJS(sockjs_url);--%>
        <%--sockjs.onopen = function () {--%>
            <%--console.log("连接成功");--%>
        <%--};--%>
        <%--sockjs.onmessage = function (e) {--%>
            <%--var msg = JSON.parse(e.data);--%>
<%--//            if (msg.fromId == oid) {//是当前用户传过来的才显示--%>
<%--//                showMessage(JSON.parse(e.data));--%>
<%--//            }--%>
            <%--$("#errorAudio").get(0).play();--%>
            <%--$("#badges span").show();--%>
            <%--//保存即可--%>
            <%--saveMsg(msg, msg.fromId);--%>
        <%--};--%>
        <%--sockjs.onclose = function () {--%>
            <%--console.log("连接关闭！");--%>
        <%--};--%>
        <%--sockjs.onerror = function (e) {--%>
            <%--console.log("发送失败！");--%>
        <%--};--%>
        <%--//离开网页前关闭--%>
        <%--window.onbeforeunload = function () {--%>
            <%--sockjs.close();--%>
        <%--}--%>
    <%--})--%>
<%--</script>--%>


