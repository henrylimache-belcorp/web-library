package beans;

import java.sql.Date;

public class Alquiler {
	private int id;
	private String username;
	private String genero;
	private Date fechaAlquiler;
	private boolean novedad;
	
	public Alquiler(int id, String username, String genero, Date fechaAlquiler, boolean novedad) {
		super();
		this.id = id;
		this.username = username;
		this.genero = genero;
		this.fechaAlquiler = fechaAlquiler;
		this.novedad = novedad;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public Date getFechaAlquiler() {
		return fechaAlquiler;
	}

	public void setFechaAlquiler(Date fechaAlquiler) {
		this.fechaAlquiler = fechaAlquiler;
	}

	public boolean isNovedad() {
		return novedad;
	}

	public void setNovedad(boolean novedad) {
		this.novedad = novedad;
	}

	@Override
	public String toString() {
		return "Alquiler [id=" + id + ", username=" + username + ", genero=" + genero + ", fechaAlquiler="
				+ fechaAlquiler + ", novedad=" + novedad + "]";
	}

	
	
}
