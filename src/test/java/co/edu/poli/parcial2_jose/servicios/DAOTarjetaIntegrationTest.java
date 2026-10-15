package co.edu.poli.parcial2_jose.servicios;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import co.edu.poli.parcial2_jose.modelo.Credito;
import co.edu.poli.parcial2_jose.modelo.Debito;
import co.edu.poli.parcial2_jose.modelo.EstadoTarjeta;
import co.edu.poli.parcial2_jose.modelo.Tarjeta;
import co.edu.poli.parcial2_jose.modelo.Titular;

/**
 * Pruebas de integración de {@link DAOTarjetaIntegrationTest} contra MySQL.
 * <p>
 * Requiere MySQL encendido, la BD creada con el script y el archivo .env.
 * Si no hay conexión, las pruebas se omiten. Usan números que empiezan por
 * 9999 y un titular propio, y limpian todo al terminar.
 * </p>
 *
 * @author José
 */
class DAOTarjetaIntegrationTest {

	private static final String ID_TITULAR = "T-ITEST";
	private static final String PREFIJO = "9999";
	private static final Titular TITULAR = new Titular(ID_TITULAR, "Titular Integración");

	private final DAOTarjeta dao = new DAOTarjeta();

	@BeforeAll
	static void prepararTitular() throws Exception {
		Connection con = null;
		try {
			con = ConexionBD.getInstancia().getConexion();
		} catch (Exception e) {
			assumeTrue(false, "Sin conexión a MySQL, se omiten las pruebas de integración: " + e.getMessage());
		}
		try (PreparedStatement ps = con
				.prepareStatement("INSERT IGNORE INTO titular (id, nombre) VALUES (?, ?)")) {
			ps.setString(1, ID_TITULAR);
			ps.setString(2, TITULAR.getNombre());
			ps.executeUpdate();
		}
	}

	@BeforeEach
	@AfterEach
	void limpiarTarjetas() throws Exception {
		try (PreparedStatement ps = conexion().prepareStatement("DELETE FROM tarjeta WHERE numero LIKE ?")) {
			ps.setString(1, PREFIJO + "%");
			ps.executeUpdate();
		}
	}

	@AfterAll
	static void borrarTitular() throws Exception {
		try (PreparedStatement ps = conexion().prepareStatement("DELETE FROM titular WHERE id = ?")) {
			ps.setString(1, ID_TITULAR);
			ps.executeUpdate();
		}
	}

	private static Connection conexion() throws Exception {
		return ConexionBD.getInstancia().getConexion();
	}

	private int contarTarjetas() throws Exception {
		try (PreparedStatement ps = conexion().prepareStatement("SELECT COUNT(*) FROM tarjeta WHERE numero LIKE ?")) {
			ps.setString(1, PREFIJO + "%");
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				return rs.getInt(1);
			}
		}
	}

	@ParameterizedTest
	@EnumSource(EstadoTarjeta.class)
	@DisplayName("Un débito se guarda y se lee con el mismo estado")
	void debitoPersisteEstado(EstadoTarjeta estado) throws Exception {
		String numero = PREFIJO + "0001";
		String resultado = dao.create(new Debito(numero, "12/2030", estado, TITULAR, 500));

		assertTrue(resultado.startsWith("✔"));
		Tarjeta leida = dao.readone(numero);
		assertInstanceOf(Debito.class, leida);
		assertEquals(estado, leida.getEstado());
		assertEquals(500, ((Debito) leida).getSaldo());
	}

	@ParameterizedTest
	@EnumSource(EstadoTarjeta.class)
	@DisplayName("Un crédito se guarda y se lee con el mismo estado")
	void creditoPersisteEstado(EstadoTarjeta estado) throws Exception {
		String numero = PREFIJO + "0002";
		dao.create(new Credito(numero, "12/2030", estado, TITULAR, 2000));

		Tarjeta leida = dao.readone(numero);
		assertInstanceOf(Credito.class, leida);
		assertEquals(estado, leida.getEstado());
		assertEquals(2000, ((Credito) leida).getLimite());
	}

	@Test
	@DisplayName("Consultar una tarjeta inexistente devuelve null")
	void readoneInexistente() throws Exception {
		assertNull(dao.readone(PREFIJO + "0000"));
	}

	@Test
	@DisplayName("readall incluye las tarjetas guardadas con su estado")
	void readallIncluyeTarjetas() throws Exception {
		dao.create(new Debito(PREFIJO + "0003", "12/2030", EstadoTarjeta.ACTIVA, TITULAR, 1));
		dao.create(new Credito(PREFIJO + "0004", "12/2030", EstadoTarjeta.BLOQUEADA, TITULAR, 1));

		List<Tarjeta> todas = dao.readall();
		long propias = todas.stream().filter(t -> t.getNumero().startsWith(PREFIJO)).count();
		assertEquals(2, propias);
		assertTrue(todas.stream().anyMatch(
				t -> t.getNumero().equals(PREFIJO + "0004") && t.getEstado() == EstadoTarjeta.BLOQUEADA));
	}

	@Test
	@DisplayName("create(null) es rechazado")
	void createNuloFalla() {
		assertThrows(IllegalArgumentException.class, () -> dao.create(null));
	}

	@Test
	@DisplayName("Número duplicado: falla y no deja registros a medias (rollback)")
	void duplicadoHaceRollback() throws Exception {
		String numero = PREFIJO + "0005";
		dao.create(new Debito(numero, "12/2030", EstadoTarjeta.ACTIVA, TITULAR, 1));

		assertThrows(SQLException.class,
				() -> dao.create(new Credito(numero, "12/2031", EstadoTarjeta.BLOQUEADA, TITULAR, 1)));

		assertEquals(1, contarTarjetas());
		assertInstanceOf(Debito.class, dao.readone(numero));
	}

	@Test
	@DisplayName("La BD rechaza un estado fuera de ACTIVA/BLOQUEADA/CANCELADA (CHECK)")
	void bdRechazaEstadoInvalido() throws Exception {
		try (PreparedStatement ps = conexion().prepareStatement(
				"INSERT INTO tarjeta (numero, fecha_exp, estado, titular_id) VALUES (?, '12/2030', 'XYZ', ?)")) {
			ps.setString(1, PREFIJO + "0006");
			ps.setString(2, ID_TITULAR);
			assertThrows(SQLException.class, ps::executeUpdate);
		}
		assertEquals(0, contarTarjetas());
	}

	@Test
	@DisplayName("La BD rechaza una tarjeta sin estado (NOT NULL, sin DEFAULT)")
	void bdRechazaTarjetaSinEstado() throws Exception {
		try (Statement st = conexion().createStatement()) {
			assertThrows(SQLException.class, () -> st.executeUpdate(
					"INSERT INTO tarjeta (numero, fecha_exp, titular_id) VALUES ('" + PREFIJO + "0007', '12/2030', '"
							+ ID_TITULAR + "')"));
		}
		assertEquals(0, contarTarjetas());
	}
}