var API_BASE_URL = "http://localhost:8080/beeter-api";

$(document).ready(function(e){
	getStingsList();
});

$("#button_search").click(function(e){
	e.preventDefault();
	getStingsSearch();
});

$("#button_update").click(function(e){	
	updateSting(sting_id, sting);
});

function updateSting() {
	var msg = $('#upmsg_sting').val();
	var sub = $('#up_sting').val();
	var username = 'alicia';
	var sting_id = $('#upid_sting').val();
	var sting = '{"content": "'+ msg +'","subject": "'+ sub +'","username": "'+ username +'"}';
	var url = API_BASE_URL + '/stings/'+sting_id;
	$.ajax({
		url: url,
		type : 'PUT',
		crossDomain : true,
		data : sting,
		headers : {
			"Accept" : "application/vnd.beeter.api.sting+json",
			"Content-Type" : "application/vnd.beeter.api.sting+json"
		},
		beforeSend: function (request)
		{
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic "+ btoa('alicia:alicia'));	        
		},
	})
	.done(function (data, status, jqxhr) {
		console.log(status);
		location.reload();
	})
	.fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});	
}

function getSting(stingid) {
	var url = API_BASE_URL + '/stings/'+stingid; 
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		beforeSend: function (request)
		{
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic "+ btoa('alicia:alicia'));
		},
	})
	.done(function (data, status, jqxhr) {
		var sting = $.parseJSON(jqxhr.responseText);
		console.log(sting);
	})
	.fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	}); 
}

