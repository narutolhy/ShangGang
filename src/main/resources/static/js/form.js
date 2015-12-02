/**
 * Created by qml_moon on 12/11/15.
 */
$(document).ready(function() {

	$('input').iCheck({
		checkboxClass: 'icheckbox_flat-green'
	});

});


function addUserHandler(thisform) {
	with (thisform) {
		var regexp = /^[a-zA-Z0-9]+$/;
		if(!regexp.test(userId.value.length) || userId.value.length < 5 || userId.value.length > 18) {
			alert("账号必须为5至18位字母和数字的组合！");
			userId.focus();
			return;
		}
		if(!regexp.test(password.value) || password.value.length < 5 || password.value.length > 18) {
			alert("密码必须为5至18位字母和数字的组合！");
			password.focus();
			return;
		}
		var ps = "";
		for (i = 0; i < privilege.length; i++) {
			if (privilege[i].checked) {
				ps += privilege[i].value + ",";
			}
		}
		if (ps.length > 0) {
			ps = ps.substr(0, ps.length - 1);
		}
		$.ajax({
			method: "POST",
			url: "/adduser",
			data: {userId: userId.value, password: SHA1(password.value),
				name: name.value, phone: phone.value, unit: unit.value, privilege: ps}
		}).fail(function() {
			alert( "未连接到服务器!" );
		}).done(function( data ) {
			if (data == 1) {
				alert('用户添加成功！');
			} else if (data == 0) {
				alert('用户名已被使用！');
			} else {
				alert('服务器异常！');
			}
		});
	}
}

function changePrivilegeHandler(thisform, userId, privilegeDialog, userDialog) {
	with (thisform) {
		var ps = "";
		for (i = 0; i < privilege.length; i++) {
			if (privilege[i].checked) {
				ps += privilege[i].value + ",";
			}
		}
		if (ps.length > 0) {
			ps = ps.substr(0, ps.length - 1);
		}

		$.ajax({
			method: "POST",
			url: "/changeprivilege",
			data: {userId: userId, privilege: ps}
		}).fail(function() {
			alert( "未连接到服务器!" );
		}).done(function( data ) {
			if (data.isSuccess == 1) {
				alert('用户权限修改成功！');
				var user = userDialog.getData("user");
				for (i = 0; i < user.length; i++) {
					if (user[i].userId == userId) {
						user[i].privilege = data.privilege;
					}
				}
				privilegeDialog.close();
				//showTableEntry(userDialog);

			} else {
				alert('服务器异常！');
			}
		});
	}
}

function deleteUserHandler(userId, dialog) {
	$.ajax({
		method: "POST",
		url: "/deleteuser",
		data: {userId: userId}
	}).fail(function() {
		alert( "未连接到服务器!" );
	}).done(function( data ) {
		if (data == 1) {
			alert('删除成功！');
			var data = dialog.getData("user");
			for (i = 0; i < data.length; i++) {
				if (data[i].userId == userId) {
					data.splice(i, 1);
				}
			}
			showTableEntry(dialog);
			showTablePage(dialog);
		} else if (data == 0) {
			alert('该用户名不存在！');
		} else {
			alert('服务器异常！');
		}
	});

}

function changeInfoHandler(thisform, data) {
	with (thisform) {
		var regexp = /^[a-zA-Z0-9]+$/;
		if (newPassword.value.length != 0 &&
			(!regexp.test(newPassword.value) || newPassword.value.length < 5 || newPassword.value.length > 18)) {
			alert("新密码必须为5至18位字母和数字的组合！");
			newPassword.focus();
			return;
		}
		$.ajax({
			method: "POST",
			url: "/changeinfo",
			data: {userId: data.userId, name: name.value, phone: phone.value, unit: unit.value,
					oldPassword: SHA1(password.value), newPassword: newPassword.value == "" ? "" : SHA1(newPassword.value)}
		}).fail(function () {
			alert("未连接到服务器!");
		}).done(function (data) {
			if (data == 1) {
				alert('信息修改成功！');
			} else if (data == 0) {
				alert('旧密码不正确！');
			} else {
				alert('服务器异常！');
			}
		});
	}
}

