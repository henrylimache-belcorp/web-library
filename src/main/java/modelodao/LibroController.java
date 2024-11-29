package modelodao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
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
        String sql = "SELECT * FROM libros";

        if (ordenar) {
            sql += " ORDER BY genero " + orden;
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

                Libro libro = new Libro(id, titulo, genero, autor, copias, novedad);

                libros.add(gson.toJson(libro));
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            con.desconectar();
        }

        return gson.toJson(libros);
    }

    // Method to update the "Novedad" field, only accessible to administrators
    public String updateNovedad(String username, int bookId, boolean novedadChecked) {
        Gson gson = new Gson();
        DBConnection con = new DBConnection();

        // Query to get the user's role
        String sql = "SELECT t.descripcion AS role FROM usuarios u " +
                     "JOIN tipo_usuario t ON u.tipo_usuario_codigotipo = t.codigotipo " +
                     "WHERE u.username = ?"; // Use parameterized query to avoid SQL injection

        try {
            PreparedStatement ps = con.getConnection().prepareStatement(sql);
            ps.setString(1, username);  // Set the username parameter
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                // Check if the user is an administrator
                if ("Administrator".equals(role)) {
                    // Proceed with updating "novedad" if the user is an admin
                    String updateSql = "UPDATE libros SET novedad = ? WHERE id = ?";
                    PreparedStatement updateSt = con.getConnection().prepareStatement(updateSql);
                    updateSt.setBoolean(1, novedadChecked); // Set the new value for "novedad"
                    updateSt.setInt(2, bookId); // Set the book ID to update
                    updateSt.executeUpdate();

                    return gson.toJson("Novedad updated successfully.");
                } else {
                    return gson.toJson("Permission Denied: Only administrators can modify Novedad.");
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            con.desconectar();
        }

        return gson.toJson("Error: Unable to update Novedad.");
    }

	@Override
	public String alquilar(int id, String username) {
		Timestamp fecha = new Timestamp(new Date().getTime());
		
		DBConnection con = new DBConnection();
		String sql = "Insert into alquiler values ('" + id + "', '" + username + "', '" + fecha + "')";
		
		try {
			Statement st = con.getConnection().createStatement();
			st.execute(sql);
			
			String modificar = modificar(id);
			
			if (modificar.equals("true")) {
				return "true";
			}
			
		}
		catch(Exception ex) {
			System.out.println(ex.toString());
		}
		finally {
			con.desconectar();
		}
		
		return "false";
	}

	@Override
	public String modificar(int id) {
		// TODO Auto-generated method stub
		DBConnection con = new DBConnection();
		String sql = "Update libros set copias = (copias - 1) where id = " + id;
		
		try {
			Statement st = con.getConnection().createStatement();
			st.execute(sql);
			
			return "true";
		}
		catch(Exception ex) {
			System.out.println(ex.toString());
		}
		finally {
			con.desconectar();
		}
				
		return "false";
	}

	@Override
	public String devolver(int id, String username) {
	    DBConnection con = new DBConnection();
	    String sql = "DELETE FROM alquiler WHERE id = " + id + " AND username = '" + username + "' LIMIT 1";
	    
	    try {
	        Statement st = con.getConnection().createStatement();
	        int rowsAffected = st.executeUpdate(sql);  // Use executeUpdate for DELETE
	        System.out.println("Rows affected by DELETE query: " + rowsAffected);

	        if (rowsAffected > 0) {
	            // Only proceed if deletion was successful
	            this.sumarCantidad(id);  // Update the book quantity
	            return "true";
	        } else {
	            System.out.println("No record found to delete.");
	            return "false";
	        }
	    } catch (Exception ex) {
	        System.out.println("Error in devolver() method: " + ex.toString());
	    } finally {
	        con.desconectar();
	    }
	    
	    return "false";
	}

	@Override
	public String sumarCantidad(int id) {
	    DBConnection con = new DBConnection();
	    String sql = "UPDATE libros SET copias = (SELECT copias FROM libros WHERE id = " + id + ") + 1 WHERE id = " + id;

	    try {
	        Statement st = con.getConnection().createStatement();
	        int rowsAffected = st.executeUpdate(sql);  // Use executeUpdate for UPDATE
	        System.out.println("Rows affected by UPDATE query: " + rowsAffected);

	        if (rowsAffected > 0) {
	            return "true";
	        } else {
	            System.out.println("No rows updated in libros table.");
	            return "false";
	        }
	    } catch (Exception ex) {
	        System.out.println("Error in sumarCantidad() method: " + ex.toString());
	    } finally {
	        con.desconectar();
	    }
	    
	    return "false";
	}


	
}
