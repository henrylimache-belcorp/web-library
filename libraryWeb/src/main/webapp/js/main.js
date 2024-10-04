var username = new URL(location.href).searchParams.get("username");
var user;

$(document).ready(function(){
	
	$(function (){
		$('[data-toggle="tooltip"]').tooltip();
	});
	
	getUsuario().then(function(){
		
		$("#mi-perfil-btn").attr("href","profile.html?username=" + username);
		
		$("#user-saldo").html(user.saldo.toFixed(2)+"€")
		
		getLibros(false,"ASC");
		
		$("#ordenar-genero").click(ordenarLibros);
				
	})	
	
})