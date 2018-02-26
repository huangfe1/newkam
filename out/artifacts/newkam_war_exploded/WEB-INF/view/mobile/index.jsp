<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/WEB-INF/view/common/common.jsp" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <title>咖盟特权商城</title>
    <%@include file="/WEB-INF/view/common/tc_css.jsp" %>
    <!-- Bootstrap -->
    <link href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
    <link href="http://ht.biofreda.com/button.css" rel="stylesheet">
    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>

    <script src="https://cdn.bootcss.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
</head>
<style>
    header p {
        text-align: center;
        font-size: 14px;
        color: #666666;
        margin: 0;
        font-weight: 200;
    }

    header h1 {
        text-align: center;
        font-size: 30px;
        font-weight: 200;
        margin: 30px 0 10px 0;
    }

    .demo_line_02 {
        margin: 8px 0 8px;
        height: 1px;
        border-top: 1px solid #ddd;
        text-align: center;
    }

    .demo_line_02 span {
        text-align: center;
        font-size: 14px;
        color: #666666;
        margin: 0;
        font-weight: 200;

        position: relative;
        top: -9px;
        background: #fff;
        padding: 0 20px;
    }

    .col{
        text-align: center;
    }

    #tagsList{
        margin-top: 1em;
        text-align: center;
        overflow: hidden;
        /*float: right;*/
    }

    #tagsList div{
        width: 33%;
        display: inline-block;
        float: left;
    }



    /*#tagsList {position:relative; width:96%; height:250px; margin: 0px auto 0;  }*/
    /*#tagsList a {position:absolute; top:0px; left:0px; font-family: Microsoft YaHei; color:#fff; font-weight:bold; text-decoration:none; padding: 3px 6px; }*/
    /*#tagsList a:hover { color:#FF0000; letter-spacing:2px;}*/
</style>
<body style="background-color:#fbfbfb;">
<div class="container">
    <header>
        <%--<h1>优惠商城</h1>--%>
        <%--<p>咖盟微商 做有态度的玻尿酸</p>--%>
        <br>
        <form action="<c:url value="/dmz/mobile/index.html"/> " method="get" id="searchform" name="searchform">
            <div class="input-group">
                <input type="text" class="form-control" value="${goodsName}" name="goodsName" placeholder="输入你要查找的产品...">
                <span class="input-group-btn">
        <button class="btn btn-default" type="submit">搜索</button>
      </span>
            </div>
        </form>
    </header>

    <div class="row category">
        <a href="<c:url value="/mobile/my.html"/> " class="col-xs-3">
            <img class="img-responsive" src="<c:url value='/resources/tcimg/icon_rm.png'/>">
            <h4>用户中心</h4>
        </a>
        <%--<a href="/m_search/list?param.commend='Y'" class="col-xs-3">--%>
        <a href="<c:url value='/dmz/pmall/show.html'/> " class="col-xs-3">
            <img class="img-responsive" src="<c:url value='/resources/tcimg/icon_tm.png'/>">
            <h4>优惠商城</h4>
        </a>


        <a href="#" id="tq" class="col-xs-3">
            <img class="img-responsive" src="<c:url value='/resources/tcimg/theme.png'/>">
            <h4>特权商城</h4>
        </a>
        <%--<a href="<c:url value='/dmz/pmall/view.html?entity.goodsType.id=17'/> " class="col-xs-3">--%>
            <%--<img class="img-responsive" src="<c:url value='/resources/tcimg/icon_pp.png'/> ">--%>
            <%--<h4>公益商城</h4>--%>
        <%--</a>--%>
        <a href="<c:url value='#'/> " class="col-xs-3">
            <img class="img-responsive" src="<c:url value='/resources/tcimg/icon_pp.png'/> ">
            <h4>公益商城</h4>
        </a>
    </div>


    <%--<div class="demo_line_02"><span>区域市场区</span></div>--%>


    <%--<div id="tagsList" class="countryButton">--%>

        <%--<div>--%>
            <%--<button  data-id="-1" type="button" style="margin:5px 0;width: 100px;line-height: 30px"--%>
                     <%--class="button yellow">内陆市场</button>--%>

        <%--</div>--%>

        <%--<c:forEach items="${countries}" var="country" varStatus="sta">--%>

            <%--<div>--%>
                <%--<button  data-id="${country.id}" type="button" style="margin:5px 0;width: 100px;line-height: 30px"--%>
                         <%--class="button yellow">${country.name}</button>--%>
            <%--</div>--%>
            <%--&lt;%&ndash;<c:if test="${sta.index%3==0}">&ndash;%&gt;--%>
            <%--&lt;%&ndash;<c:if test="${sta.index!=0}">&ndash;%&gt;--%>
            <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</c:if>&ndash;%&gt;--%>
            <%--&lt;%&ndash;<div class="row">&ndash;%&gt;--%>
            <%--&lt;%&ndash;</c:if>&ndash;%&gt;--%>
            <%--&lt;%&ndash;<div  class="col-xs-4 col" >&ndash;%&gt;--%>
            <%--&lt;%&ndash;<button  data-id="${type.id}" type="button" style="margin:5px 0;width: 100px;line-height: 30px"&ndash;%&gt;--%>
            <%--&lt;%&ndash;class="button blue">${type.name}</button>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
            <%--&lt;%&ndash;<c:if test="${sta.index+1==pTypes.size()}">&ndash;%&gt;--%>
            <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</c:if>&ndash;%&gt;--%>
        <%--</c:forEach>--%>
    <%--</div>--%>


    <div class="demo_line_02"><span>产品分类区</span></div>
    <%--<div class="row">--%>
        <%--<div class="col-xs-12">--%>
            <div id="tagsList" class="categoryButton">
                <c:forEach items="${categorys}" var="type" varStatus="sta">
                    <div>
                    <button  data-id="${type.id}" type="button" style="margin:5px 0;width: 100px;line-height: 30px"
                             class="button blue">${type.name}</button>
                    </div>
                <%--<c:if test="${sta.index%3==0}">--%>
                <%--<c:if test="${sta.index!=0}">--%>
            <%--</div>--%>
            <%--</c:if>--%>
            <%--<div class="row">--%>
                <%--</c:if>--%>
                <%--<div  class="col-xs-4 col" >--%>
                    <%--<button  data-id="${type.id}" type="button" style="margin:5px 0;width: 100px;line-height: 30px"--%>
                             <%--class="button blue">${type.name}</button>--%>
                <%--</div>--%>
                <%--<c:if test="${sta.index+1==pTypes.size()}">--%>
            <%--</div>--%>
            <%--</c:if>--%>
            </c:forEach>
        </div>
    <%--</div>--%>
