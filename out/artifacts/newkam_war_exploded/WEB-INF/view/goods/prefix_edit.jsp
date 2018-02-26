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
	<form action="<c:url value='/securityCode/prefix/edit.json'/>"
		class="form-horizontal" name="editForm" id="editForm" method="post">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title text-primary" id="myModalLabel">码前缀编辑</h4>
			</div>
			<div class="modal-body">
				<div class="container-fluid">
					<div class="row">
						<div class="col-md-12 col-xs-12">

							<input type="hidden" name="id" value="${codePrefix.id}">


							<div class="form-group">
								<label for="name" class="col-sm-2 control-label">前缀名称</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" id="editName"
										name="name" value="${codePrefix.name}"
										placeholder="前缀名称">
								</div>
							</div>


							<div class="form-group">
								<label for="name" class="col-sm-2 control-label">前缀链接</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" id="editName"
										   name="url" value="${codePrefix.url}"
										   placeholder="链接">
								</div>
							</div>


							<div class="form-group">
								<label for="name" class="col-sm-2 control-label">小码参数</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" id="editName"
										   name="cp" value="${codePrefix.cp}"
										   placeholder="小码参数">
								</div>
							</div>


							<div class="form-group">
								<label for="name" class="col-sm-2 control-label">大码参数</label>
								<div class="col-sm-10">
									<input type="text" class="form-control" id="editName"
										   name="bp" value="${codePrefix.bp}"
										   placeholder="大码参数">
								</div>
							</div>



						</div>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<div class="form-group">
					<div class="col-md-6 col-xs-12">
						<button type="button" class="btn btn-default btn-block quitBtn"
							id="quitBtn" data-dismiss="modal" name="quitBtn" value="login"
							tabindex="4" data-loading-text="正在返回......">
							<span class="glyphicon glyphicon-close">&nbsp;</span>关闭
						</button>
					</div>
					<div class="col-md-6 col-xs-12">
						<button type="submit" class="btn btn-success btn-block"
							form="editForm" id="saveBtn" name="saveBtn" value="saveBtn"
							tabindex="4" data-loading-text="正在提交......">
							<span class="glyphicon glyphicon-save">&nbsp;</span>保存
						</button>
					</div>
				</div>
			</div>
		</div>
	</form>
</div>


<jsp:include page="/WEB-INF/view/common/form.jsp"></jsp:include>
<script type="text/javascript">
	$(function() {
		init();
	});
	function init() {
		var btn = null;
		$("#editForm").validate(
				{
					submitHandler : function(form) {
						$(form).ajaxSubmit(
								{
									beforeSubmit : function() {
										btn.button("loading");
									},
									success : function(responseText,
											statusText, xhr, $form) {
										var m = $.parseJSON(xhr.responseText);
										btn.button("reset");
										if (m.flag == "0") {
											alert("保存成功");
											$(".quitBtn").click();
											$("#search").click();
										} else {
											alert("保存失败");
										}

									},
									error : function(xhr, textStatus,
											errorThrown) {
										var m = $.parseJSON(xhr.responseText);
										$("#alert").empty().html(m.message)
												.removeClass("invisible");
										btn.button("reset");
									}
								});
					},
					rules : {
						"entity.name" : {
							required : true
						}
					},
					onkeyup : false,
					messages : {
						"entity.name" : {
							required : "前缀名称"
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
		$("#saveBtn").click(function() {
			btn = $(this).button();
		});
	}
</script>
