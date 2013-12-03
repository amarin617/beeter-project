<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="//netdna.bootstrapcdn.com/bootstrap/3.0.1/css/bootstrap.min.css">
<link rel="stylesheet"
	href="//netdna.bootstrapcdn.com/bootstrap/3.0.1/css/bootstrap-theme.min.css">
<!-- jQuery -->
  <script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
 
<!-- Validate Plugin -->
  <script src="http://jquery.bassistance.de/validate/jquery.validate.js"></script>
<script
	src="//netdna.bootstrapcdn.com/bootstrap/3.0.1/js/bootstrap.min.js"></script>

<link rel="stylesheet"
	href="//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.min.css">
<link rel="stylesheet"
	href="http://bootsnipp.com/css/bootsnipp.css?ver=2">
<link rel="stylesheet"
	href="http://bootsnipp.com/css/ladda-themeless.min.css">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Registro de Usuarios</title>
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="col-xs-12 col-sm-12 col-md-4 well well-sm">
				<legend>
					<i class="glyphicon glyphicon-globe"></i> Registrate!
				</legend>
				<form class="form"  id="contact-form" action="/beeter-auth/ServletRegister"
					method="POST" role="form">
					<div class="row">
						<div class="col-xs-6 col-md-6">
							<input class="form-control" id="name" name="name" placeholder="Nombre"
								type="text" required autofocus />
						</div>
						<div class="col-xs-6 col-md-6">
							<input class="form-control" id="username" name="username"
								placeholder="Nombre de Usuario" type="text" required />
						</div>
					</div>
					<input class="form-control" name="email" placeholder="Tu Email"
						type="email" required id="email" /> <input class="form-control"
						name="reenteremail" placeholder="Repite Email" type="email"
						required id=reenteremail /> <input class="form-control"
						name="password" placeholder="Contraseña" id="password" type="password" required />
					<br /> <br />
					<button class="btn btn-lg btn-primary btn-block" type="submit"
						value="register" id="boton" />
					Resgistrar
					</button>
					<script type="text/javascript">
						/* $('#reenteremail').change(function() {
							if ($('#reenteremail').val() == $('#email').val()) {
								$("#boton").disabled = true;
							} else {
								$("#boton").disabled;
							}
						}); */
						$(document).ready(function(){
							 
							 $('#contact-form').validate(
							 {
							  rules: {
							    name: {
							      minlength: 2,
							      required: true
							    },
							    username: {
							    	required:true,
							    	minlength: 2,
							    	maxlength: 10
							    },
							    email: {
							      required: true,
							      email: true
							    },
							    subject: {
							      minlength: 2,
							      required: true
							    },
							    message: {
							      minlength: 2,
							      required: true
							    }
							  },
							  highlight: function(element) {
							    $(element).closest('.control-group').removeClass('success').addClass('error');
							  },
							  success: function(element) {
							    element
							    .text('OK!').addClass('valid')
							    .closest('.control-group').removeClass('error').addClass('success');
							  }
							 });
							}); // end document.ready
					</script>
				</form>
			</div>
		</div>
	</div>

	</div>
</body>
</html>