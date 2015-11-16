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
		if (name == null || name.value == "") {
			alert("请填写姓名！");
			name.focus();
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
				name: name.value, phone: phone.value, privilege: ps}
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

function changePasswordHandler(thisform) {
	with (thisform) {
		var regexp = /^[a-zA-Z0-9]+$/;
		if (!regexp.test(newPassword.value) || newPassword.value.length < 5 || newPassword.value.length > 18) {
			alert("新密码必须为5至18位字母和数字的组合！");
			newPassword.focus();
			return;
		}
		$.ajax({
			method: "POST",
			url: "/changepassword",
			data: {userId: userId.value, oldPassword: SHA1(oldPassword.value), newPassword: SHA1(newPassword.value)}
		}).fail(function () {
			alert("未连接到服务器!");
		}).done(function (data) {
			if (data == 1) {
				alert('密码修改成功！');
			} else if (data == 0) {
				alert('旧密码不正确！');
			} else {
				alert('服务器异常！');
			}
		});
	}
}

function uploadHandler(thisform) {
	with (thisform) {
		var fileName = file.value;
		if(fileName.match(/fakepath/)) {
			// update the file-path text using case-insensitive regex
			fileName = fileName.replace(/C:\\fakepath\\/i, '');
		}
		console.log(fileName);
		var regexp = /^\d\d-\d\d\.txt+$/;
		if (!regexp.test(fileName)) {
			alert("文件名格式不正确！");
			return;
		}
		var fd = new FormData(thisform);
		fd.append("name", fileName);


		$.ajax({
			url: "/upload",
			type: "POST",
			data: fd,
			enctype: 'multipart/form-data',
			processData: false,  // tell jQuery not to process the data
			contentType: false   // tell jQuery not to set contentType
		}).fail(function () {
			alert("未连接到服务器!");
		}).done(function( data ) {
			console.log( data );
			alert("文件上传成功！");
		});
		return false;
	}
}

function showUploadFileName() {
	$("input[id=lefile]").click();
	$('input[id=lefile]').change(function() {
		$('#photoCover').val($(this).val());
	});
}
function uploadDialog() {
	var formHead =
		'<form id="uploadForm">';

	var formBody =
		'<div class="row"><div class="col-lg-8 col-lg-offset-2"><div class="input-group">' +
			'<input id="lefile" type="file" name="file" style="display:none">' +
			'<input id="photoCover" type="text" class="form-control">' +
			'<span class="input-group-btn">' +
				'<button class="btn btn-default" type="button" onclick=showUploadFileName();>选择文件</button>' +
			'</span>' +
		'</div></div></div>';
	var formTail = '</form>';
	BootstrapDialog.show({
		title : '上传水深数据',
		message : formHead + formBody + formTail,
		asynchronize: false,

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
				uploadHandler($('#uploadForm')[0]);
			}
		}]
	});
}



function changePasswordDialog() {
	var formHead =
		'<form id="changePasswordForm" >';

	var account =
		'<div class="row"><div class="col-lg-6 col-lg-offset-3"><div class="input-group">' +
			'<span class="input-group-addon" id="sizing-addon2">' +
				'<span class="glyphicon glyphicon-user" aria-hidden="true"></span>' +
			'</span>' +
			'<input type="text" class="form-control" name="userId" placeholder="账号">' +
		'</div></div></div>';
	var oldPassword =
		'<div class="row"><div class="col-lg-6 col-lg-offset-3"><div class="input-group">' +
			'<span class="input-group-addon" id="sizing-addon2">' +
				'<span class="glyphicon glyphicon-lock" aria-hidden="true"></span>' +
			'</span>' +
			'<input type="password" class="form-control" name="oldPassword" placeholder="旧密码" aria-describedby="sizing-addon2">' +
		'</div></div></div>';

	var newPassword =
		'<div class="row"><div class="col-lg-6 col-lg-offset-3"><div class="input-group">' +
			'<span class="input-group-addon" id="sizing-addon2">' +
				'<span class="glyphicon glyphicon-lock" aria-hidden="true"></span>' +
			'</span>' +
			'<input type="password" class="form-control" name="newPassword" placeholder="新密码" aria-describedby="sizing-addon2">' +
		'</div></div></div>';

	var formTail = '</form>';
	BootstrapDialog.show({
		title : '修改密码',
		message : formHead + account + oldPassword + newPassword + formTail,
		asynchronize: false,

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
			label : '确认修改',
			cssClass : 'btn-primary',
			action : function(dialog){
				changePasswordHandler($('#changePasswordForm')[0]);
			}
		}]
	});

}


function addUserDialog() {
	var formHead =
		'<form id="signUpForm" >';

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
	var table =
		'<div class="panel panel-default" style="margin-top: 5%"><div class="panel-heading text-center">权限设置</div>' +
		'<table class="table table-hover">' +
			'<thead><tr><th>权限</th><th>洋山港</th><th>罗泾港</th><th>外高桥港</th><th>黄浦江</th></tr></thead>' +
			'<tbody>' +
				'<tr><td>查看水深</td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="11"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="21"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="31"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="41"></td>' +
				'</tr>' +
				'<tr><td>预测</td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="12"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="22"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="32"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="42"></td>' +
				'</tr>' +
				'<tr><td>预警</td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="13"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="23"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="33"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="43"></td>' +
				'</tr>' +
				'<tr><td>导入数据</td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="14"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="24"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="34"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="44"></td>' +
				'</tr>' +
				'<tr><td>导出数据</td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="15"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="25"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="35"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="45"></td>' +
				'</tr>' +
				'<tr><td>土方计算</td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="16"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="26"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="36"></td>' +
					'<td><input type="checkbox" class="form-control" name="privilege" value="46"></td>' +
				'</tr>' +
			'</tbody>' +
		'</table></div>';

	var formTail = '</form>';
	BootstrapDialog.show({
		title : '添加新用户',
		message : formHead + account + password + name + phone + table + formTail,
		asynchronize: false,

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
			label : '确认添加',
			cssClass : 'btn-primary',
			action : function(dialog){
				addUserHandler($('#signUpForm')[0]);
			}
		}]
	});

}