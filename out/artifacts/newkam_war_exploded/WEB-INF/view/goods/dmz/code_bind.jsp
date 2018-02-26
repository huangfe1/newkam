<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/view/common/common.jsp" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>大小码绑定</title>
    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
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
        <div class="form-inline navbar-form navbar-left" action="<c:url value="/securityCode/code/create.json"/>">

            <div class="form-group">
                <label for="code">防伪码</label>
                <input class="form-control typeahead" id="code" name="code" autofocus>
            </div>

            <div class="form-group">
                <label for="perBox">大码对应的小码数</label>
                <input class="form-control" type="number" id="perBox" name="perBox" value="${param.perBox}">
            </div>


            <button role="btn" type="button" class="btn btn-danger" id="cbtn">手动提交</button>
            <a id="download">离线下载</a>


        </div>

    </div>


</nav>


<nav class="navbar navbar-default ">
    <div class="container-fluid">
        <form   method="post" class="form-inline navbar-form navbar-left" action="<c:url value="/securityCode/dmz/code/bind.json"/>" enctype="multipart/form-data" >

            <div class="form-group">
                <label for="bfile">大码文件</label>
                <input type="file" class="form-control typeahead" id="bfile" name="bfile" >
            </div>

            <div class="form-group">
                <label for="cfile">小码文件</label>
                <input type="file" class="form-control typeahead" id="cfile" name="cfile" >
            </div>

            <div class="form-group">
                <label for="perBox">大码对应小码数</label>
                <input type="number" class="form-control typeahead"  name="perBox" value="${param.perBox}" >
            </div>

            <button role="btn" type="button"  id="off" class="btn btn-danger">离线提交</button>

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
                    <th>大码链接</th>
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
                    <th>小码链接</th>
                    <th>小码操作</th>
                </tr>
                </thead>
                <tbody id="cList">

                </tbody>
            </table>
        </div>
    </div>
</div>


<div class="row">
    <div class="col-lg-12 col-md-12">
        <div class="table-responsive">
            <table class="table table-striped table-bordered table-hover table-condensed">
                <thead>
                <tr>
                    <th>错码链接</th>
                    <th>错码原因</th>
                    <th>错码操作</th>
                </tr>
                </thead>
                <tbody id="eList">

                </tbody>
            </table>
        </div>
    </div>
</div>

<a style="display: block;text-align: center" role="btn" target="_blank"
   href="<c:url value="${ctx}/resources/mallimages/confirm.png"/>">下载自动提交码</a>

<div style="display: none" class="audio">
    <audio id="errorAudio" src="http://data3.huiyi8.com/2015/saraxuss/3/10/6.mp3">
        Your browser does not support the audio element.
    </audio>
</div>

<script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
<script src="https://cdn.bootcss.com/jquery.form/4.2.2/jquery.form.min.js"></script>
<script src="http://ajax.microsoft.com/ajax/jquery.templates/beta1/jquery.tmpl.min.js"></script>
<%--<script src="https://cdn.bootcss.com/xlsx/0.11.8/xlsx.core.min.js"></script>--%>
<script src="https://cdn.bootcss.com/Base64/1.0.1/base64.min.js"></script>
<script src="https://cdn.bootcss.com/FileSaver.js/2014-11-29/FileSaver.min.js"></script>
<script src="https://cdn.bootcss.com/TableExport/4.0.10/js/tableexport.min.js"></script>


