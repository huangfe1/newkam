<%@ page import="com.dreamer.util.TokenInfo" %>
<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/view/common/common.jsp"%>
<style>
<!--
-->
.none {
	display: none;
}
</style>
<div class="modal-dialog modal-lg" role="document">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal"
				aria-label="Close">
				<span aria-hidden="true">&times;</span>
			</button>
			<h4 class="modal-title text-primary" id="myModalLabel">产品信息编辑</h4>
		</div>
		<div class="modal-body">
			<div class="container-fluid">
				<form action="<c:url value='/goods/edit.json'/>" name="editForm" enctype="multipart/form-data"
							class="form-horizontal"  id="editForm" method="post">
				<div class="row">
					<div class="col-md-12 col-xs-12">
					<div class="panel panel-primary">
							<div class="panel-heading">
								基本信息
							</div>
							<div class="panel-body">

							<input type="hidden" name="id" value="${parameter.entity.id}">
							<div class="form-group">
								<label for="name" class="col-sm-2 control-label">产品名称</label>
								<div class="col-sm-3">
									<input type="text" class="form-control" id="editName" tabIndex="10"
										name="name" value="${parameter.entity.name}"
										placeholder="输入产品名称">
								</div>
								<div class="col-md-4 col-xs-4 text-error"></div>
							</div>

							<!-- 产品类型 -->
						<div class="form-group">
								<label for="spec" class="col-sm-2 control-label">产品类型</label>
								<div class="col-sm-6">
								<c:forEach items="${types}" var="type">
									 <label class="checkbox-inline">
    			  						<input required  type="radio" name="goodsType" id="goodsType" value="${type}"  ${parameter.entity.goodsType eq  type ? 'checked' : ''} >
        					 				 ${type.desc}
        							 </label>
   								</c:forEach>
								</div>
								<div class="col-md-4 col-xs-4 text-error"></div>
							</div>


								<!-- 产品分类 -->
								<div class="form-group" >
									<label for="spec" class="col-sm-2 control-label">产品分类</label>
									<div class="col-sm-6">
										<select name="cId" id="category"  class="form-control">
											<option >
												选择分类
											</option>
											<c:forEach items="${categorys}" var="type">
												<option value="${type.id}" <c:if test="${type.id eq parameter.entity.category.id}">selected</c:if>>
														${type.name}
												</option>
											</c:forEach>
										</select>
									</div>
									<div class="col-md-4 col-xs-4 text-error"></div>
								</div>


							<div class="form-group">
								<label for="spec" class="col-sm-2 control-label">产品规格</label>
								<div class="col-sm-3">
									<input type="text" class="form-control" id="editSpec" tabIndex="11"
										name="spec" value="${parameter.entity.spec}"
										placeholder="输入产品规格">
								</div>
								<div class="col-md-4 col-xs-4 text-error"></div>
							</div>
								<!--产品重量-->
								<div class="form-group">
									<label for="spec" class="col-sm-2 control-label">产品重量</label>
									<div class="col-sm-3">
										<input type="text" class="form-control" id="editWeight" tabIndex="11"
											   name="weight" value="${parameter.entity.weight}"
											   placeholder="输入产品重量Kg">
									</div>
									<div class="col-md-4 col-xs-4 text-error"></div>
								</div>

								<!--每件数量-->
								<div class="form-group">
									<label for="boxamount" class="col-sm-2 control-label">每件数量</label>
									<div class="col-sm-3">
										<input type="text" class="form-control" id="boxamount" tabIndex="11"
											   name="boxamount" value="${parameter.entity.boxamount}"
											   placeholder="输入产品每件数量">
									</div>
									<div class="col-md-4 col-xs-4 text-error"></div>
								</div>

							<div class="form-group">
								<label for="img" class="col-sm-2 control-label">产品图片</label>
								<div class="col-sm-4">
									<input type="file" class="form-control" id="img" tabIndex="12"
										name="img" accept="image/png,image/jpeg,image/gif" placeholder="产品图片">
									<span class="help-block">图片尺寸:200*200px 体积小于50kb</span>
								</div>
								<div class="col-md-4 col-xs-4 text-error"></div>
							</div>


                                <!--活动图片-->
                                <div class="form-group">
                                    <label for="actImgFile" class="col-sm-2 control-label">活动图片</label>
                                    <div class="col-sm-4">
                                        <input type="file" class="form-control" id="img" tabIndex="12"
                                               name="actImgFile" accept="image/png,image/jpeg,image/gif" placeholder="活动图片">
                                        <span class="help-block">图片尺寸:604 × 289px 体积小于50kb</span>
                                    </div>
                                    <div class="col-md-4 col-xs-4 text-error"></div>
                                </div>

								<!--产品详情页-->
								<div class="form-group">
									<label for="detailImg" class="col-sm-2 control-label">产品详情</label>
									<div class="col-sm-4">
										<div id="demo">
											<c:set value="${fn:split(parameter.entity.detailImg,'+')}" var="imgs" />
											<c:forEach items="${imgs}" var="val" varStatus="i" >
												<c:if test="${val!=''}">
													<div id="uploadedList_${i.index}" class="upload_append_list" >
														<div class="file_bar">
															<div style="padding:5px;"><p class="file_name">${val}</p><span class="ufile_del" data-index="${i.index}" title="删除"></span>
															</div>
														</div>
														<a style="height:100px;width:120px;" href="#" class="imgBox">
															<div class="uploadImg" style="width:105px"><img id="uploadImage_0" class="upload_image" src="${dmzImgPath}${val}"
																											style="width:expression(this.width > 105 ? 105px : this.width)">
															</div>
														</a>
														<p id="uploadedProgress_0" class="file_progress"></p>
														<p id="uploadedFailure_0" class="file_failure">上传失败，请重试</p>
														<p id="uploadedSuccess_0" class="file_success"></p>
													</div>
												</c:if>

											</c:forEach>
										</div>
										<span class="help-block">图片尺寸:200*200px 体积小于50kb</span>
									</div>
									<div class="col-md-4 col-xs-4 text-error"></div>
								</div>

								<div class="form-group">
									<label for="pointFactor" class="col-sm-2 control-label">零售价</label>
									<div class="col-sm-4">
										<input type="number" class="form-control" id="editRetailPrice" tabIndex="13" required
											   name="retailPrice" value="${parameter.entity.retailPrice}"
											   placeholder="输入产品零售价">
									</div>
									<div class="col-md-4 col-xs-4 text-error"></div>
								</div>
								<div class="form-group" style="display: none">
									<label for="pointFactor" class="col-sm-2 control-label">积分系数</label>
									<div class="col-sm-4">
										<input type="number" class="form-control" id="editPointFactor" tabIndex="13"
											   name="pointFactor" value="1"
											   placeholder="输入产品积分系数">
									</div>
									<div class="col-md-4 col-xs-4 text-error"></div>
								</div>

								<!-- 返利模式 -->
								<div class="form-group">
									<label for="pointFactor" class="col-sm-2 control-label">模式</label>
									<div class="col-sm-4">
										<input type="text" class="form-control" id="editPointFactor" tabIndex="13"
											   name="voucher" value="${parameter.entity.voucher}"
											   placeholder="如21_22_23 一代返21 二代返22 三代返回23">
									</div>
									<div class="col-md-4 col-xs-4 text-error"></div>
								</div>
								<%--<div class="form-group">--%>
								<%--<label for="currentStock" class="col-sm-2 control-label">当前库存</label>--%>
								<%--<div class="col-sm-4">--%>
								<%--<input type="number" class="form-control" id="editCurrentStock" tabIndex="14" readonly="readonly"--%>
								<%--name="currentStock" value="${parameter.entity.currentStock}"--%>
								<%--placeholder="输入产品库存">--%>
								<%--</div>--%>
								<%--<div class="col-md-4 col-xs-4 text-error"></div>--%>
								<%--</div>--%>
								<%--<div class="form-group">--%>
								<%--<label for="currentPoint" class="col-sm-2 control-label">当前积分</label>--%>
								<%--<div class="col-sm-4">--%>
								<%--<input type="number" class="form-control" id="editCurrentPoint" tabIndex="15" readonly="readonly"--%>
								<%--name="currentPoint" value='<fmt:formatNumber value="${parameter.entity.currentPoint}" groupingUsed="false"/>'--%>
								<%--placeholder="输入产品当前总积分">--%>
								<%--</div>--%>
								<%--<div class="col-md-4 col-xs-4 text-error"></div>--%>
								<%--</div>--%>
								<%--<div class="form-group">--%>
								<%--<label for="currentBalance" class="col-sm-2 control-label">当前余额</label>--%>
								<%--<div class="col-sm-4">--%>
								<%--<input type="number" class="form-control" id="editCurrentBalance" tabIndex="16" readonly="readonly"--%>
								<%--name="currentBalance" value='<fmt:formatNumber value="${parameter.entity.currentBalance}" groupingUsed="false"/>'--%>
								<%--placeholder="输入产品当前余额">--%>
								<%--</div>--%>
								<%--<div class="col-md-4 col-xs-4 text-error"></div>--%>
								<%--</div>--%>
								<div class="form-group">
									<div class="col-sm-offset-2 col-sm-6">
										<div class="checkbox">
											<label> <input type="checkbox" id="benchmark"
											<c:if test="${parameter.entity.benchmark == true }">
														   checked="checked"
											</c:if>
														   name="benchmarkCheckBox">是主打产品
												<input type="hidden" name="benchmark"
													   value="${parameter.entity.benchmark ? 1 : 0}">
											</label>
										</div>
									</div>
									<div class="col-md-4 col-xs-4 text-error"></div>
								</div>

								<!--是否上架-->
								<div class="form-group">
									<label for="shelf" class="col-sm-2 control-label">是否上架</label>
									<div class=" col-sm-6">
										<div class="checkbox">
											<label> <input type="checkbox" id="shelf"
											<c:if test="${parameter.entity.shelf == true }">
														   checked="checked"
											</c:if>
														   name="shelfCheckBox">上架
												<input type="hidden" name="shelf"
													   value="${parameter.entity.shelf ? 1 : 0}">
											</label>
										</div>
									</div>
									<div class="col-md-4 col-xs-4 text-error"></div>
								</div>


								<!--是否可以发货-->
								<div class="form-group">
									<label for="shelf" class="col-sm-2 control-label">是否发货</label>
									<div class=" col-sm-6">
										<div class="checkbox">
											<label> <input type="checkbox" id="canDelivery"
											<c:if test="${parameter.entity.canDelivery == true }">
														   checked="checked"
											</c:if>
														   name="deliveryCheckBox">可以发货
												<input type="hidden" name="canDelivery"
													   value="${parameter.entity.canDelivery ? 1 : 0}">
											</label>
										</div>
									</div>
									<div class="col-md-4 col-xs-4 text-error"></div>
								</div>



								<!--是否是搞活动-->
								<div class="form-group">
									<label for="startTime" class="col-sm-2 control-label">是否搞活动</label>
									<div class=" col-sm-6">
										<div class="checkbox">
											<label> <input type="checkbox" id="activity"
											<c:if test="${parameter.entity.activity == true }">
														   checked="checked"
											</c:if>
														   name="activityCheckBox">活动
												<input type="hidden" name="activity"
													   value="${parameter.entity.activity ? 1 : 0}">
											</label>
										</div>
									</div>
									<div class="col-md-4 col-xs-4 text-error"></div>
								</div>

								<%--<!--活动开始时间-->--%>

								<div class="form-group">
									<label for="startTime" class="col-sm-2 control-label">活动时间</label>
									<div class="input-daterange input-group" id="datepicker">
										<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
										<input type="text" class="form-control pointer" value="${parameter.entity.startTime }" id="startDate" data-date-format="yyyy-mm-dd" name="startTime" placeholder="开始日期" />
										<span class="input-group-addon">到</span>
										<input type="text" class="form-control pointer" value="${parameter.entity.endTime}" name="endTime" placeholder="截止日期" />
										<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
									</div>
									<div class="col-md-4 col-xs-4 text-error"></div>
								</div>




								<div class="form-group">
									<label for="pointFactor" class="col-sm-2 control-label">零售价</label>
									<div class="col-sm-4">
										<input type="number" class="form-control" id="editRetailPrice" tabIndex="13" required
											   name="retailPrice" value="${parameter.entity.retailPrice}"
											   placeholder="输入产品零售价">
									</div>
									<div class="col-md-4 col-xs-4 text-error"></div>
								</div>

								<div class="form-group">
									<label for="editOrder" class="col-sm-2 control-label">排列顺序</label>
									<div class="col-sm-4">
										<input type="number" class="form-control" id="editOrder"
											   name="order" value="${parameter.entity.order}" tabIndex="17"
											   placeholder="输入显示顺序">
									</div>
									<div class="col-md-4 col-xs-4 text-error"></div>
								</div>
								<!-- </form>-->
							</div>
					</div>
					</div>
					<div class="col-md-12 col-xs-12">
						<div class="panel panel-primary">
							<div class="panel-heading">
								产品授权信息
							</div>
							<input type="hidden" name="authorizationType.id" value="${parameter.entity.authorizationType.id}">
							<div class="panel-body">
								<div class="form-group">
									<label for="authName" class="col-sm-2 control-label">授权类型</label>
									<div class="col-sm-4">
										<input type="text" class="form-control" id="editAuthName"
											   name="authorizationType.name" value="${parameter.entity.authorizationType.name}" tabIndex="17"
											   placeholder="授权类型名称">
									</div>
									<div class="col-md-4 col-xs-4 text-error"></div>
								</div>
								<div class="form-group">
									<label for="editOrder" class="col-sm-2 control-label">授权说明</label>
									<div class="col-sm-4">
									<textarea class="form-control" name="authorizationType.remark"
											  rows="3" tabIndex="17" placeholder="请输入授权类型的说明">${parameter.entity.authorizationType.remark}</textarea>
									</div>
									<div class="col-md-4 col-xs-4 text-error"></div>
								</div>
								<div class="form-group">
									<label for="editOrder" class="col-sm-2 control-label">授权排列顺序</label>
									<div class="col-sm-4">
										<input type="number" class="form-control" id="editOrder"
											   name="authorizationType.order" value="${parameter.entity.authorizationType.order}" tabIndex="19"
											   placeholder="输入显示顺序">
									</div>
									<div class="col-md-4 col-xs-4 text-error"></div>
								</div>
							</div>
						</div>
					</div>
					<div class="col-md-12 col-xs-12">
						<div class="panel panel-primary">
							<div class="panel-heading">
								价格设置
							</div>
							<div class="table-responsive">
								<table id="levelTable" class="table table-condensed">
									<thead>
									<tr>
										<th>等级名称</th>
										<th>单价</th>
										<th>等级阈值</th>
									</tr>
									</thead>
									<tbody>
									<c:forEach var="l" items="${levels}" varStatus="s">

										<c:set var="levelP" value="${prices[l.id]}" />
										<tr class="lc" tag="${l.goodsType}">
											<td>${l.name}
												<input  type="hidden"   name="priceId" value="${levelP.id}">
												<input  type="hidden" 	name="levelId"  value="${l.id}">

											</td>
											<td><input   type="number" name="levelPrice" tabindex="${s.count+18}"  value="${levelP.price}" ></td>
											<td><input   type="number" name="levelThreshold" tabindex="${s.count+19}" value="${levelP.threshold}"></td>
										</tr>
									</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
					</div>

					<div class="col-md-12 col-xs-12">
						<div class="panel panel-primary">
							<div class="panel-heading">
								国外市场
							</div>
							<div class="table-responsive">
								<table id="countryTable" class="table table-condensed">
									<thead>
									<tr>
										<th>公司名称</th>
										<th>产品差价</th>
										<th>产品利润</th>
										<th>产品图片</th>
										<th>0关闭1开启</th>
									</tr>
									</thead>
									<tbody>
									<c:forEach var="c" items="${countries}" varStatus="s">

										<c:set var="cP" value="${cps[c.id]}" />
										<tr class="lc">
											<td>${c.name}
												<input  type="hidden"   name="cpids" value="${cP.id}">
												<input  type="hidden" 	name="cids"  value="${c.id}">
											</td>
											<td><input   type="number" name="cps" tabindex="${s.count+19}"  value="${cP.price}" ></td>
											<td><input   type="number" name="profits" tabindex="${s.count+20}" value="${cP.profit}"></td>
											<td><input   type="file" accept="image/png,image/jpeg,image/gif"  name="cfiles" tabindex="${s.count+21}" ></td>
											<td><input  tabindex="${s.count+21}" type="number" name="opens" value="${cP.open}" ></td>
										</tr>
									</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
					</div>

				</div>
				</form>
			</div>
		</div>
		<div class="modal-footer">
			<div class="form-group">
				<div class="col-md-6 col-xs-12">
					<button type="button" class="btn btn-default btn-block quitBtn" tabIndex="26"
							id="quitBtn" data-dismiss="modal" name="quitBtn" value="login"
							tabindex="4" data-loading-text="正在返回......">
						<span class="glyphicon glyphicon-remove-sign">&nbsp;</span>关闭
					</button>
				</div>
				<div class="col-md-6 col-xs-12">
					<button type="button" class="btn btn-success btn-block" form="editForm" tabIndex="27"
							id="saveBtn" name="saveBtn" value="saveBtn" tabindex="4"
							data-loading-text="正在提交......">
						<span class="glyphicon glyphicon-save">&nbsp;</span>保存
					</button>
				</div>
			</div>
		</div>
	</div>