function uploadHandler(thisform, override) {
	with (thisform) {
		var fileName = file.value;
		if(fileName.match(/fakepath/)) {
			// update the file-path text using case-insensitive regex
			fileName = fileName.replace(/C:\\fakepath\\/i, '');
		}
		var regexp = /^\d\d-\d\d\.txt+$/;
		if (!regexp.test(fileName)) {
			alert("文件名格式不正确！");
			return;
		}
		var fd = new FormData(thisform);
		fd.append("name", fileName.substr(0, 5));
		fd.append("harborId", 1);
		fd.append("override", override);
		$('#loading-indicator').show();

		$.ajax({
			url: "/upload",
			type: "POST",
			data: fd,
			enctype: 'multipart/form-data',
			processData: false,  // tell jQuery not to process the data
			contentType: false   // tell jQuery not to set contentType
		}).fail(function() {
			$('#loading-indicator').hide();
			alert("未连接到服务器!");
		}).done(function(data) {
			$('#loading-indicator').hide();
			if (data == 0) {
				var r = confirm("该时间数据已在数据库中，是否覆盖？");
				if (r) {
					uploadHandler(thisform, true);
				}
			} else if (data == 1) {
				alert("数据上传成功！");
			} else if (data == -1) {
				alert("服务器异常, 上传失败！");
			} else {
				alert("数据格式不符合要求，请确保输入每行为3个数字并以空格分割！")
			}
		});
		return false;
	}
}

function downloadHandler(thisform) {
	var year = $('.year-form').text();
	var month = $('.month-form').text();
	var date = year.substr(2, 2) + "-" + month.substr(0, 2);
	thisform.date.value = date;
	thisform.submit();
}

function showUploadFileName() {
	$("input[id=lefile]").click();
	$('input[id=lefile]').change(function() {
		$('#photoCover').val($(this).val());
	});
}
function uploadDialog() {
	var waitingGIF = '<img src="/img/loading.gif" id="loading-indicator" style="display:none" />';

	var formHead =
		'<form id="uploadForm">';

	var formBody =
		'<div class="row"><div class="col-lg-8 col-lg-offset-2"><div class="input-group">' +
			'<input id="lefile" type="file" name="file" style="display:none">' +
			'<input id="photoCover" type="text" class="form-control" placeholder="文件名必须为YY-MM.txt,例如15-03.txt">' +
			'<span class="input-group-btn">' +
				'<button class="btn btn-default" type="button" onclick=showUploadFileName();>选择文件</button>' +
			'</span>' +
		'</div></div></div>';
	var formTail = '</form>';
	BootstrapDialog.show({
		title : '上传水深数据',
		message : formHead + formBody + formTail + waitingGIF,

		onshown: function(dialog){
			$('input').iCheck({
				checkboxClass: 'icheckbox_flat-green'
			});
		},
		buttons : [{
			label : '取消',
			action : function(dialog) {
				dialog.close();
			}
		}, {
			label : '上传',
			cssClass : 'btn-primary',
			action : function(dialog){
				uploadHandler($('#uploadForm')[0], false);
			}
		}]
	});
}

function downloadDialog() {

	var waitingGIF = '<img src="/img/loading.gif" id="loading-indicator" style="display:none" />';

	var formHead = '<form id="downloadForm" action="/download" method="post">';

	var formBody =
		'<div class="row"><div class="col-lg-4 col-lg-offset-4"><label class="pull-right">请选择对应的时间：</label></div></div>' +
		'<div class="row">' +
			'<div class="col-lg-3 col-lg-offset-3"><div class="input-group-btn">' +
				'<button type="button" class="btn btn-default dropdown-toggle year-form" name="year" data-toggle="dropdown" style="width:100%">选择年份<span class="caret"></span></button>' +
				'<ul class="dropdown-menu dropdown-year" style="min-width: 0;width:100%"></ul>' +
			'</div></div>' +
			'<div class="col-lg-3" style="left:0px"><div class="input-group-btn">' +
				'<button type="button" class="btn btn-default dropdown-toggle month-form disabled" name="month" data-toggle="dropdown" style="width:100%">选择月份<span class="caret"></span></button>' +
				'<ul class="dropdown-menu dropdown-month" style="min-width: 0;width:100%"></ul>' +
			'</div></div>' +
		'</div>';


	var dummyInput = '<input type="text" name="date" style="display:none"/>';
	var formTail = '</form>';
	BootstrapDialog.show({
		title : '下载水深数据',
		message : formBody + formHead + dummyInput + formTail + waitingGIF,

		onshown: function(dialog){
			$('#loading-indicator').show();
			dialog.getButton('btn-download').disable();

			$.ajax({
				url: "/getdate",
				type: "GET",
				data: {harborId: 1},
				contentType: false   // tell jQuery not to set contentType
			}).fail(function() {
				alert("未连接到服务器!");
				$('#loading-indicator').hide();

			}).done(function(date) {
				$('#loading-indicator').hide();

				console.log(date);
				var years = getYears(date);
				for (i = 0; i < years.length; i++) {
					$('.dropdown-year').append('<li><a href="#" style="text-align: center">' + years[i] +  '年</a></li>');
				}
				$('.dropdown-year li').click(function(e){
					e.preventDefault();
					var selected = $(this).text();
					$('.year-form').text(selected);
					$('.month-form').removeClass("disabled");
					$('.month-form').html('选择月份<span class="caret"></span>');
					dialog.getButton('btn-download').disable();
					var months = getMonths(date, selected);
					$('.dropdown-month').empty();
					for (i = 0; i < months.length; i++) {
						$('.dropdown-month').append('<li><a href="#" style="text-align: center">' + months[i] + '月</a></li>');
					}
					$('.dropdown-month li').click(function(e){
						e.preventDefault();
						var selected = $(this).text();
						$('.month-form').text(selected);
						dialog.getButton('btn-download').enable();
					});
				});
			})


		},
		buttons : [{
			label : '取消',
			action : function(dialog) {
				dialog.close();
			}
		}, {
			id : 'btn-download',
			label : '下载',
			cssClass : 'btn-primary',
			action : function(dialog){
				downloadHandler($('#downloadForm')[0]);
			}
		}]
	});
}

