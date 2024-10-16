var username = new URL(location.href).searchParams.get("username");
var user;

$(document).ready(function(){

    // Initialize tooltips
    $(function () {
        $('[data-toggle="tooltip"]').tooltip();
    });

    // Fetch user and then perform the necessary actions
    getUsuario().then(function(){
        $("#mi-perfil-btn").attr("href", "/libraryWeb/view/profile.html?username=" + username);
        $("#user-saldo").html(user.saldo.toFixed(2) + "$");

        // Fetch books after user is retrieved
        getLibros(false, "ASC");

        // Attach click event to sort books by genre
        $("#ordenar-genero").click(ordenarLibros);
    }).catch(function(error) {
        // Handle errors
        console.error("Error fetching user data:", error);
    });

});

// Refactored getUsuario function using promises (no async/await)
function getUsuario() {
    return $.ajax({
        type: "GET",
        dataType: "html",
        url: "/libraryWeb/ServletUsuarioPedir",
        data: $.param({
            username: username
        })
    }).then(function(result) {
        // Parse the result returned from the server
        let parsedResult = JSON.parse(result);
        
        if (parsedResult !== false) {
            // Assign the parsed user data to the user variable
            user = parsedResult;
        } else {
            console.log("Error recuperando los datos del usuario");
        }
    }).catch(function(error) {
        // Handle AJAX request errors
        console.error("An error occurred during the AJAX request:", error);
    });
}

function getLibros(ordenar,orden){
	
	$.ajax({
			type:"GET",
			dataType:"html",
			url:"/libraryWeb/ServletLibroListar",
			data: $.param({
				ordenar: ordenar,
				orden: orden
			}),
			success: function(result){
				let parsedResult = JSON.parse(result);
				
				if (parsedResult != false){
					mostrarLibros(parsedResult);
				}
				else{
					console.log("Error recuperando los datos de los libros");
				}
			}
		});
	
}

function mostrarLibros(libros){
	
	let contenido = "";
	
	$.each(libros, function(index, libro){
		
		libro = JSON.parse(libro);
		let precio;
		
		if (libro.copias > 0){
			
			if (user.premium){
				
				if (libro.novedad){
					precio = (2 - (2 * 0.1));
				}
				else{
					precio = (1 - (1 * 0.1));
				}
			}
			else{
				if (libro.novedad){
					precio = 2;
				}
				else{
					precio = 1;
				}
			}
			
			contenido += '<tr><th scope="row">' + libro.id + '</th>' + 
				'<td>' + libro.titulo + '</td>' +
				'<td>' + libro.genero + '</td>' +
				'<td>' + libro.autor + '</td>' +
				'<td>' + libro.copias + '</td>' +
				'<td><input type="checkbox" name="novedad" id="novedad' + libro.id + '" disabled '; 
			if (libro.novedad){
				contenido += 'checked';
			}
			contenido += '></td>' +
				'<td>' + precio + '</td>' +
				'<td><button onclick="alquilarLibro(' + libro.id + ',' + precio + ');" class="btn btn-success" ';
			if (user.saldo < precio){
				contenido += ' disabled ';
			}
				
			contenido += '>Reservar</button></td></tr>'
				
			
		}
		
	});
	
	$("#libros-tbody").html(contenido);
	
	
}