var username = new URL(location.href).searchParams.get("username");
var user;

$(document).ready(function(){
	
	
	fillUsuario().then(function(){
		
		$("#user-saldo").html(user.saldo.toFixed(2)+"€");
		
		getAlquiladas(user.username);
	});
	
	$("#reservar-btn").attr("href", `home.html?username=${username}`);
	
	$("#form-modificar").on("submit",function(event){
		
		event.preventDefault();
		modificarUsuario();
	});
	
	$("#aceptar-eliminar-cuenta-btn").click(function(){
		
		eliminarCuenta().then(function(){
			location.href = "index.html";
		})
	})
	
});

function fillUsuario() {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: "GET",
            dataType: "html",
            url: "/libraryWeb/ServletUsuarioPedir",
            data: $.param({
                username: username,
            }),
            success: function(result) {
                let parsedResult = JSON.parse(result);
                
                if (parsedResult != false) {
                    user = parsedResult;

                    $("#input-username").val(parsedResult.username);
                    $("#input-contrasena").val(parsedResult.contrasena);
                    $("#input-nombre").val(parsedResult.nombre);
                    $("#input-apellidos").val(parsedResult.apellidos);
                    $("#input-email").val(parsedResult.email);
                    $("#input-saldo").val(parsedResult.saldo.toFixed(2));
                    $("#input-premium").prop("checked", parsedResult.premium);
                    
                    resolve();  // Resolve the promise when AJAX succeeds
                } else {
                    console.log("Error recuperando los datos del usuario");
                    reject("Error recuperando los datos del usuario");  // Reject on failure
                }
            },
            error: function(error) {
                console.log("Error in AJAX request", error);
                reject(error);  // Reject if there is an AJAX error
            }
        });
    });
}


function getAlquiladas(username) {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: "GET",
            dataType: "html",
            url: "/libraryWeb/ServletAlquilerListar",
            data: $.param({
                username: username,
            }),
            success: function(result) {
                let parsedResult = JSON.parse(result);
                
                if (parsedResult != false) {
                    mostrarHistorial(parsedResult);
                    resolve();  // Resolve the promise when the data is retrieved
                } else {
                    console.log("Error recuperando los datos de las reservas");
                    reject("Error recuperando los datos de las reservas");  // Reject if there's an error
                }
            },
            error: function(error) {
                console.log("Error in AJAX request", error);
                reject(error);  // Reject on AJAX error
            }
        });
    });
}


function mostrarLibros(libros) {
    let contenido = "";

    $.each(libros, function(index, libro) {
        libro = JSON.parse(libro);
        let precio;

        if (libro.copias > 0) {
            precio = libro.novedad ? (2 - (2 * 0.1)) : (1 - (1 * 0.1));
        } else {
            precio = libro.novedad ? 2 : 1;  // Set price even for books with 0 copies
        }

        contenido += '<tr><th scope="row">' + libro.id + '</th>' +
            '<td>' + libro.titulo + '</td>' +
            '<td>' + libro.genero + '</td>' +
            '<td>' + libro.autor + '</td>' +
            '<td>' + libro.copias + '</td>' +
            '<td>';

        if (libro.copias > 0) {
            contenido += `<input type="checkbox" id="novedad${libro.id}" ${libro.novedad ? 'checked' : ''} />`;
        } else {
            contenido += `<input type="checkbox" id="novedad${libro.id}" disabled ${libro.novedad ? 'checked' : ''} />`;  // Disable for 0 copies
        }

        contenido += '</td>' +
            '<td>' + precio + '</td>' +
            '<td><button onclick="alquilarLibro(' + libro.id + ',' + precio + ');" class="btn btn-success" ' +
            (libro.copias <= 0 ? 'disabled' : '') + '>Reservar</button></td></tr>';
    });

    $("#libros-tbody").html(contenido);
}



function devolverLibro(id) {
    $.ajax({
        type: "GET",
        dataType: "html",
        url: "/libraryWeb/ServletLibroDevolver",
        data: $.param({
            username: username,
            id: id,
        }),
        success: function(result) {
            if (result == "true") {
                console.log("Libro devuelto correctamente");
                // After successfully returning the book, reload the list
                getLibros(false, "ASC"); // Reload the book list after updating the stock
            } else {
                console.log("Error devolviendo el libro");
            }
        },
        error: function(xhr, status, error) {
            console.error("AJAX Error: " + status + " - " + error);
        }
    });
}



function modificarUsuario() {
    return new Promise((resolve, reject) => {
        let username = $("#input-username").val();
        let contrasena = $("#input-contrasena").val();
        let nombre = $("#input-nombre").val();
        let apellidos = $("#input-apellidos").val();
        let email = $("#input-email").val();
        let saldo = $("#input-saldo").val();
        let premium = $("#input-premium").prop('checked');
        
        $.ajax({
            type: "GET",
            dataType: "html",
            url: "/libraryWeb/ServletUsuarioModificar",
            data: $.param({
                username: username,
                contrasena: contrasena,
                nombre: nombre,
                apellidos: apellidos,
                email: email,
                saldo: saldo,
                premium: premium,
            }),
            success: function(result) {
                if (result != false) {
                    $("#modificar-error").addClass("d-none");
                    $("#modificar-exito").removeClass("d-none");
                    resolve();  // Resolve the promise on success
                } else {
                    $("#modificar-error").removeClass("d-none");
                    $("#modificar-exito").addClass("d-none");
                    reject("Error al modificar el usuario");  // Reject on failure
                }

                setTimeout(function() {
                    location.reload();
                }, 3000);
            }
        });
    });
}


function eliminarCuenta() {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: "GET",
            dataType: "html",
            url: "/libraryWeb/ServletUsuarioEliminar",
            data: $.param({
                username: username
            }),
            success: function(result) {
                if (result != false) {
                    console.log("Usuario eliminado");
                    resolve();  // Resolve the promise when the user is deleted
                } else {
                    console.log("Error eliminando el usuario");
                    reject("Error eliminando el usuario");  // Reject if there's an error
                }
            }
        });
    });
}