function login(thisform) {
	with(thisform) {
		password.value = SHA1(password.value);
		$.ajax({
			url: "/login",
			type: "POST",
			data: {userId: userId.value, password: password.value}
		}).fail(function() {
			alert("未连接到服务器!");
		}).done(function(data) {
			changeInfoDialog(data);
		});
	}
}

function removeDisabled(id) {
	console.log($('#changeInfoForm #'+ id).attr("disabled", false));
	$('#changeInfoForm #'+ id).focus();
	$('#changeInfoForm #'+ id).focusout(function() {$('#changeInfoForm #'+ id).attr("disabled", true)});

	if (id == "password") {
		$('#changeInfoForm #'+ id).val("");
		$('#changeInfoForm #'+ id).attr("placeholder", "请输入旧密码");
		$(".new-password").css("display", "block");
	}
}

function changeInfoDialog(data) {
	var formHead = '<form id="changeInfoForm" >';

	var name =
		'<div class="row"><div class="col-lg-8 col-lg-offset-2"><div class="input-group">' +
			'<span class="input-group-addon" id="sizing-addon2">' +
				'<span class="glyphicon glyphicon-file" aria-hidden="true" style="padding-right: 10px"></span>姓名' +
			'</span>' +
			'<input type="text" class="form-control" id="name" disabled="true" name="name" style="text-align: center">' +
			'<span class="input-group-btn">' +
				'<button class="btn btn-default" type="button" onclick=removeDisabled("name");>' +
					'<span class="glyphicon glyphicon-pencil" aria-hidden="true" style=""></span>' +
				'</button>' +
			'</span>' +
		'</div></div></div>';

	var phone =
		'<div class="row"><div class="col-lg-8 col-lg-offset-2"><div class="input-group">' +
			'<span class="input-group-addon" id="sizing-addon2">' +
				'<span class="glyphicon glyphicon-earphone" aria-hidden="true" style="padding-right: 10px"></span>联系方式' +
			'</span>' +
			'<input type="text" class="form-control" disabled="true" id="phone" name="phone" style="text-align: center">' +
			'<span class="input-group-btn">' +
				'<button class="btn btn-default" type="button" onclick=removeDisabled("phone");>' +
					'<span class="glyphicon glyphicon-pencil" aria-hidden="true" style=""></span>' +
				'</button>' +
			'</span>' +
		'</div></div></div>';

	var unit =
		'<div class="row"><div class="col-lg-8 col-lg-offset-2"><div class="input-group">' +
			'<span class="input-group-addon" id="sizing-addon2">' +
				'<span class="glyphicon glyphicon-home" aria-hidden="true" style="padding-right: 10px"></span>单位' +
			'</span>' +
			'<input type="text" class="form-control" disabled="true" id="unit" name="unit" style="text-align: center">' +
			'<span class="input-group-btn">' +
				'<button class="btn btn-default" type="button" onclick=removeDisabled("unit");>' +
					'<span class="glyphicon glyphicon-pencil" aria-hidden="true" style=""></span>' +
				'</button>' +
			'</span>' +
		'</div></div></div>';

	var password =
		'<div class="row"><div class="col-lg-8 col-lg-offset-2"><div class="input-group">' +
			'<span class="input-group-addon" id="sizing-addon2">' +
				'<span class="glyphicon glyphicon-lock" aria-hidden="true" style="padding-right: 10px"></span>密码' +
			'</span>' +
			'<input type="password" class="form-control" disabled="true" id="password" name="password" style="text-align: center">' +
			'<span class="input-group-btn">' +
				'<button class="btn btn-default" type="button" onclick=removeDisabled("password");>' +
					'<span class="glyphicon glyphicon-pencil" aria-hidden="true" style=""></span>' +
				'</button>' +
			'</span>' +
		'</div></div></div>';

	var newPassword =
		'<div class="row new-password" style="display:none;"><div class="col-lg-8 col-lg-offset-2"><div class="input-group">' +
			'<span class="input-group-addon" id="sizing-addon2">' +
				'<span class="glyphicon glyphicon-lock" aria-hidden="true" style="padding-right: 10px"></span>新密码' +
			'</span>' +
			'<input type="password" class="form-control" placeholder="请输入新密码" id="newPassword" name="newPassword"  style="text-align: center">' +
			'<span class="input-group-btn">' +
				'<button class="btn btn-default" type="button" onclick=removeDisabled("newPassword")><span class="glyphicon glyphicon-pencil" aria-hidden="true" style=""></span></button>' +
			'</span>' +
		'</div></div></div>';

	var formTail = '</form>';
	BootstrapDialog.show({
		title : '修改用户信息',
		message : formHead + name + phone + unit + password + newPassword + formTail,

		onshown: function(dialog){
			console.log(data.name);
			$('#changeInfoForm #name').val(getRideOfNullAndEmpty(data.name));
			$('#changeInfoForm #phone').val(getRideOfNullAndEmpty(data.phone));
			$('#changeInfoForm #unit').val(getRideOfNullAndEmpty(data.unit));
			$('#changeInfoForm #password').val("******");

		},
		buttons : [{
			label : '取消',
			action : function(dialog) {
				dialog.close();
			}
		}, {
			label : '确认修改',
			cssClass : 'btn-primary',
			action : function(dialog){
				changeInfoHandler($('#changeInfoForm')[0], data);
			}
		}]
	});

}