function editSting(stingid) {
	var url = API_BASE_URL + '/stings/'+stingid; 
	$.ajax({
		url : url,
		type : 'GET',
		crossDomain : true,
		beforeSend: function (request)
		{
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic "+ btoa('alicia:alicia'));
		},
	})
	.done(function (data, status, jqxhr) {
		var sting = $.parseJSON(jqxhr.responseText);
		var htmlModal= "";
		htmlModal += '<table class="table"><form action="javascript:updateSting()"><tbody><tr><td></td><td><h3>Editar Sting</h3><div class="form-group"><label>Asunto</label><input id="up_sting" type="text" class="form-control" value="'+sting.subject+'" required>';
		htmlModal += '<div class="form-group"><label>Mensaje:</label><textarea id="upmsg_sting" class="form-control" required>'+sting.content;
		htmlModal += '</textarea></div></div><button type="submit" class="btn btn-primary">Actualizar!</button><input id="upid_sting" type="text" class="hidden" value="'+sting.stingid+'"></td><td><button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button></td></tr></tbody></form></table>';
		$('#modalshow').html(htmlModal);
	})
	.fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function deleteSting(stingid) {
	var url = API_BASE_URL + '/stings/'+stingid; 
	$.ajax({
		url : url,
		type : 'DELETE',
		crossDomain : true,
		username : 'alicia',
		password : 'alicia',
	})
	.done(function (data, status, jqxhr) {
		console.log(status);
		location.reload();
	})
	.fail(function (jqXHR, textStatus) {
		console.log(textStatus);
		alert("No tienes permisos suficientes");
	});
}

function createSting() {
	var url = API_BASE_URL + '/stings';
	var msg = $('#msg_sting').val();
	var sub = $('#sub_sting').val();
	var username = 'alicia';
	var sting = '{"content": "'+ msg +'","subject": "'+ sub +'","username": "'+ username +'"}';
	$.ajax({
		url: url,
		type : 'POST',
		crossDomain : true,
		data : sting,
		headers : {
			"Accept" : "application/vnd.beeter.api.sting+json",
			"Content-Type" : "application/vnd.beeter.api.sting+json"
		},
		beforeSend: function (request)
		{
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic "+ btoa('alicia:alicia'));

		},
	})
	.done(function (data, status, jqxhr) {
		console.log(status);
		location.reload();
	})
	.fail(function (jqXHR, textStatus) {
		console.log(textStatus);
	});
}

function getStingsList() {
	var url = API_BASE_URL + '/stings?offset=0&length=6'; 
	$.ajax({
		url : url,
		type : 'GET',
		headers : {
			"Accept" : "application/vnd.beeter.api.sting.collection+json"
		},
		crossDomain : true,
		beforeSend: function (request)
		{
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic "+ btoa('alicia:alicia'));
		},
		success : function(data, status, jqxhr) {
			var response = $.parseJSON(jqxhr.responseText);
			var links = response.links;
			$.each(links, function(i,v){
				var link = v;	
			});
			var stings = response.stings;
			var htmlString = "";
			var n=0;
			var f=0;
			$.each(stings, function(i,v){
				var sting = v;
				if(n==0){                
                	htmlString += '<div class="col-sm-4"><div class="panel panel-primary"><div class="panel-heading"><li type="submit" class="glyphicon glyphicon-remove pull-left" onClick="deleteSting('+sting.stingid;
                    htmlString += ')";></li><h3 class="panel-title">&nbsp; '+sting.subject+'</h3><span class="label label-danger pull-right">'+sting.username+'</span></div><div class="panel-body">'+sting.content;
                    htmlString += '<li data-toggle="modal" href="#addWidgetModal" type="submit" class="glyphicon glyphicon-edit pull-right" onClick="editSting('+sting.stingid+')";></li></div></div>';
                    n++;
				}else{
					htmlString += '<div class="panel panel-primary"><div class="panel-heading"><li type="submit" class="glyphicon glyphicon-remove pull-left" onClick="deleteSting('+sting.stingid;
                    htmlString += ')";></li><h3 class="panel-title">&nbsp; '+sting.subject+'</h3><span class="label label-danger pull-right">'+sting.username+'</span></div><div class="panel-body">'+sting.content;
                    htmlString += '<li data-toggle="modal" href="#addWidgetModal" type="submit" class="glyphicon glyphicon-edit pull-right" onClick="editSting('+sting.stingid+')";></li></div></div></div>';
					n=0;
					f++;
				}
				if(f==3){
					htmlString += '</div>';
				}
			})
			$('#stingsshow').html(htmlString);
		},
		error : function(jqXHR, options, error) {}
		});
}

function getStingsSearch(){
	var url = '';
	var option = $('#option_search').val();
	if(option=="user"){
		var username = $('#text_search').val();
		url = API_BASE_URL + '/stings?username='+username+'&offset=0&length=6';
		$.ajax({
			url : url,
			type : 'GET',
			headers : {
				"Accept" : "application/vnd.beeter.api.sting.collection+json"
			},
			crossDomain : true,
			beforeSend: function (request)
			{
				request.withCredentials = true;
				request.setRequestHeader("Authorization", "Basic "+ btoa('alicia:alicia'));
			},
			success : function(data, status, jqxhr) {
				var response = $.parseJSON(jqxhr.responseText);
				var links = response.links;
				$.each(links, function(i,v){
					var link = v;
					console.log(v.uri);			
				});
				var stings = response.stings;
				var htmlString = '<div class="page-header"><h5>Resultados del usuario: '+ username +'</h5></div>';
				var n=0;
				var f=0;
				$.each(stings, function(i,v){
					var sting = v;
					if(n==0){                
                	htmlString += '<div class="col-sm-4"><div class="panel panel-primary"><div class="panel-heading"><li type="submit" class="glyphicon glyphicon-remove pull-left" onClick="deleteSting('+sting.stingid;
                    htmlString += ')";></li><h3 class="panel-title">&nbsp; '+sting.subject+'</h3><span class="label label-danger pull-right">'+sting.username+'</span></div><div class="panel-body">'+sting.content;
                    htmlString += '<li data-toggle="modal" href="#addWidgetModal" type="submit" class="glyphicon glyphicon-edit pull-right" onClick="editSting('+sting.stingid+')";></li></div></div>';
                    n++;
				}else{
					htmlString += '<div class="panel panel-primary"><div class="panel-heading"><li type="submit" class="glyphicon glyphicon-remove pull-left" onClick="deleteSting('+sting.stingid;
                    htmlString += ')";></li><h3 class="panel-title">&nbsp; '+sting.subject+'</h3><span class="label label-danger pull-right">'+sting.username+'</span></div><div class="panel-body">'+sting.content;
                    htmlString += '<li data-toggle="modal" href="#addWidgetModal" type="submit" class="glyphicon glyphicon-edit pull-right" onClick="editSting('+sting.stingid+')";></li></div></div></div>';
					n=0;
					f++;
					}
					if(f==3){
						htmlString += '</div>';
					}
				})
				$('#stingsshow').html(htmlString);
			},
			error : function(jqXHR, options, error) {}
		});
}else{
	var pattern = $('#text_search').val();
	url = API_BASE_URL + '/stings?pattern='+pattern+'&offset=0&length=6';
	$.ajax({
		url : url,
		type : 'GET',
		headers : {
			"Accept" : "application/vnd.beeter.api.sting.collection+json"
		},
		crossDomain : true,
		beforeSend: function (request)
		{
			request.withCredentials = true;
			request.setRequestHeader("Authorization", "Basic "+ btoa('alicia:alicia'));
		},
		success : function(data, status, jqxhr) {
			var response = $.parseJSON(jqxhr.responseText);
			var links = response.links;
			$.each(links, function(i,v){
				var link = v;
				console.log(v.uri);			
			});
			var stings = response.stings;
			var htmlString = '<div class="page-header"><h5>Resultados con palabra clave: '+ pattern +'</h5></div>';
			var n=0;
			var f=0;
			$.each(stings, function(i,v){
				var sting = v;
				if(n==0){                
                	htmlString += '<div class="col-sm-4"><div class="panel panel-primary"><div class="panel-heading"><li type="submit" class="glyphicon glyphicon-remove pull-left" onClick="deleteSting('+sting.stingid;
                    htmlString += ')";></li><h3 class="panel-title">&nbsp; '+sting.subject+'</h3><span class="label label-danger pull-right">'+sting.username+'</span></div><div class="panel-body">'+sting.content;
                    htmlString += '<li data-toggle="modal" href="#addWidgetModal" type="submit" class="glyphicon glyphicon-edit pull-right" onClick="editSting('+sting.stingid+')";></li></div></div>';
                    n++;
				}else{
					htmlString += '<div class="panel panel-primary"><div class="panel-heading"><li type="submit" class="glyphicon glyphicon-remove pull-left" onClick="deleteSting('+sting.stingid;
                    htmlString += ')";></li><h3 class="panel-title">&nbsp; '+sting.subject+'</h3><span class="label label-danger pull-right">'+sting.username+'</span></div><div class="panel-body">'+sting.content;
                    htmlString += '<li data-toggle="modal" href="#addWidgetModal" type="submit" class="glyphicon glyphicon-edit pull-right" onClick="editSting('+sting.stingid+')";></li></div></div></div>';
					n=0;
					f++;
				}
				if(f==3){
					htmlString += '</div>';
				}
			})
			$('#stingsshow').html(htmlString);
		},
		error : function(jqXHR, options, error) {
			//callbackError(jqXHR, options, error);
		}
	});
}	
}