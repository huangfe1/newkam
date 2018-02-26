<%@ page language="java" pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/view/common/common.jsp" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link href="${ctx}/resources/mallcss/initcss.css" rel="stylesheet">
    <link href="${ctx}/resources/mallcss/common.css" rel="stylesheet">
    <style rel="stylesheet">
        header {
            width: 100%;
            height: 3.7em;
            background: #f03791;
            border-bottom: 1px solid #ddd;
        }

        header.fixed {
            max-width: 640px;
            position: fixed;
            /*left: 0;*/
            top: 0;
            z-index: 99;
        }

        header .Title {
            color: white;
            font-size: 1.3em;
            line-height: 2.7em;
        }

        .header {
            /*margin:0 20px;*/
            text-align: center;
            color: #4e4a49;
            font-size: 1em;
            height: 45px;
            line-height: 45px;
            position: relative;
        }

        .Return, .Home {
            position: absolute;
            width: 3.7em;
            height: 3.7em;
        }

        .Return {
            left: 0;
        }

        .Home {
            right: 0;
        }

        .Return span, .Home span {
            background: url("${ctx}/resources/mallimages/png.png");
            background-size: 17em;
            position: absolute;
            width: 1.7em;
            height: 1.7em;
        }

        .Return span {
            background-position: -0.17em -5.5em;
            left: 1em;
            top: 1em;
        }

        .Home span {
            background-position: -0.17em -8.1em;
            left: 1em;
            top: 1em;
        }

        #letter img {
            width: 50px;
            height: 50px;
            float: left;
            margin: 25px 0px 0px 25px;
        }

        .sort_box {
            width: 100%;
            /*padding-top: 1em;*/
            overflow: hidden;
            padding-bottom: 4em;
        }

        .sort_list {
            padding: 0.5em 0 0.5em 5em;
            position: relative;
            /*height: 40px;*/
            /*line-height: 40px;*/
            border-bottom: 1px solid #ddd;
            cursor: pointer;
        }

        .sort_list .num_logo {

            /*border-radius: 10px;*/
            overflow: hidden;
            position: absolute;

            top: 0.5em;
            left: 1em;
            /*background-color: #2782D2;*/
            width: 3.5em;
        }

        .sort_list .num_logo img {
            width: 100%;
            /*height: 50px;*/
        }

        .sort_list .num_name {
            /*color: #000;*/
            /*line-height: 2.8em;*/
            /*font-size: 1em;*/
        }

        .num_name .fname {
            display: block;
            font-size: 1.1em;
            line-height: 1.5em;
            padding-top: 0.1em;
        }

        .num_name .fmsg {
            display: block;
            font-size: 1em;
            line-height: 1.5em;
            padding-top: 0.2em;
            padding-bottom: 0.2em;
            color: #a5a5a5;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            width: 90%;
        }

        .initials li img {
            width: 14px;
        }

        .company ul li {
            margin-top: -1px;
            border-top: 1px solid #ddd;;
            position: relative;
            overflow: hidden;
            /*height: 4em;*/
            padding: 0.5em 0 0.5em 5em;
            cursor: pointer;
        }

        .company ul li .img {
            position: absolute;
            top: 0.5em;
            left: 1em;
            /*background-color: #2782D2;*/
            width: 3em;
            /*padding: 0.5em;*/
        }

        .company ul li .img.sq {
            /*background-color: #43BF18;*/
        }

        .company ul li .img img {
            display: inline-block;
            width: 100%;
        }

        .company ul li span {
            line-height: 2.5em;
            font-size: 1.2em;
        }

        div.Title {
            padding: 0.4em 0 0.4em 1em;
            background-color: #EAEBEA;
            color: #a5a5a5;
            font-size: 0.9em;
        }

        .name input {
            font-size: 1.2em;
            height: 2.2em;
            width: 8em;
            outline: none;
            background-color: #F6F7F6;
            border: none;

            box-shadow: none;
            -webkit-box-shadow: none;
            -moz-box-shadow: none;
            text-shadow: none;
        }



        .badges span {
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
        }

    </style>
    <title>消息</title>
</head>
<body>

<%--<header class="fixed">--%>
<%--<div class="header">--%>
<%--<a href="" class="Return"><span></span></a>--%>
<%--<span class="Title">通讯录</span>--%>
<%--<a href="" class="Home"><span></span></a>--%>
<%--</div>--%>
<%--</header>--%>


<div class="sort_box">

    <%--<div class="sort_list" data-id="1">--%>
    <%--<div class="num_logo">--%>
    <%--<img src="${ctx}/resources/mallimages/sq.jpg" alt="">--%>
    <%--</div>--%>
    <%--<div class="badges">--%>
    <%--<span>1</span>--%>
    <%--</div>--%>
    <%--<div class="num_name">--%>
    <%--<span class="fname">授权客服&nbsp;zmz123456</span>--%>
    <%--<span class="fmsg">我先问问，谢谢拉拉啊实打实大所大所大所多阿斯达打算多</span>--%>
    <%--</div>--%>
    <%--</div>--%>


    <%--<script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>--%>

    <script type="text/javascript" src="${ctx}/resources/malljs/mysocket.js"></script>
    <jsp:include page="/WEB-INF/view/mobile/appfooter.jsp"/>


    <script type="text/javascript">
        $(function () {
            showMessages();

            //从本地加载所有聊天记录
            function showMessages() {
                var array = getMsgsArray();//所有的聊天
                var uid = "";
                var name = "";
                var code = "";
                var avatar = "";
                var content = "";
                for (var a in array) {
                    var msgs = array[a];
                    var count = 0;
                    for (var i in msgs) {
                        var msg = msgs[i];
                        if (msg["fromId"] != a) {//跳过自己的聊天
                            continue;
                        }
//                       console.log("read+"+msg["read"]);
                        if (msg["read"] != true) {
                            count++;
                        }
                        name = msg["name"];
                        code = msg["code"];
                        avatar = msg["avatar"];
                        uid = msg["fromId"];
                        content = msg["msg"];
                    }
                    showMessage(uid, avatar, name, code, content, count);
                }
            }

            function showMessage(uid, avatar, name, code, msg, count) {
                var data = {
                    "fromId": uid,
                    "avatar": avatar,
                    "name": name,
                    "code": code,
                    "msg": msg,
                    "count": count
                }
                var html = $("#chat").tmpl(data);
                if (count == 0) {
//                    console.log("---"+html.find(".badges"));
                    html.find(".badges").remove();
//                    html.remove(".badges");
                } else {
                    html.find(".fmsg").css("color", "#449103");
                }
                html.appendTo($(".sort_box"));
            }

            $(".sort_box").delegate(".sort_list","click",function () {
                var toUid = $(this).attr("data-id");
                var url = "<c:url value="/mobile/chat.html?toId="/>"+toUid;
                window.location.href = url;
            })

        });
    </script>

    <script id="chat" type="text/x-jquery-tmpl">
    <div class="sort_list" data-id="{{= fromId}}">
        <div class="num_logo">
            <img src="{{= avatar}}" alt="">
        </div>

         <div class="badges">
            <span>{{= count}}</span>
        </div>

        <div class="num_name">
            <span class="fname">{{= name}}&nbsp;{{= code}}</span>
            <span class="fmsg">{{= msg}}</span>
        </div>

    </div>

    </script>

</body>
</html>
</html>