<%--</div>--%>
<%--<div class="demo_line_02"><span>今日主推区</span></div>--%>
<div class="row products">

    <c:forEach items="${goodss}" var="g">
        <div class="col-xs-12 col-md-6 col">
            <a href="<c:url value='/dmz/mobile/${g.id}/detail.html'/>">
                <img class="img-responsive" src="${dmzImgPath}${g.imgFile}" alt="${g.name}">
            </a>
        </div>
    </c:forEach>

    <%--<div class="col-xs-12">--%>
        <%--<div class="demo_line_02"><span>产品分类区</span></div>--%>
    <%--</div>--%>


    <%--<div class="col-xs-12 col-md-6">--%>
    <%--<a href="#">--%>
    <%--<img class="img-responsive" src="http://ht.kam365.com/kam/dmz/img/goods/fc3590d29c1649bb9442914d7166fc27.jpg" alt="产批名">--%>
    <%--</a>--%>
    <%--</div>--%>

</div>
<%--<div id="ajax_loading" style="width:300px;margin: 10px  auto 15px;text-align:center;">--%>
    <%--<img src="http://www.zmz365.com:8081/loading.gif">--%>
<%--</div>--%>
<%--<jsp:include page="/WEB-INF/view/common/footer.jsp"/>--%>
</div>

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<%--<script type="text/javascript" src="http://www.zmz365.com:8081/data/tags.js"></script>--%>
<script type="text/javascript">

    $(function () {
//        $.ajaxSettings.async = false;
        $(".countryButton div>button").click(function (e) {
            e.preventDefault();
            e.stopPropagation();
            var url = "<c:url value='/dmz/mobile/index.html?countryId='/>" + $(this).attr("data-id");
            window.location.href = url;
        });

        $(".categoryButton div>button").click(function (e) {
            e.preventDefault();
            e.stopPropagation();
            var url = "<c:url value='/dmz/mobile/index.html?cid='/>" + $(this).attr("data-id");
            window.location.href = url;
        });

        $(".rk").click(function (e) {
            e.preventDefault();
            e.stopPropagation();
            window.location.href = $(this).attr("data-url");
        });

        $("#tq").click(function (e) {
            e.preventDefault();
            <c:if test="${user.agentCode==null}">
            alert("请先去用户中心完善信息！");
            location.href="<c:url value="/mobile/my.html"/>";
            </c:if>
            <c:if test="${user.agentCode!=null}">
            location.href="<c:url value="/dmz/mobile/index.html"/>";
            </c:if>

        })




    });


</script>

</body>
</html>