function addUserDialog() {
	var formHead = '<form id="signUpForm" >';

	var account =
		'<div class="row"><div class="col-lg-6 col-lg-offset-3"><div class="input-group">' +
			'<span class="input-group-addon" id="sizing-addon2">' +
				'<span class="glyphicon glyphicon-user" aria-hidden="true"></span>' +
			'</span>' +
			'<input type="text" class="form-control" name="userId" placeholder="账号">' +
		'</div></div></div>';
	var password =
		'<div class="row"><div class="col-lg-6 col-lg-offset-3"><div class="input-group">' +
			'<span class="input-group-addon" id="sizing-addon2">' +
				'<span class="glyphicon glyphicon-lock" aria-hidden="true"></span>' +
			'</span>' +
			'<input type="password" class="form-control" name="password" placeholder="初始密码" aria-describedby="sizing-addon2">' +
		'</div></div></div>';
	var name =
		'<div class="row"><div class="col-lg-6 col-lg-offset-3"><div class="input-group">' +
			'<span class="input-group-addon" id="sizing-addon2">' +
				'<span class="glyphicon glyphicon-file" aria-hidden="true"></span>' +
			'</span>' +
			'<input type="text" class="form-control" name="name" placeholder="用户姓名（选填）" aria-describedby="sizing-addon2">' +
		'</div></div></div>';
	var phone =
		'<div class="row"><div class="col-lg-6 col-lg-offset-3"><div class="input-group">' +
			'<span class="input-group-addon" id="sizing-addon2">' +
				'<span class="glyphicon glyphicon-earphone" aria-hidden="true"></span>' +
			'</span>' +
			'<input type="text" class="form-control" name="phone" placeholder="联系方式（选填）" aria-describedby="sizing-addon2">' +
		'</div></div></div>';
	var unit =
		'<div class="row"><div class="col-lg-6 col-lg-offset-3"><div class="input-group">' +
			'<span class="input-group-addon" id="sizing-addon2">' +
				'<span class="glyphicon glyphicon-home" aria-hidden="true"></span>' +
			'</span>' +
			'<input type="text" class="form-control" name="unit" placeholder="单位（选填）" aria-describedby="sizing-addon2">' +
		'</div></div></div>';
	var table =
		'<div class="panel panel-default" style="margin-top: 5%"><div class="panel-heading text-center">权限设置</div>' +
		'<table class="table table-hover privilege-table">' +
			'<thead><tr><th>权限</th><th>洋山港</th><th>罗泾港</th><th>外高桥港</th><th>黄浦江</th></tr></thead>' +
			'<tbody></tbody>' +
		'</table></div>';

	var formTail = '</form>';
	BootstrapDialog.show({
		title : '添加新用户',
		message : formHead + account + password + name + phone + unit + table + formTail,

		onshown: function(dialog){

			var prName = new Array("查看水深", "预测", "预警", "导入数据", "导出数据", "土方计算");
			var table = $('.privilege-table tbody');
			for (i = 0; i < 6; i++) {
				var entry = "";
				entry += '<tr><td>' + prName[i] + '</td>';
				for (j = 0; j < 4; j++) {
					var value = "" + j + i;
					entry += '<td><input type="checkbox" class="form-control" name="privilege" value="' + value +'"></td>';
				}
				entry += '</tr>';
				table.append(entry);
			}

			$('input').iCheck({
				checkboxClass: 'icheckbox_flat-green'
			});
		},
		buttons : [{
			label : '取消',
			action : function(dialog) {
				dialog.close();
			}
		}, {
			label : '确认添加',
			cssClass : 'btn-primary',
			action : function(dialog){
				addUserHandler($('#signUpForm')[0]);
			}
		}]
	});

}

