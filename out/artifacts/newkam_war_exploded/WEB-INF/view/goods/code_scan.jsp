<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/view/common/common.jsp" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>扫码录入</title>
    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.bootcss.com/font-awesome/4.6.3/css/font-awesome.min.css" rel="stylesheet">
    <script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
    <style type="text/css">
        th {
            text-align: center;
        }

        td {
            text-align: center;
        }

    </style>
</head>
<body style="background:white;">


<nav class="navbar navbar-default ">
    <div class="container-fluid">
        <form class="form-inline navbar-form navbar-left" action="/securityCode/code/scan.json">

            <div class="form-group"  <c:if test="${!user.admin}"> style="display:none;"</c:if> >
                <label for="goodsName">产品名</label>
                <select class="form-control" name="goodsName" id="goodsName">
                    <option value="洗眼液">洗眼液</option>
                    <option value="玻尿酸">玻尿酸</option>
                </select>
            </div>

            <div class="form-group">
                <label for="owner">接纳人</label>
                <input class="form-control typeahead" id="owner" name="owner" value="${param.owner}">
            </div>

            <div class="form-group">
                <label for="owner">接纳人编号</label>
                <input class="form-control typeahead" id="agentCode" name="agentCode" value="${param.agentCode}">
            </div>

            <button type="button" id="scanBtn" class="btn btn-info">
                <li class="fa fa-search"></li>
                扫一扫
            </button>

            <button type="button" id="okBtn" class="btn btn-danger">
                <li class="fa fa-search"></li>
                录入
            </button>

        </form>

    </div>


</nav>


<div class="row">
    <div class="col-lg-12 col-md-12">
        <div class="table-responsive">
            <table id="dama" class="table table-striped table-bordered table-hover table-condensed">
                <thead>
                <tr>
                    <th>大码号</th>
                    <th>大码操作</th>
                </tr>
                </thead>
                <tbody id="bList">

                </tbody>
            </table>
        </div>
    </div>
</div>


<div class="row">
    <div class="col-lg-12 col-md-12">
        <div class="table-responsive">
            <table id="xiaoma" class="table table-striped table-bordered table-hover table-condensed">
                <thead>
                <tr>
                    <th>小码号</th>
                    <th>小码操作</th>
                </tr>
                </thead>
                <tbody id="cList">
                <tr class="ctr">
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>


<script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
<script src="https://cdn.bootcss.com/jquery.form/4.2.2/jquery.form.min.js"></script>
<script src="http://ajax.microsoft.com/ajax/jquery.templates/beta1/jquery.tmpl.min.js"></script>
<script src="${ctx}/resources/malljs/scode.js?ref=1" type="text/javascript"></script>

<script>


    $(function () {
        var jp = ${jsapiParamJson};
//        jp = $.parseJSON(jp);
        wx.config({
            debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
            appId: jp.appId, // 必填，公众号的唯一标识
            timestamp: jp.timestamp, // 必填，生成签名的时间戳
            nonceStr: jp.nonceStr, // 必填，生成签名的随机串
            signature: jp.signature,// 必填，签名，见附录1
            jsApiList: [
                'scanQRCode'
            ]
        });

        //扫码
        $("#scanBtn").click(function () {
            //扫一扫
            wx.scanQRCode({
                needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
                scanType: ["qrCode", "barCode"], // 可以指定扫二维码还是一维码，默认二者都有
                success: function (res) {
                    code = res.resultStr;
                    addCode(code);
                }
            });
        });


        function addCode(code) {
            if (!validateUrl(code)) {
                alert("码规则有误，请联系管理员!");
                return;
            }
            var c = getCodeFromCodeStr(code);//码号
            //模板数据装填
            var data = {
                "c": c,
                "code": code
            }
            if (isBox(code)) {//大箱
                try {
                    //判断是否有重复
                    if (validateRepeat($(".btd"), c)) {
                        alert("重复录入大码!");
                        return;
                    }
                $("#bList").append($("#bt").tmpl(data));
                    setTimeout(function () {
                        $("#scanBtn").trigger("click");
                    },1000);

                }catch (err){
                    alert(err);
                }
            } else {
                //判断是否有重复
                if (validateRepeat($(".ctd"), c)) {
                    alert("重复录入小码!");
                    return;
                }
                $("#cList").append($("#ct").tmpl(data));
                $("#scanBtn").trigger("click");
            }
        }


        //提交
        $("#okBtn").click(function () {

            var boxs = [];

            var codes = [];

            $(".ctd").each(function () {
                codes.push($(this).html());
            })

            $(".btd").each(function () {
                boxs.push($(this).html());
            })

            var params = {
                "boxs": boxs,
                "codes": codes,
                "goodsName": $("#goodsName").val(),
                "owner": $("#owner").val(),
                "agentCode": $("#agentCode").val()
            }
            params = $.param(params, true);//切记这里 不然数组传不过去
            var url = "<c:url value="/securityCode/code/scan.json"/>";

            $.post(url, params, function (m) {
                if (m.flag == 0) {//录入成功
                    alert("录入成功！");
                    //三秒后刷新
                    setTimeout(function () {
                        window.location.reload();
                    }, 300);
                } else {
                    alert("绑定失败" + m.message);
                }
            }).fail(function (xhr) {
                alert("未知错误！请联系管理员");
            });

        })

        //删除事件
        $(".table").delegate(".ajaxLink", "click", function () {
            $(this).closest("tr").remove();
        })


    })

</script>

<!--大箱td模板-->
<script id="bt" type="text/x-jquery-tmpl">
    <tr class="btr">
    <td class="btd">{{= c}}</td>
    <td><a class="ajaxLink btn btn-danger default" data-role="delete" ><span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除</a>
    </tr>


</script>

<!--小箱td模板-->
<script id="ct" type="text/x-jquery-tmpl">
  <tr class="ctr">
    <td class="ctd">{{= c}}</td>
    <td><a class="ajaxLink btn btn-danger default" data-role="delete" ><span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除</a>
    </tr>



</script>


</body>
</html>
