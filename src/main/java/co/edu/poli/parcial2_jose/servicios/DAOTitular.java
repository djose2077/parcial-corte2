package co.edu.poli.parcial2_jose.servicios;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import co.edu.poli.parcial2_jose.modelo.Titular;

/**
 * Consulta los titulares almacenados en la base de datos.
 */
public class DAOTitular implements CRUD<Titular> {

	/**
	 * La creación de titulares no está implementada en este DAO.
	 *
	 * @param t titular que se intentaría crear
	 * @return {@code null}, ya que esta operación aún no está soportada
	 */
	@Override
	public String create(Titular t) {
		return null;
	}
	
	/**
	 * La búsqueda de un titular individual no está implementada en este DAO.
	 *
	 * @param <K> tipo del identificador
	 * @param id identificador del titular
	 * @return {@code null}, ya que esta operación aún no está soportada
	 * @throws Exception conservado por el contrato CRUD
	 */
	@Override
	public <K> Titular readone(K id) throws Exception {
		return null;
	}

	/**
	 * Obtiene todos los titulares para, por ejemplo, cargarlos en el formulario.
	 *
	 * @return lista de titulares de la base de datos
	 * @throws Exception si falla la conexión o la consulta SQL
	 */
	@Override
	public List<Titular> readall() throws Exception {

		Connection con = ConexionBD.getInstancia().getConexion();
		List<Titular> lista = new ArrayList<>();

		String SQL_SELECT_TITULAR = "SELECT ti.id AS titular_id, ti.nombre AS titular_nombre " 
		+ "FROM titular ti;";

		PreparedStatement ps = con.prepareStatement(SQL_SELECT_TITULAR);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Titular t = new Titular(rs.getString("titular_id"), rs.getString("titular_nombre"));
			lista.add(t);
		}
		return lista;
	}

}
