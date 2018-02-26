<%@ page language="java" pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/view/common/common.jsp" %>
<!doctype html>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link href="${ctx}/resources/mallcss/initcss.css" rel="stylesheet">
    <link href="${ctx}/resources/mallcss/common.css" rel="stylesheet">
    <style>

        html, body {
            height: 100%;
        }

        .message {
            overflow: scroll;
            background-color: #EAEBEA;
            height: 100%;
            padding-bottom: 4em;
        }

        .footer {
            position: fixed;
            bottom: 0;
            width: 100%;
        }

        .edit {
            width: 100%;
            background-color: #F2F3F2;
        }

        .footer div {
            display: inline-block;
            float: left;
        }

        .vb, .eb, .add, .wb, .send {
            width: 14%;
            text-align: center;

        }

        .vb img, .eb img, .add img, .wb img {
            width: 3.5em;
            cursor: pointer;
            /*margin: 0 auto;*/
        }

        .rb, .sb {
            width: 58%;
            height: 3.5em;

        }

        .rb input {
            outline: none;
            border: none;
            border-bottom: 1px solid #D7D8D7;
            background-color: #F2F3F2;
            width: 100%;
            height: 2.1em;
            padding-top: 0.4em;
            /*padding-top: 0.2em;*/
            font-size: 1.2em;
            /*line-height: 22em;*/
        }

        .sb span {
            display: block;
            text-align: center;
            width: 95%;
            color: #818281;
            height: 2.5em;
            margin-top: 0.4em;
            line-height: 2.5em;
            border: 1px solid #DBDCDB;
        }

        .send {
            cursor: none;
        }

        .send span {
            cursor: pointer;
            background-color: #1AA818;
            color: white;
            display: block;
            width: 3em;
            height: 2.5em;
            margin-top: 0.5em;
            line-height: 2.5em;
            border-radius: 4px;
        }

        .bar {
            /*height: em;*/

            margin-top: 1em;
            overflow: hidden;
        }

        .my {
            padding-right: 0.5em;
        }

        .other {
            padding-left: 0.5em;
        }

        .bar div {
            display: inline-block;

        }

        .my div {
            float: right;
        }

        .other div {
            float: left;
        }

        .bar .img {
            height: 3em;
            width: 3em;

        }

        .my .img {
            margin-left: 1em;
        }

        .other .img {
            margin-right: 1em;
        }

        .bar .img img {
            width: 100%;
            height: 100%;
        }

        .bar .mes {
            display: inline-block;
            height:100%;
            word-break: break-all;
            position: relative;
            line-height: 3em;
            padding: 0 10px;
            /*background-color: #FEFFFE;*/

            min-height: 3em;
            border-radius: 4px;
            max-width: calc(100% - 10em);
        }

        .my .mes {
            background-color: #A1E75A;
        }

        .other .mes {
            background-color: #FEFFFE;
        }

        .mes:before {
            content: "";
            position: absolute;
            top: 0.8em;
            /*right: 100%;*/
            /*border-right-color: #fafafa;*/

            /*right: inherit;*/

        }

        .my .mes:before {
            left: 100%;
            border: 6px solid transparent;
            border-left-color: #A1E75A;
        }

        .other .mes:before {
            right: 100%;
            border: 6px solid transparent;
            border-right-color: #FEFFFE;
        }

        .mes :after {
            box-sizing: border-box;
        }

        .time {
            text-align: center;
            padding-top: 1em;
        }

        .time span {
            background-color: #D4D5D4;
            color: #FEFFFE;
            padding: 0.2em;
            font-size: 0.8em;
        }

        .blank{
            height: 3em;
        }


    </style>
    <title>${toAgent.realName}</title>
</head>
<body style="background: rgb(232, 232, 232);">
<div class="message">

    <!--<div class="my bar">-->

    <!--<div class="img">-->
    <!--<img src="resources/imagestem/ava.jpeg" alt="">-->
    <!--</div>-->

    <!--<div class="mes">-->
    <!--唐总你是nia ha xing 么！-->
    <!--</div>-->

    <!--</div>-->


    <!--<div class="time">-->
    <!--<span>16:34</span>-->
    <!--</div>-->


    <!--<div class="other bar">-->

    <!--<div class="img">-->
    <!--<img src="resources/imagestem/tw.png" alt="">-->
    <!--</div>-->

    <!--<div class="mes">-->
    <!--我是乐哈，乐哈就是我，请叫我乐哈-->
    <!--</div>-->

    <!--</div>-->

    <%--<div class="blank">--%>

    <%--</div>--%>
    <div class="other"></div>
</div>
<div class="footer">
    <div class="edit">
        <div class="vb ww">
            <img src="${ctx}/resources/mallimages/yy.png" alt="">
        </div>

        <div class="wb wv" style="display: none">
            <img src="${ctx}/resources/mallimages/wd.png" alt="">
        </div>

        <div class="sb wv" style="display: none">
            <span>按住 说话</span>
        </div>

        <div class="rb ww">
            <input type="text">
        </div>
        <div class="eb">
            <img src="${ctx}/resources/mallimages/bq.png" alt="">
        </div>
        <div class="add">
            <img src="${ctx}/resources/mallimages/add.png" alt="">
        </div>
        <div class="send" style="display: none">
            <span>发送</span>
        </div>
    </div>


