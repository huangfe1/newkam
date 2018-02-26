<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/view/common/common.jsp" %>
<html>
<head>
    <title>生成码段</title>
    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
    <style>
        th {
            text-align: center;
        }
        td {
            word-break: keep-all;
            text-align: center;
        }
    </style>
</head>

<body>

<nav class="navbar navbar-default ">
    <div class="container-fluid">
        <form class="form-inline navbar-form navbar-left" action="<c:url value="/securityCode/code/create.json"/>">
            <div class="form-group">
                <label for="prefix">选择前缀</label>
                <select name="pid" id="prefix" class="form-control">
                   <c:forEach items="${prefixes}" var="prefix">
                       <option value="${prefix.id}">${prefix.name}</option>
                   </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="codeNumber">小码数量</label>
                <input class="form-control" type="number"  size="5" style="text-align: center" id="codeNumber" name="codeNumber" value="0">
            </div>

            <div class="form-group">
                <label for="boxNumber">大码数量</label>
                <input class="form-control" id="boxNumber" name="boxNumber" value="0">
            </div>

            <div class="form-group">
                <label for="info">备注</label>
                <input class="form-control" id="info" name="info">
            </div>

            <button class="btn btn-danger">生成码段</button>
        </form>



        <form class="form-inline navbar-form navbar-right" action="/securityCode/code/create.html">

            <div class="form-group">
                <label for="code">码</label>
                <input class="form-control" id="code" name="code" value="${code}">
            </div>

            <div class="form-group">
                <label>备注</label>
                <input class="form-control" name="info" value="${parameter.entity.info}">
            </div>
            <button class="btn btn-warning">查询</button>
        </form>


    </div>
</nav>








<div class="row">
    <div class="col-lg-12 col-md-12">
        <div class="table-responsive">
            <table class="table table-striped table-bordered table-hover table-condensed">
                <thead>
                <tr>
                    <th>前缀</th>
                    <th>小码</th>
                    <th>数量</th>
                    <th>大码</th>
                    <th>数量</th>
                    <th>备注</th>
                    <th>时间</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody id="dataList">
                <c:forEach items="${codeSegments}" var="cs">
                    <tr data-row="${cs.id}" >
                        <td>${cs.prefix}</td>
                        <td>${cs.startCode}-${cs.endCode}</td>
                        <td>${cs.codeNumber}</td>
                        <td>${cs.startBox}-${cs.endBox}</td>
                        <td>${cs.boxNumber}</td>
                        <td>${cs.info}</td>
                        <td>${cs.date}</td>
                        <td><a class="ajaxLink btn btn-danger default" data-role="delete" href="<c:url value='/securityCode/code/remove.json?id=${cs.id}' /> "><span class="glyphicon glyphicon-remove" aria-hidden="true"></span>删除</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>
<div class="row">
    <div class="col-md-12 col-sm-12 col-xs-12">
        <jsp:include page="/WEB-INF/view/common/pager.jsp"></jsp:include>
    </div>
</div>
<script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
<script>

    $("#dataList a.ajaxLink").click(function(e){
        e.preventDefault();
        var method="POST";
        if($(this).attr("data-role")){
            if(!window.confirm("删除将无法恢复数据,是否继续?")){
                return false;
            }
            method="DELETE";
        }
        var url=$(this).attr("href");
        $.ajax({url:url,method:method,complete:function(xhr,ts){
            if(xhr.status>=200 && xhr.status<300){
//                $("#winBody").empty().html("操作成功");
//                $("#winModal").modal({backdrop:"static",show:true});
//                $("#search").click();
                alert("操作成功！")
                window.location.reload();
            }else{
//                $("#winBody").empty().html("操作失败").addClass("text-danger");
//                $("#winModal").modal({backdrop:"static",show:true});
                alert("操作失败")
            }
        }});
    });

</script>


</body>
</html>
