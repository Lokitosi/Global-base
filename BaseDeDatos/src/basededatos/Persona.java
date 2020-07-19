
package basededatos;

public class Persona {
    	
	
	private int ID;
	private String nombre, apellido;
	private int edad;
	
	
	
	public Persona(int iD, String nombre, String apellido, int edad) {
		super();
		ID = iD;
		this.nombre = nombre;
		this.apellido = apellido;
		this.edad = edad;
	}
	
	public String getDatos() {
		String datos = "Nombre: "+ nombre + "    Apellido: " + apellido 
						+ "    Edad: "+ edad + "    ID: "+ID;
		return datos;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public int getEdad() {
		return edad;
	}
	public void setEdad(int edad) {
		this.edad = edad;
	}
	
}
