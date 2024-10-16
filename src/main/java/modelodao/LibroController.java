package modelodao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import modelo.DBConnection;
import interfaz.ILibroController;
import modelo.Libro;

public class LibroController implements ILibroController {

	@Override
	public String listar(boolean ordenar, String orden) {

		Gson gson = new Gson();
		DBConnection con = new DBConnection();
		String sql= "SELECT * FROM libros";
		
		if(ordenar == true) {
			sql += " order by genero " + orden;
		}
		
		List<String> libros = new ArrayList<String>();
		try {
			
			Statement st = con.getConnection().createStatement();
			ResultSet rs = st.executeQuery(sql);
			
			while (rs.next()) {
				int id = rs.getInt("id");
				String titulo = rs.getString("titulo");
				String genero = rs.getString("genero");
				String autor = rs.getString("autor");
				int copias = rs.getInt("copias");
				boolean novedad = rs.getBoolean("novedad");
				
				Libro libro = new Libro(id,titulo,genero,autor,copias,novedad);
				
				libros.add(gson.toJson(libro));
				
			}
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		finally {
			con.desconectar();
		}
		
		return gson.toJson(libros);
	}
}