</div>
<script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
<script src="http://ajax.microsoft.com/ajax/jquery.templates/beta1/jquery.tmpl.min.js"></script>
<!--<script src="https://cdn.bootcss.com/scrollup/2.4.1/jquery.scrollUp.js"></script>-->
<script src="https://cdn.bootcss.com/sockjs-client/1.1.4/sockjs.min.js"></script>
<script type="text/javascript" src="${ctx}/resources/malljs/mysocket.js"></script>
<script>
    $(function () {


        var mid = "${user.id}";
        var oid = "${toAgent.id}";
        var avatar = "${user.headimgurl}";
        var name = "${user.realName}";
        var code = "${user.agentCode}";

        var sockjs_url = "<c:url value='/messageHandler'/>";
        var sockjs = new SockJS(sockjs_url);
        sockjs.onopen = function () {
            console.log("连接成功");
        };
        sockjs.onmessage = function (e) {
            var msg = JSON.parse(e.data);
            if (msg.fromId == oid) {//是当前用户传过来的才显示
                showMessage(JSON.parse(e.data));
            }
            saveMsg(msg, msg.fromId);
        };
        sockjs.onclose = function () {
            console.log("连接关闭！");
        };
        sockjs.onerror = function (e) {
            console.log("发送失败！");
        };
        //离开网页前关闭
        window.onbeforeunload = function () {
            sockjs.close();
        }

        //发送消息封装
        function sendMessage(fromId, toId, avatar, msg, name, code) {
            var message = {
                "fromId": fromId,
                "toId": toId,
                "avatar": avatar,
                "msg": msg,
                "name": name,
                "code": code
            }
            sockjs.send(JSON.stringify(message));//发送
            return message;
        }


        showMessages(oid);


        $(".wb").click(function () {
            $(".ww").show();
            $(".wv").hide();
        });

        $(".vb").click(function () {
            $(".ww").hide();
            $(".wv").show();
        })

        $(".rb>input").keyup(function () {
            var value = $(this).val();
            if (value != null && value != "") {
                $(".add").hide();
                $(".send").show();
            } else {
                $(".send").hide();
                $(".add").show();
            }
        })

        $(".send").click(function () {
            var content = $("input").val();
            $("input").val("");
            $("input").keyup();
            var msg = sendMessage(mid, oid, avatar, content, name, code);//socket发送
            showMessage(msg);
//            $("#mt").tmpl(msg).appendTo($(".message"));//实时显示
            saveMsg(msg, oid);//存储数据
//            if ($(".message")[0].scrollHeight > $(window).height()) {
//                $('.message').animate({scrollTop: $(".message")[0].scrollHeight}, 200);
//            }
//
//            setTimeout(function () {
//                autoReplay(content);
//            }, 1000);
        })

//
//        //存储数据
//        var saveMsg = function (msg, key) {
//            var array = localStorage.getItem(key);
//            if (array == null) {
//                array = [];
//            } else {
//                array = JSON.parse(array);
//            }
//            array.push(msg);
//            localStorage.setItem(key, JSON.stringify(array));
//            console.log(msg + "保存成功" + (JSON.stringify(array)));
//        }


//        //删除聊天记录
//        var removeMessage = function (key, id) {
//            if (key < 0) {//清除所有
//                localStorage.clear();
//            } else {
//                if (id < 0) {//清楚当前聊天的所有
//                    localStorage.removeItem(key);
//                } else {
//                    var msgs = loadMessage(key);
//                    for (var msg in msgs) {
//                        if (msg.id == id) {
//                            msgs.splice(msg);
//                        }
//                    }
//                }
//            }
//
//        }

        //回车键监听
        $("input").keydown(function (e) {
            if (e.keyCode == 13) {
                $(".send").click();
            }
        })


        //渲染聊天记录
        function showMessages(key) {
            var messages = getMsgsArrayByUid(key);
//            console.log("加载的记录" + JSON.stringify(messages));
            $(".message").css("opacity", 0);
            for (var a in messages) {
                var msg = messages[a];
                msg.read = true;
                messages[a] = msg;
                showMessage(msg);
            }
            updateMessages(key,messages);//更新为read
//            console.log("更新的msg是"+JSON.stringify(messages));
//            if ($(".message")[0].scrollHeight > $(window).height()) {
//                $('.message').animate({scrollTop: $(".message")[0].scrollHeight}, 200);
//            }
        }

        function showMessage(msg) {
//            var msg = JSON.parse(data);
//            console.log("正在渲染" + JSON.stringify(msg));
            //渲染一条设置已经看过
            msg["read"] = true;
//            console.log("---设置看了"+msg.read);
            if (msg.fromId == mid) {//自己发的
                $("#mt").tmpl(msg).appendTo($(".message"));
            } else {//别人发的
                $("#ot").tmpl(msg).appendTo($(".message"));
            }
            $('.message').animate({scrollTop: $(".message")[0].scrollHeight+40}, 0);
            $(".message").css("opacity", 1);
        }


    })
</script>

<script id="mt" type="text/x-jquery-tmpl">
    <div class="my bar">
       <div class="img">
            <img src="{{= avatar}}" alt="">
        </div>

        <div class="mes">
           {{= msg}}
        </div>

    </div>








</script>


<script id="ot" type="text/x-jquery-tmpl">
    <div class="other bar">

        <div class="img">
            <img src="{{= avatar}}" alt="">
        </div>

        <div class="mes">
           {{= msg}}
        </div>
    </div>


</script>


</body>
</html>