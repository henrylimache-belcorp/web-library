var username = new URL(location.href).searchParams.get("username");
var user;

$(document).ready(function(){

    $(function () {
        $('[data-toggle="tooltip"]').tooltip();
    });

    getUsuario().then(function(){
        $("#mi-perfil-btn").attr("href", "/libraryWeb/view/profile.html?username=" + username);
        $("#user-saldo").html(user.saldo.toFixed(2) + "$");

        getLibros(false, "ASC");

        $("#ordenar-genero").click(ordenarLibros);
    }).catch(function(error) {
        console.error("Error fetching user data:", error);
    });

});

function getUsuario() {
    return $.ajax({
        type: "GET",
        dataType: "html",
        url: "/libraryWeb/ServletUsuarioPedir",
        data: $.param({
            username: username
        })
    }).then(function(result) {
        let parsedResult = JSON.parse(result);
        
        if (parsedResult !== false) {
            user = parsedResult;
        } else {
            console.log("Error recuperando los datos del usuario");
        }
    }).catch(function(error) {
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
					mostrarLibros(parsedResult, user.role);
				}
				else{
					console.log("Error recuperando los datos de los libros");
				}
			}
		});
	
}

function mostrarLibros(libros, role) {
    let contenido = "";

    $.each(libros, function(index, libro) {
        libro = JSON.parse(libro);
        let precio;

        if (libro.copias > 0) {
            if (user.premium) {
                precio = libro.novedad ? (2 - (2 * 0.1)) : (1 - (1 * 0.1));
            } else {
                precio = libro.novedad ? 2 : 1;
            }

            // Build the row for each book
            contenido += '<tr><th scope="row">' + libro.id + '</th>' +
                '<td>' + libro.titulo + '</td>' +
                '<td>' + libro.genero + '</td>' +
                '<td>' + libro.autor + '</td>' +
                '<td>' + libro.copias + '</td>' +
                '<td>';

            // Enable/Disable the checkbox based on user role
            if (role === "Administrator") {
                contenido += `<input type="checkbox" id="novedad${libro.id}" ${libro.novedad ? 'checked' : ''} onclick="updateNovedad(${libro.id}, this.checked)" />`;  // Enable for admin
            } else {
                contenido += `<input type="checkbox" id="novedad${libro.id}" disabled ${libro.novedad ? 'checked' : ''} />`;  // Disable for non-admin
            }

            contenido += '</td>' +
                '<td>' + precio + '</td>' +
                '<td><button onclick="alquilarLibro(' + libro.id + ',' + precio + ');" class="btn btn-success" ' +
                (user.saldo < precio ? 'disabled' : '') +
                '>Reservar</button></td></tr>';
        }
    });

    $("#libros-tbody").html(contenido);
}


function updateNovedad(bookId, novedadChecked) {
    const username = user.username; // Get the logged-in username

    // Send POST request to update Novedad field
    fetch('/libraryWeb/updateNovedad', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: $.param({
            username: username,
            bookId: bookId,
            novedadChecked: novedadChecked
        })
    })
    .then(response => response.json())
    .then(data => {
        alert(data);  // Display success or error message
    })
    .catch(error => {
        console.error('Error:', error);
    });
}
function ordenarLibros(){
	
	if ($("#icono-ordenar").hasClass("fa-sort")) {
		getLibros(true, "ASC");
		$("#icono-ordenar").removeClass("fa-sort");
		$("#icono-ordenar").addClass("fa-sort-down");
	} else if ($("#icono-ordenar").hasClass("fa-sort-down")) {
		getLibros(true, "DESC");
		$("#icono-ordenar").removeClass("fa-sort-down");
		$("#icono-ordenar").addClass("fa-sort-up");
	} else if ($("#icono-ordenar").hasClass("fa-sort-up")) {
		getLibros(false, "ASC");
		$("#icono-ordenar").removeClass("fa-sort-up");
		$("#icono-ordenar").addClass("fa-sort");
	}
	
}
function alquilarLibro(id,precio){
	
	$.ajax({
			type:"GET",
			dataType:"html",
			url:"/libraryWeb/ServletLibroAlquilar",
			data: $.param({
				id:id,
				username:username
				
			}),
			success: function(result){
				let parsedResult = JSON.parse(result);
				
				if (parsedResult != false){
					restarDinero(precio).then(function(){
						location.reload();
					})
				}
				else{
					console.log("Error en la reserva de la película");
				}
			}
		});
	
}

function restarDinero(precio) {
    return new Promise(function(resolve, reject) {
        $.ajax({
            type: "GET",
            dataType: "html",
            url: "/libraryWeb/ServletUsuarioRestarDinero",
            data: $.param({
                username: username,
                saldo: parseFloat(user.saldo - precio)
            }),
            success: function(result) {
                let parsedResult = JSON.parse(result);

                if (parsedResult != false) {
                    console.log("Saldo actualizado");
                    resolve();
                } else {
                    console.log("Error en el proceso de pago");
                    reject("Error en el proceso de pago");
                }
            },
            error: function(error) {
                console.log("AJAX error: " + error);
                reject("AJAX error");
            }
        });
    });
}
