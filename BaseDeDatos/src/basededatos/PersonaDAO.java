
package basededatos;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class PersonaDAO {
    private Persona p;
	

	public void salvar() {
		Connection ad = (Connection) ConexionBD.devolverConexion();

		try {

			String query = "UPDATE Persona SET nombre = ?, apellido = ?,edad = ? WHERE ID = ? LIMIT 1 ;";
			
			
			// el statement genera el vinculo con la base de datos :)
			PreparedStatement st = (PreparedStatement) ad.prepareStatement(query);
			st.setString(1, p.getNombre());
			st.setString(2, p.getApellido());
			st.setInt(3, p.getEdad());
			st.setInt(4, p.getID());
			
			st.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("error al generar el statement 2");
			 e.printStackTrace();

		}
	}
	
	public void agregar(int ID, String n, String a, int edad) {
		Connection ad = (Connection) ConexionBD.devolverConexion();
		
		try {
		String query = "INSERT INTO Persona VALUES (?,?,?,?)";
		
		PreparedStatement st = (PreparedStatement) ad.prepareStatement(query);
		st.setString(3, n);
		st.setString(2, a);
		st.setInt(4, edad);
		st.setInt(1, ID);
		
		st.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Error al actualizar");
			e.printStackTrace();
		}
	}
        /*public void verId() {
		Connection ad = (Connection) ConexionBD.devolverConexion();
		
		try {
		String query = "SELECT FROM Persona count* (?,?,?,?)";
		
		PreparedStatement st = (PreparedStatement) ad.prepareStatement(query);
		st.setString(3, n);
		st.setString(2, a);
		st.setInt(4, edad);
		st.setInt(1, ID);
		
		st.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Error al actualizar");
			e.printStackTrace();
		}
	}*/

	public Persona buscarID(int ID) {
		Connection ad = (Connection) ConexionBD.devolverConexion();

		try {
			// el statement genera el vinculo con la base de datos :)
			Statement st = (Statement) ad.createStatement();
			ResultSet rs = st.executeQuery("select * from Persona WHERE ID =" + ID);

			while (rs.next()) {
				this.p = new Persona(rs.getInt(1), rs.getString(3), rs.getString(2), rs.getInt(4));
			}
			
		} catch (SQLException e) {
			System.out.println("error al generar el statement 1");
			e.printStackTrace();

		}
		return p;
	}
	
	public void cerrarBd() {
		 ConexionBD.cerrarConexion();
	}
    
}