</div>


<jsp:include page="/WEB-INF/view/common/form.jsp"></jsp:include>
<%@include file="/WEB-INF/view/common/script_hf_upload.jsp"%>
<script type="text/javascript">
	$(function() {
        $(".input-daterange").datepicker({
            autoclose : true,
            language : "zh-CN",
            todayHighlight : true,
            todayBtn : "linked",
            format:"yyyy-mm-dd",
            startDate:new Date()
        });
		init();
		initRmLevel();
		rmLevel();
		$("#detailImg").click(function(){
			$("#imgModal").modal({
				backdrop: "static"
			});
		});
	});

	function rmLevel(){
		$(":radio").click(function(){
			var type=$(this).val();
			hideInput(type);
		});
	}

	function initRmLevel(){
		var type=$("input:radio:checked").val();
		if(type!=undefined){
			hideInput(type);
		}
	}

	function hideInput(type){
		$("#levelTable tr.lc").each(function (){
			if($(this).attr("tag")!=type){
				$(this).find("input").attr("disabled","disabled");
				$(this).css("display","none");
			}else{
				$(this).find("input").removeAttr("disabled");
				$(this).css("display","");
			}
		});
	}


	function init() {
		$("#editName").focus().select();
		var btn = null;
		$("#editForm").validate({submitHandler : function(form) {
			$(form).ajaxSubmit({
				beforeSubmit : function(arr, $form, options) {
					btn.button("loading");
				},
				success : function(responseText,
								   statusText, xhr, $form) {
					var m = $.parseJSON(xhr.responseText);
					btn.button("reset");
					if(m.flag=="0"){
						alert("保存成功");
						$(".quitBtn").click();
//						$("#search").click();
						location.reload();
					}else{
						alert("保存失败");
					}

				},
				error : function(xhr, textStatus,
								 errorThrown) {
					var m = $.parseJSON(xhr.responseText);
					btn.button("reset");
					alert("保存失败");
				}
			});
		},
			rules : {
				name : {
					required : true
				},
				levelPrice:{
					required: true,
					number: true
				},
				currentStock:{
					digits:true,
					min:0
				},
				retailPrice:{
					required:true,
					number:true,
					min:0
				},
				currentBalance:{
					digits:true
				}
			},
			onkeyup : false,
			messages : {
				name : {
					required : "产品名称必须填写"
				},
				levelPrice:{
					required:"价格必须填写",
					number:"价格必须为数字"
				},
				currentStock:{
					digits:"当前库存余额必须为整数",
					min:"库存不能为负数"
				},
				currentBalance:{
					digits:"当前帐户余额必须为整数"
				},
				retailPrice:{
					required:"填写零售价",
					number:"零售价必须为有效数字",
					min:"零售价不能小于0"
				}
			},
			focusInvalid : true,
			errorClass : "text-danger",
			validClass : "valid",
			errorElement : "small",
			errorPlacement : function(error, element) {
				error.appendTo(element.closest("div.form-group")
						.children("div.text-error"));
			}
		});



		$("#editForm").find("input[type='checkbox']").change(function(e){
			var $t=$(this);
			var next=$t.next("input[type='hidden']");
			$t.prop("checked")?next.val(1):next.val(0);
		});



		$("#saveBtn").click(function(e) {
			btn = $(this).button();
			//$(document.forms["editForm"]).submit();
			ZYFILE.submit();
			/* $('#uploadModal').modal({
			 backdrop : "static"
			 });

			 setInterval("getProgress()",1000); */
		});


	}

</script>
