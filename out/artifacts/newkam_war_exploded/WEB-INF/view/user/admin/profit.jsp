<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/view/common/common.jsp" %>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title>计算返利</title>
    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.bootcss.com/bootstrap-datepicker/1.7.1/css/bootstrap-datepicker3.min.css" rel="stylesheet">
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
        <div class="form-inline navbar-form navbar-left">

            <form class="form-group" action="<c:url value="/admin/calulate/profit.json"/>">
                <div style="width: 60%" class="input-daterange input-group" id="datepicker"><span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                    <input type="text" class="form-control pointer" value="${startTime}" id="startDate" data-date-format="yyyy-mm-dd" name="startTime" placeholder="开始日期"/>
                    <span class="input-group-addon">到</span> <input type="text" class="form-control pointer" value="${endTime}" name="endTime" placeholder="截止日期"/>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>

                </div>
                <input type="text" placeholder="股东编号" name="agentCode" value="${agentCode}" class="form-control" id="agentCode">
                <button type="submit" class="btn btn-primary" id="search" name="search">
                    <span class="glyphicon glyphicon-search searchBtn"></span>&nbsp;下载
                </button>
            </form>




        </div>

    </div>


</nav>







<script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
<script src="https://cdn.bootcss.com/jquery.form/4.2.2/jquery.form.min.js"></script>
<script src="http://ajax.microsoft.com/ajax/jquery.templates/beta1/jquery.tmpl.min.js"></script>
<%--<script src="https://cdn.bootcss.com/xlsx/0.11.8/xlsx.core.min.js"></script>--%>
<script src="https://cdn.bootcss.com/Base64/1.0.1/base64.min.js"></script>
<script src="https://cdn.bootcss.com/FileSaver.js/2014-11-29/FileSaver.min.js"></script>
<script src="https://cdn.bootcss.com/TableExport/4.0.10/js/tableexport.min.js"></script>
<script src="https://cdn.bootcss.com/bootstrap-datepicker/1.7.1/js/bootstrap-datepicker.min.js"></script>
<script src="https://cdn.bootcss.com/bootstrap-datepicker/1.7.1/locales/bootstrap-datepicker.zh-CN.min.js"></script>

<script>

    $(function () {
        $("#datepicker.input-daterange").datepicker({
            autoclose: true,
            language: "zh-CN",
            todayHighlight: true,
            todayBtn: "linked",
            format: "yyyy-mm-dd",
            endDate: new Date()
        });
    })


</script>






</body>
</html>