function deleteUserDialog() {
	var waitingGIF = '<img src="/img/loading.gif" id="loading-indicator" style="display:none" />';

	var formHead =
		'<form id="delteUserForm" >';
	var table =
		'<div class="panel panel-default" style="margin-top: 5%"><div class="panel-heading text-center">用户管理</div>' +
		'<table class="table table-hover user-table">' +
			'<thead><tr><th>账号</th><th>姓名</th><th>联系方式</th><th>最近登录</th><th>单位</th></tr></thead>' +
			'<tbody>' +
		'</tbody>' +
		'</table></div>';
	var formTail = '</form>';

	var pagination = '<div class="text-center"><ul class="pagination" margin="auto"></ul></div>';

	BootstrapDialog.show({
		title : '用户管理',
		message : formHead + table + formTail + pagination + waitingGIF,
		cssClass : 'deleteuser-dialog',
		onshown: function(dialog){

			$('#loading-indicator').show();

			$.ajax({
				url: "/getuser",
				type: "GET",
				contentType: false   // tell jQuery not to set contentType
			}).fail(function() {
				alert("未连接到服务器!");
				$('#loading-indicator').hide();

			}).done(function(data) {
				$('#loading-indicator').hide();
				dialog.setData("user", data);
				dialog.setData("lastEntry", "");
				showTablePage(dialog);
				showTableEntry(dialog);
			})


		},
		buttons : [{
			label : '取消',
			action : function(dialog) {
				dialog.close();
			}
		},{
			id : 'btn-privilege',
			label : '修改权限',
			cssClass : 'btn-warning',
			action : function(dialog){
				var last = dialog.getData("lastEntry");
				var account = last.innerHTML.substring(4, last.innerHTML.indexOf('</td>'));
				var privilege = "";
				for (i = 0; i < dialog.getData("user").length; i++) {
					console.log(dialog.getData("user")[i].userId);
					if (dialog.getData("user")[i].userId ==  account) {
						privilege = dialog.getData("user")[i].privilege;
					}
				}
				changePrivilegeDialog(account, privilege, dialog);
			}
		}, {
			id : 'btn-delete',
			label : '删除用户',
			cssClass : 'btn-danger',
			action : function(dialog){
				var last = dialog.getData("lastEntry");
				deleteUserHandler(last.innerHTML.substring(4, last.innerHTML.indexOf('</td>')), dialog);
			}
		}]
	});
}

