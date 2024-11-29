package controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelodao.UsuarioController;

import java.io.IOException;
import java.io.PrintWriter;
/**
 * Servlet implementation class ServletUsuarioRegister
 */
public class ServletUsuarioRegister extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletUsuarioRegister() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		UsuarioController usuario = new UsuarioController();
		
		String username = request.getParameter("username");
		String contrasena = request.getParameter("contrasena");
		String nombre = request.getParameter("nombre");
		String apellidos = request.getParameter("apellidos");
		String email = request.getParameter("email");
		double saldo = Double.parseDouble(request.getParameter("saldo"));
		boolean premium = Boolean.parseBoolean(request.getParameter("premium"));
		
		String result = usuario.register(username, contrasena, nombre, apellidos, email, saldo, premium);
		
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println(result);
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