<script>


    //错误提示
    function alertError(message) {
        //警报
        $("#errorAudio").get(0).play();
//        addErrorTdInput( $("#code").val(),message)
        var code = $("#code").val();
        var data = {
            "code": code,
            "message": message
        }
        $("#et").tmpl(data).appendTo($("#eList"));
        $("#code").val("");//复原
    }

    //验证码的正确性
    function validateUrl(url) {
        if (url.indexOf("code=") < 0 && url.indexOf("boxCode=") < 0) {
            alertError("码规则有误，请检查");
            return false;
        }
        return true;
    }

    //是否是大箱
    function isBox(code) {
        if (code.indexOf("boxCode") > -1) {
            return true;
        }
        return false;
    }

    //判断大小码是否有重复
    function validateRepeat(os, code) {
        var result = false;
        os.each(function () {
            if (os.find(".ctd").html() == code) {
                result = true;
            }
        })
        return result;
    }

    //是否是小箱
    function isCode(code) {
        if (code.indexOf("code") > -1) {
            return true;
        }
        return false;
    }

    //从防伪码链接中获取编号
    function getCodeFromCodeStr(code) {
        var sc = code.substr(code.lastIndexOf("=") + 1, code.length);
        return sc;
    }


    $(function () {


        $("#code").keypress(function (event) {
            //碰到回车
            var keynum = (event.keyCode ? event.keyCode : event.which);
            if (keynum != '13') {
                return;
            }
            //每箱数量
            var perBox = $("#perBox").val();
            //获取链接
            var code = $(this).val();
            if (code == "confirm") {//自动提交
                confirmCodes();
                //TODO
                return;
            }
            //验证防伪码
            if (!validateUrl(code)) {
                return;
            }


            //处理code
            var c = getCodeFromCodeStr(code);//获取编号
            //模板数据装填
            var data = {
                "c": c,
                "code": code
            }
            var btdSize = $(".btr").length;//获取大箱td当前的数量
            var ctdSize = $(".ctr").length;//获取小箱td当前的数量
            if (btdSize == null) btdSize = 0;
            if (ctdSize == null) btdSize = 0;
            //处理大箱
            if (isBox(code)) {
                //如果已经有了必须要有对应的小箱数量才能录入
                if (btdSize != 0) {
                    if (btdSize * perBox != ctdSize) {
                        alertError("请录入小码!");
                        return;
                    }
                }
                //判断是否有重复
                if (validateRepeat($(".btr"), c)) {
                    alertError("重复录入大码!");
                    return;
                }

                $("#bList").append($("#bt").tmpl(data));
            } else if (isCode(code)) { //处理小箱子
                if (btdSize == 0 || btdSize * perBox == ctdSize) {
                    alertError("请录入大码!");
                    return;
                }
                //判断是否有重复
                if (validateRepeat($(".ctr"), c)) {
                    alertError("重复录入小码!");
                    return;
                }
                $("#cList").append($("#ct").tmpl(data));
            }

            $("#code").val("");
        });

        $(".table").delegate(".ajaxLink", "click", function () {
            $(this).closest("tr").remove();
        })

        //离线下载
        $("#download").click(function () {
            TableExport.prototype.charset = "charset=utf-8";
            TableExport(document.getElementsByTagName("table"), {
                headers: true,                              // (Boolean), display table headers (th or td elements) in the <thead>, (default: true)
                footers: true,                              // (Boolean), display table footers (th or td elements) in the <tfoot>, (default: false)
                formats: ['txt'],             // (String[]), filetype(s) for the export, (default: ['xls', 'csv', 'txt'])
                filename: 'id',                             // (id, String), filename for the downloaded file, (default: 'id')
                bootstrap: true,                           // (Boolean), style buttons using bootstrap, (default: true)
                exportButtons: true,                        // (Boolean), automatically generate the built-in export buttons for each of the specified formats (default: true)
                position: 'bottom',                         // (top, bottom), position of the caption element relative to table, (default: 'bottom')
                ignoreRows: null,                           // (Number, Number[]), row indices to exclude from the exported file(s) (default: null)
                ignoreCols: [1, 2],                           // (Number, Number[]), column indices to exclude from the exported file(s) (default: null)
                trimWhitespace: true                        // (Boolean), remove all leading/trailing newlines, spaces, and tabs from cell text in the exported file(s) (default: false)
            });
        })


        //提交在线表单
        var confirmCodes = function () {

            var boxs = [];

            var codes = [];

            $(".ctd").each(function () {
                codes.push($(this).html());
            })

            $(".btd").each(function () {
                boxs.push($(this).html());
            })

            console.log("--"+codes);
            var params = {
                "boxs": boxs,
                "codes": codes,
                "perBox":$("#perBox").val()
            }
            params = $.param(params, true);//切记这里 不然数组传不过去
            var url = "<c:url value="/securityCode/dmz/code/bind.json"/>";

            $.post(url,params,function (m) {
                if(m.flag==0){//录入成功
                    $("body").html("录入成功！三秒后返回！");
                    //三秒后刷新
                    setTimeout(function () {
                        window.location.reload();
                    },3000);
                }else {
                    alert("绑定失败"+m.message);
                }
            }).fail(function (xhr) {
                alert("未知错误！请联系管理员");
            });
            
        }

        //手动提交
        $("#cbtn").click(function () {
            confirmCodes();
        })
        //离线提交
        $("#off").click(function () {
            $("form").ajaxSubmit({
                success: function(data) { // data 保存提交后返回的数据，一般为 json 数据
                    // 此处可对 data 作相关处理
                    if(data.flag==0){
                        alert('绑定成功！');
                        window.location.reload();
                    }else {
                        alert('绑定失败!'+data.message);
                    }
                }
            });
        })

    })


</script>

<!--大箱td模板-->
<script id="bt" type="text/x-jquery-tmpl">
    <tr class="btr">
    <td class="btd">{{= c}}</td>
    <td>{{= code}}</td>
    <td><a class="ajaxLink btn btn-danger default" data-role="delete" ><span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除</a>
    </tr>




</script>

<!--小箱td模板-->
<script id="ct" type="text/x-jquery-tmpl">
  <tr class="ctr">
    <td class="ctd">{{= c}}</td>
    <td>{{= code}}</td>
    <td><a class="ajaxLink btn btn-danger default" data-role="delete" ><span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除</a>
    </tr>




</script>

<!--错误模板-->
<script id="et" type="text/x-jquery-tmpl">
  <tr class="etr">
    <td>{{= code}}</td>
    <td>{{= message}}</td>
    <td><a class="ajaxLink btn btn-danger default" data-role="delete" ><span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除</a>
    </tr>




</script>


</body>
</html>
