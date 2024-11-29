package modelodao;

import java.sql.ResultSet;
import java.sql.Statement;

import com.google.gson.Gson;

import modelo.DBConnection;
import interfaz.IUsuarioController;
import modelo.Usuario;

public class UsuarioController implements IUsuarioController{

	@Override
	public String login(String username, String contrasena) {
	    Gson gson = new Gson();
	    
	    DBConnection con = new DBConnection();
	    // Updated SQL query to fetch role from tipo_usuario table
	    String sql = "SELECT u.*, t.descripcion AS role FROM usuarios u " +
	                 "JOIN tipo_usuario t ON u.tipo_usuario_codigotipo = t.codigotipo " +
	                 "WHERE u.username = '" + username + "' AND u.contrasena = '" + contrasena + "'";
	    
	    try {
	        Statement st = con.getConnection().createStatement();
	        ResultSet rs = st.executeQuery(sql);
	        
	        while (rs.next()) {
	            String nombre = rs.getString("nombre");
	            String apellidos = rs.getString("apellidos");
	            String email = rs.getString("email");
	            double saldo = rs.getDouble("saldo");
	            boolean premium = rs.getBoolean("premium");
	            String role = rs.getString("role");  // Fetch the role from the query
	            
	            // Pass the role to the Usuario constructor
	            Usuario usuario = new Usuario(username, contrasena, nombre, apellidos, email, saldo, premium, role);
	            return gson.toJson(usuario);
	        }
	    }
	    catch(Exception ex) {
	        System.out.println(ex.getMessage());
	    }
	    finally {
	        con.desconectar();
	    }
	    return "false";
	}


	@Override
	public String register(String username, String contrasena, String nombre, String apellidos, String email,
	                       double saldo, boolean premium) {
	    Gson gson = new Gson();
	    
	    DBConnection con = new DBConnection();
	    
	    // Default role, or you can handle it based on some other logic, e.g., based on the premium status.
	    String role = "Customer";  // Default role (you can adjust this logic as needed)
	    
	    // Insert into usuarios table, mapping role to tipo_usuario_codigotipo
	    String sql = "INSERT INTO usuarios (username, contrasena, nombre, apellidos, email, saldo, premium, tipo_usuario_codigotipo) " +
	                 "VALUES ('" + username + "', '" + contrasena + "', '" + nombre + "', '" + apellidos + "', '" + email + "', " + saldo + ", " + premium + ", " +
	                 "(SELECT codigotipo FROM tipo_usuario WHERE descripcion = '" + role + "'))";
	    
	    try {
	        Statement st = con.getConnection().createStatement();
	        st.execute(sql);
	        
	        // Create a Usuario object (using the default role here)
	        Usuario usuario = new Usuario(username, contrasena, nombre, apellidos, email, saldo, premium, role);
	        
	        st.close();
	        
	        return gson.toJson(usuario);
	    }
	    catch(Exception ex) {
	        System.out.println(ex.getMessage());
	    }
	    finally {
	        con.desconectar();
	    }
	    
	    return "false";
	}



	@Override
	public String pedir(String username) {
	    Gson gson = new Gson();
	    DBConnection con = new DBConnection();
	    
	    // Modify SQL to join with tipo_usuario and get the role (descripcion)
	    String sql = "SELECT u.*, t.descripcion AS role FROM usuarios u " +
	                 "JOIN tipo_usuario t ON u.tipo_usuario_codigotipo = t.codigotipo " +
	                 "WHERE u.username = '" + username + "'";

	    try {
	        Statement st = con.getConnection().createStatement();
	        ResultSet rs = st.executeQuery(sql);

	        while (rs.next()) {
	            String contrasena = rs.getString("contrasena");
	            String nombre = rs.getString("nombre");
	            String apellidos = rs.getString("apellidos");
	            String email = rs.getString("email");
	            double saldo = rs.getDouble("saldo");
	            boolean premium = rs.getBoolean("premium");
	            String role = rs.getString("role");  // Get the role from the query

	            // Pass the role to the Usuario constructor
	            Usuario usuario = new Usuario(username, contrasena, nombre, apellidos, email, saldo, premium, role);
	            
	            return gson.toJson(usuario);
	        }
	    } catch (Exception ex) {
	        System.out.println(ex.getMessage());
	    } finally {
	        con.desconectar();
	    }

	    return "false";
	}


	@Override
	public String restarDinero(String username, double nuevoSaldo) {
		// TODO Auto-generated method stub
		DBConnection con = new DBConnection();
		String sql = "Update usuarios set saldo = " + nuevoSaldo + " where username = '" + username + "'";
		
		try {
			
			Statement st = con.getConnection().createStatement();
			st.execute(sql);
			
			
			return "true";
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		finally {
			con.desconectar();
		}
		
		return "false";
	}

}
