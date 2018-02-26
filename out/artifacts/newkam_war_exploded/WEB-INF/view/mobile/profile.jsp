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
    <link href="https://cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
    <style>

        .con {
            background-color: #ebebeb;
        }

        header {
            background: #f03791;
            height: 3.7em;
            text-align: center;
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
            background: url("resources/mallimages/png.png");
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

        .Title {
            color: white;
            font-size: 1.3em;
            line-height: 2.7em;
        }

        .info {
            background-color: #ffffff;
            overflow: hidden;
            margin-top: 1.5em;
        }

        .info div {
            float: left;
        }

        .avatar {
            height: 4.7em;
            width: 4.7em;
            padding: 0.8em 0.8em;
        }

        .avatar img {
            width: 100%;
            height: auto;
        }

        .profile {
            padding: 0.8em 0.8em;
        }

        .profile p {
            color: #a5a5a5;
            margin-top: 0.5em;
        }

        .profile i {
            color: #e37c6e;
        }

        .buttons {
            padding: 0em 0.8em;
        }

        .buttons div {
            padding: 1em 0;
            border-radius: 4px;
            text-align: center;
            background-color: #f8f8f8;
            margin-top: 1em;
            -webkit-box-shadow: 0 1px 2px rgba(0, 0, 0, .1);
        }

        .buttons div span {
            font-size: 1.1em;
        }

        .buttons div.click {

            background-color: #a1a1a1;
        }

        .buttons .chat {
            width: 100%;
            background-color: #27ab26;
            color: white;
        }

        .buttons .chat.click {
            background-color: #04be02;
        }

        .label {
            padding: 0.8em 0.8em;
            margin-top: 1em;
            background-color: #ffffff;
            overflow: hidden;
            height: 2em;
            line-height: 2em;
            cursor: pointer;
        }

        .label .h3 {
            /*float: left;*/
            /*width: 3.7em;*/
            font-size: 1.2em;

        }

        .label span.content {
            margin-left: 1.6em;
            color: #a5a5a5;
            /*line-height: 4em;*/
        }

        .label i {
            float: right;
            margin-right: 1em;
            line-height: 2em;
            font-size: 1.1em;
            color: #a5a5a5;
        }

        .moreInfo {
            margin-top: 1em;
        }

        .moreInfo .label {
            margin-top: 0;
            border-bottom: 1px solid #ebebeb;
        }

        .accounts {
            margin-top: 1em;

        }

        .accounts ul li {
            background-color: #FEFFFE;
            border-bottom: 1px solid #ebebeb;
            padding: 0.5em 0.5em;
            overflow: hidden;
            position: relative;

        }

        .accounts ul li img {
            width: 4em;
            border-radius: 4em;
            float: left;
        }

        .accounts .accountsInfo {
            margin-left: 1em;
            display: inline-block;
            padding-top: 0.6em
        }

        .accounts .accountsInfo h4 {
            color: #a5a5a5;
            padding-top: 0.8em;

        }

        /*.accounts .accountsInfo h3 {*/
        /*white-space: nowrap;*/
        /*overflow: hidden;*/
        /*text-overflow: ellipsis;*/
        /*max-width: 5em;*/

        /*}*/


    </style>
    <title>详细资料</title>
</head>
<body style="background: rgb(232, 232, 232);">
<div class="con">


    <div class="info">
        <div class="avatar">
            <img src="${agent.headimgurl}" alt="">
        </div>
        <div class="profile">
            <h3>${agent.realName} &nbsp; <i class="fa fa-user" aria-hidden="true"></i></h3>
            <p class="code">编号:${agent.agentCode}</p>
            <p class="phone">手机号:${agent.mobile}</p>
        </div>
    </div>


    <c:choose>
    <c:when test="${agent.id eq user.id||agent.parent.id eq user.id||up}">
    <div class="label level">
        <span class="h3">等级值</span>
        <span class="content">${levelName}</span>
        <i class="fa fa-angle-right" aria-hidden="true">库存</i>
    </div>
    </c:when>
    <c:otherwise>
    <div class="label level">
        <span class="h3">等级值</span>
        <span class="content noShow">****</span>
        <i class="fa fa-angle-right" aria-hidden="true"></i>
    </div>
    </c:otherwise>

    </c:choose>


    <!--不是公司-->
    <c:if test="${!agent.mutedUser}">


    <!--账户信息 上级才能显示-->
    <div class="moreInfo">
        <c:forEach items="${aTypes}" var="type">
            <div data-uid="${agent.id}" data-state="${type.state}" class="account label">
                <span class="h3">${type.stateInfo}</span>
                <c:choose>
                    <c:when test="${agent.id eq user.id||up}">
                        <span class="content">${agent.accounts.getAccount(type)}</span>
                    </c:when>
                    <c:otherwise>
                        <span class="content noShow">****</span>
                    </c:otherwise>
                </c:choose>
                <i style="color: red" class="fa fa-angle-right" aria-hidden="true">转账</i>
            </div>
        </c:forEach>
    </div>

    <!--产品库存 上级才能显示-->
    <div class="accounts" style="display: none">
        <ul>
            <c:forEach items="${goodsAccount}" var="gac">
                <li>
                    <img src="${dmzImgPath}${gac.goods.imgFile}" alt="">
                    <div class="accountsInfo">
                        <h3>${gac.goods.name}</h3>
                            <%--<h4>库存:${gac.currentBalance}</h4>--%>
                        <c:choose>
                            <c:when test="${agent.id eq user.id||up}">
                                <h4>库存:${gac.currentBalance}</h4>
                            </c:when>
                            <c:otherwise>
                                <h4>库存:****</h4>
                            </c:otherwise>
                        </c:choose>

                    </div>
                </li>
            </c:forEach>
        </ul>
    </div>

    <!--团队显示-->
    <c:if test="${agent.id eq user.id||team}">
    <div class="buttons">
        <div data-uid="${agent.id}" class="transferGoods"><span>我要发货</span></div>
    </div>
    </c:if>


    <!--拨货与授权 团队显示-->
    <c:if test="${agent.id ne user.id}">

    <div class="buttons">
            <%--<div class="chat" data-uid="${agent.id}"><span>发消息</span></div>--%>
        <c:if test="${agent.agentCode!=null&&team}">
            <div data-uid="${agent.id}" class="transferGoods"><span>给他拨货</span></div>
            <!--不是上级-->
            <c:if test="${user.parent.id ne agent.id}">
                <div class="enterCode" data-owner="${agent.realName}" data-code="${agent.agentCode}"><span>录防伪码</span>
                </div>
            </c:if>
            <c:if test="${myLevelName eq '联合股东' && user.parent.id ne agent.id}">
                <div data-id="${agent.id}" class="changeLevel"><span>给他授权</span></div>
                <div class="levels" style="display: none">
                    <select>
                        <option value="0">选择级别</option>
                        <c:forEach items="${levels}" var="lv">
                            <option value="${lv.id}">
                                    ${lv.name}
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </c:if>

        </c:if>
    </div>
    </c:if>


    <!--上级能看下级-->
    <c:if test="${up}">
    <div class="buttons">
        <div data-uid="${agent.id}" class="showContacts"><span>查看市场</span></div>
    </div>
    </c:if>


    </c:if>

    <!--是公司-->
    <c:if test="${agent.mutedUser}">
    <div class="buttons">
        <div data-uid="${agent.id}" class="backTransferGoods"><span>申请退货</span></div>

        </c:if>
    </div>

    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
    <script type="text/javascript">
        //转券
        $(".account").click(function () {
            var state = $(this).attr("data-state");
            var uid = $(this).attr("data-uid");
            <c:if test="${user.id eq agent.id}">
            return;
            </c:if>
            <%--var url = "<c:url value="/mobile/accounts/transfer.html"/>" + "?uid=" + uid + "&state=" + state;--%>
//            window.location.href = url;
            alert("暂时不允许转货!");
        });
        //转库存
        $(".transferGoods").click(function () {
            var uid = $(this).attr("data-uid");
            var url = "<c:url value="/mobile/goods/transfer.html"/>" + "?toUid=" + uid;
            window.location.href = url;
        })
        //申请退货
        $(".backTransferGoods").click(function () {
            var url = "<c:url value="/mobile/goods/backTransfer.html"/>";
            window.location.href = url;
        })
        $(".level").click(function () {
            $(".accounts").show();
        })

        //发消息
        $(".chat").click(function () {
            var uid = $(this).attr("data-uid");
            var url = "<c:url value="/mobile/chat.html"/>" + "?toId=" + uid;
            window.location.href = url;
        })

        //检查是否可以授权
        $(".changeLevel").click(function () {
            var url = "<c:url value="/mobile/isTeam.json"/>";
            var aid = $(this).attr("data-id");
            var param = {
                "aid": aid
            }
            $.post(url, param, function (data, status, jqXHR) {
                var m = data;
                if (m.flag == "0") {
                    //显示可以授权
                    $(".changeLevel").hide();
                    $(".levels").show();
                } else {
                    alert("操作失败" + m.message);
                }
            }).fail(function () {
                alert("未知错误请联系管理员");
            })
        })

        $(".showContacts").click(function () {
            var uid = $(this).attr("data-uid");
            var url = "<c:url value="/mobile/contacts.html?uid="/>" + uid;
            window.location.href = url;
        })


        $(".levels select").change(function () {
            var value = $(this).val();
            var aid = $(".changeLevel").attr("data-id");
            if (value != 0) {
                var url = "<c:url value="/mobile/changeLevel.json"/>";
                var param = {
                    "aid": aid,
                    "lid": value
                }
                $.post(url, param, function (data) {
                    var m = data;
                    if (m.flag == 0) {
                        alert("修改成功!");
                        window.location.reload();
                    } else {
                        alert("修改失败" + m.message);
                    }

                }).fail(function (xhr) {
                    alert("未知错误,请联系管理员!");
                })
            }
        })
        $(".enterCode").click(function () {
            var owner = $(this).attr("data-owner");
            var code = $(this).attr("data-code");
            var url = "<c:url value="/securityCode/scan_num.html?owner=param1&&agentCode=param2"/>";
            url = url.replace("param1", owner);
            url = url.replace("param2", code);
            window.location.href = url;
        })

    </script>

</body>
</html>
