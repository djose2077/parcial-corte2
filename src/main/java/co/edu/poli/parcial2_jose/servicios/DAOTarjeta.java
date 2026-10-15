package co.edu.poli.parcial2_jose.servicios;

import co.edu.poli.parcial2_jose.modelo.Credito;
import co.edu.poli.parcial2_jose.modelo.Debito;
import co.edu.poli.parcial2_jose.modelo.EstadoTarjeta;
import co.edu.poli.parcial2_jose.modelo.Tarjeta;
import co.edu.poli.parcial2_jose.modelo.Titular;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Accede a tarjetas y sus subtipos en las tablas relacionadas de MySQL.
 */
public class DAOTarjeta implements CRUD<Tarjeta> {

	private final Connection conexionPrueba;

	private static final String SELECT_TARJETAS = "SELECT t.numero, t.fecha_exp, t.estado, "
			+ "ti.id AS titular_id, ti.nombre AS titular_nombre, d.saldo, c.limite "
			+ "FROM tarjeta t "
			+ "INNER JOIN titular ti ON t.titular_id = ti.id "
			+ "LEFT JOIN tarjeta_debito d ON d.numero = t.numero "
			+ "LEFT JOIN tarjeta_credito c ON c.numero = t.numero ";

	/**
	 * Crea un DAO que utiliza la conexión configurada para la aplicación.
	 */
	public DAOTarjeta() {
		this.conexionPrueba = null;
	}

	/**
	 * Crea un DAO que utiliza una conexión suministrada, principalmente para pruebas.
	 *
	 * @param conexion conexión JDBC que utilizará este DAO
	 */
	DAOTarjeta(Connection conexion) {
		if (conexion == null) {
			throw new IllegalArgumentException("La conexión no puede ser null.");
		}
		this.conexionPrueba = conexion;
	}

	/**
	 * Inserta una tarjeta y sus datos específicos dentro de una transacción.
	 *
	 * @param tarjeta tarjeta de débito o crédito que se va a guardar
	 * @return mensaje de confirmación
	 * @throws Exception si los datos son inválidos o falla la persistencia
	 */
	@Override
	public String create(Tarjeta tarjeta) throws Exception {
		if (tarjeta == null || tarjeta.getEstado() == null || tarjeta.getTitular() == null) {
			throw new IllegalArgumentException("La tarjeta, su estado y su titular son obligatorios.");
		}
		if (!(tarjeta instanceof Debito) && !(tarjeta instanceof Credito)) {
			throw new IllegalArgumentException("Tipo de tarjeta no soportado.");
		}

		Connection conexion = obtenerConexion();
		boolean autoCommitOriginal = conexion.getAutoCommit();
		try {
			conexion.setAutoCommit(false);
			String sqlTarjeta = "INSERT INTO tarjeta (numero, fecha_exp, estado, titular_id) VALUES (?, ?, ?, ?)";
			try (PreparedStatement ps = conexion.prepareStatement(sqlTarjeta)) {
				ps.setString(1, tarjeta.getNumero());
				ps.setString(2, tarjeta.getFechaExp());
				ps.setString(3, tarjeta.getEstado().name());
				ps.setString(4, tarjeta.getTitular().getId());
				ps.executeUpdate();
			}

			if (tarjeta instanceof Debito debito) {
				try (PreparedStatement ps = conexion
						.prepareStatement("INSERT INTO tarjeta_debito (numero, saldo) VALUES (?, ?)")) {
					ps.setString(1, tarjeta.getNumero());
					ps.setDouble(2, debito.getSaldo());
					ps.executeUpdate();
				}
			} else if (tarjeta instanceof Credito credito) {
				try (PreparedStatement ps = conexion
						.prepareStatement("INSERT INTO tarjeta_credito (numero, limite) VALUES (?, ?)")) {
					ps.setString(1, tarjeta.getNumero());
					ps.setDouble(2, credito.getLimite());
					ps.executeUpdate();
				}
			}

			conexion.commit();
			return "✔ " + tarjeta.getClass().getSimpleName() + " [" + tarjeta.getNumero()
					+ "] guardada correctamente.";
		} catch (Exception e) {
			try {
				conexion.rollback();
			} catch (Exception rollbackError) {
				e.addSuppressed(rollbackError);
			}
			throw e;
		} finally {
			conexion.setAutoCommit(autoCommitOriginal);
		}
	}

	/**
	 * Busca una tarjeta por número y recupera su tipo y titular.
	 *
	 * @param <K> tipo del identificador
	 * @param numero número de tarjeta como {@link String}
	 * @return tarjeta encontrada, o {@code null} si no existe
	 * @throws Exception si el identificador es inválido o falla la consulta
	 */
	@Override
	public <K> Tarjeta readone(K numero) throws Exception {
		if (!(numero instanceof String numeroTarjeta)) {
			throw new IllegalArgumentException("El número de tarjeta debe ser texto.");
		}

		Connection conexion = obtenerConexion();
		try (PreparedStatement ps = conexion.prepareStatement(SELECT_TARJETAS + "WHERE t.numero = ?")) {
			ps.setString(1, numeroTarjeta);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? mapearTarjeta(rs) : null;
			}
		}
	}

	/**
	 * Recupera todas las tarjetas, incluidas sus variantes de débito y crédito.
	 *
	 * @return lista de tarjetas
	 * @throws Exception si falla la consulta o el mapeo de resultados
	 */
	@Override
	public List<Tarjeta> readall() throws Exception {
		List<Tarjeta> tarjetas = new ArrayList<>();
		Connection conexion = obtenerConexion();
		try (PreparedStatement ps = conexion.prepareStatement(SELECT_TARJETAS + "ORDER BY t.numero");
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				tarjetas.add(mapearTarjeta(rs));
			}
		}
		return tarjetas;
	}

	/**
	 * Convierte una fila de la consulta conjunta en la subclase correspondiente.
	 *
	 * @param rs fila actual del resultado SQL
	 * @return tarjeta de débito o crédito construida desde la fila
	 * @throws Exception si el estado almacenado no es válido o falla la lectura
	 */
	private Tarjeta mapearTarjeta(ResultSet rs) throws Exception {
		String numero = rs.getString("numero");
		String fechaExp = rs.getString("fecha_exp");
		EstadoTarjeta estado = EstadoTarjeta.valueOf(rs.getString("estado"));
		Titular titular = new Titular(rs.getString("titular_id"), rs.getString("titular_nombre"));

		if (rs.getObject("saldo") != null) {
			return new Debito(numero, fechaExp, estado, titular, rs.getDouble("saldo"));
		}
		if (rs.getObject("limite") != null) {
			return new Credito(numero, fechaExp, estado, titular, rs.getDouble("limite"));
		}
		throw new IllegalStateException("La tarjeta " + numero + " no tiene un tipo asociado.");
	}

	private Connection obtenerConexion() throws Exception {
		return conexionPrueba == null ? ConexionBD.getInstancia().getConexion() : conexionPrueba;
	}
}
