package controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelodao.LibroController;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/updateNovedad")
public class ServletUpdateNovedad extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ServletUpdateNovedad() {
        super();
    }

    // Handles the GET request
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    // Handles the POST request for updating the "novedad" field
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve parameters sent from the frontend
        String username = request.getParameter("username");
        int bookId = Integer.parseInt(request.getParameter("bookId"));
        boolean novedadChecked = Boolean.parseBoolean(request.getParameter("novedadChecked"));

        // Create an instance of the LibroController
        LibroController libroController = new LibroController();

        // Call the updateNovedad method to process the update
        String result = libroController.updateNovedad(username, bookId, novedadChecked);

        // Set response content type to JSON and send the result back to the frontend
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(result);
        out.flush();
    }
}