function changePrivilegeDialog(account, privilege, userDialog) {
	var formHead = '<form id="changePrivilegeForm" >';

	var table =
		'<div class="panel panel-default" style="margin-top: 5%"><div class="panel-heading text-center">权限设置</div>' +
		'<table class="table table-hover privilege-table">' +
			'<thead><tr><th>权限</th><th>洋山港</th><th>罗泾港</th><th>外高桥港</th><th>黄浦江</th></tr></thead>' +
			'<tbody></tbody>' +
		'</table></div>';

	var formTail = '</form>';

	BootstrapDialog.show({
		title : '修改用户' + account + '的权限',
		message : formHead + table + formTail,

		onshown: function(dialog){
			var pr = parsePrivilege(privilege);
			var prName = new Array("查看水深", "预测", "预警", "导入数据", "导出数据", "土方计算");
			var table = $('.privilege-table tbody');
			for (i = 0; i < 6; i++) {
				var entry = "";
				entry += '<tr><td>' + prName[i] + '</td>';
				for (j = 0; j < 4; j++) {
					var value = "" + j + i;
					if (pr[j][i]) {
						entry += '<td><input type="checkbox" class="form-control" name="privilege" value="' + value +'" checked="true"></td>';
					} else {
						entry += '<td><input type="checkbox" class="form-control" name="privilege" value="' + value +'"></td>';
					}
				}
				entry += '</tr>';
				table.append(entry);
			}

			$('input').iCheck({
				checkboxClass: 'icheckbox_flat-green'
			});
		},
		buttons : [{
			label : '取消',
			action : function(dialog) {
				dialog.close();
			}
		}, {
			label : '确认修改',
			cssClass : 'btn-primary',
			action : function(dialog){
				changePrivilegeHandler($('#changePrivilegeForm')[0], account, dialog, userDialog)
			}
		}]
	});

}

function showTablePage(dialog) {
	var data = dialog.getData("user");
	var page = 0;
	if (dialog.getData("lastPage") != null) {
		page = dialog.getData("lastPage").value - 1;
	}

	var numOfPages = data.length / 10;
	if (data.length % 10 != 0) {
		numOfPages+=1;
	}
	$('.pagination').empty();
	for (i = 1; i <= numOfPages; i++) {
		var li = '<li value=' + i + '><a >' + i + '</a></li>';
		$('.pagination').append(li);
	}

	dialog.setData("lastPage", $('.pagination li')[page]);

	$(dialog.getData("lastPage")).addClass("disabled");
	$('.pagination li').click(function() {

		$(this).addClass("disabled");
		var last = dialog.getData("lastPage");
		$(last).removeClass("disabled");
		dialog.setData("lastPage", this);
		showTableEntry(dialog);
	})

}

function showTableEntry(dialog) {
	dialog.getButton('btn-delete').disable();
	dialog.getButton('btn-privilege').disable();
	var data = dialog.getData("user");
	var page = 1;
	if (dialog.getData("lastPage") != null) {
		page = dialog.getData("lastPage").value;
	}

	if (dialog.getData("lastEntry") != "") {
		$(dialog.getData("lastEntry")).removeClass("table-active");
		dialog.setData("lastEntry", "");
	}
	$('.user-table tbody').empty();
	for (i = 10 * (page - 1); i < Math.min(data.length, 10 * page); i++) {
		with (data[i]) {
			var tr = '<tr><td>' + userId + '</td>' +
				'<td>' + getRideOfNullAndEmpty(name) + '</td>' +
				'<td>' + getRideOfNullAndEmpty(phone) + '</td>' +
				'<td>' + getRideOfNullAndEmpty(lastOnline) + '</td>' +
				'<td>' + getRideOfNullAndEmpty(unit) + '</td></tr>';
			$('.user-table tbody').append(tr);
		}
	}

	$('.user-table tr').click(function() {
		$(this).addClass("table-active");
		dialog.getButton('btn-delete').enable();
		dialog.getButton('btn-privilege').enable();
		var last = dialog.getData("lastEntry");
		if (last != "") {
			$(last).removeClass("table-active");
		}
		dialog.setData("lastEntry",this);

	})
}

//---------------------------Utility function-------------------------------
function parsePrivilege(privilege) {
	var result = [];
	var splits = privilege.split("|");
	for (i = 0; i < splits.length; i++) {
		var tmp = [];
		for (j = 0; j < splits[i].length; j++) {
			tmp.push(splits[i].charAt(j) == 'Y');
		}
		result.push(tmp);
	}
	return result;
}
function getRideOfNullAndEmpty(s) {
	if (s == null || s == "") {
		return "无";
	} else {
		return s;
	}
}
function getYears(date) {
	var years = [];
	var last = "";
	for (i = 0; i < date.length; i++) {
		if (last != date[i].substr(0, 2)) {
			years.push("20" + date[i].substr(0, 2));
			last = date[i].substr(0, 2);
		}
	}
	return years;
}

function getMonths(date, year) {
	var months = [];
	for (i = date.length - 1; i >= 0; i--) {
		if (year.substr(2, 2) == date[i].substr(0, 2)) {
			months.push(date[i].substr(3, 2));
		}
	}
	return months;
}