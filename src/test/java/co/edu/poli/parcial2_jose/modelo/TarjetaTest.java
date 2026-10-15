package co.edu.poli.parcial2_jose.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para los modelos de titulares y tarjetas.
 */
class TarjetaTest {

	/**
	 * Comprueba que una tarjeta de débito conserva sus datos y permite actualizarlos.
	 */
	@Test
	void tarjetaDebitoConservaYActualizaSusDatos() {
		Titular titular = new Titular("T100", "Ana");
		Debito debito = new Debito("1234", "08/2028", EstadoTarjeta.ACTIVA, titular, 250.0);

		assertEquals("1234", debito.getNumero());
		assertEquals("08/2028", debito.getFechaExp());
		assertSame(EstadoTarjeta.ACTIVA, debito.getEstado());
		assertSame(titular, debito.getTitular());
		assertEquals(250.0, debito.getSaldo());

		debito.setNumero("5678");
		debito.setFechaExp("09/2029");
		debito.setEstado(EstadoTarjeta.BLOQUEADA);
		debito.setTitular(new Titular("T200", "Luis"));
		debito.setSaldo(500.0);

		assertEquals("5678", debito.getNumero());
		assertEquals("09/2029", debito.getFechaExp());
		assertSame(EstadoTarjeta.BLOQUEADA, debito.getEstado());
		assertEquals("T200", debito.getTitular().getId());
		assertEquals(500.0, debito.getSaldo());
	}

	/**
	 * Comprueba que una tarjeta de crédito conserva el límite y su estado.
	 */
	@Test
	void tarjetaCreditoConservaYActualizaLimite() {
		Credito credito = new Credito("9876", "10/2030", EstadoTarjeta.CANCELADA,
				new Titular("T300", "Marta"), 1_000_000.0);

		assertEquals(1_000_000.0, credito.getLimite());
		assertSame(EstadoTarjeta.CANCELADA, credito.getEstado());

		credito.setLimite(2_000_000.0);

		assertEquals(2_000_000.0, credito.getLimite());
		assertTrue(credito.toString().contains("limite=2000000.0"));
	}

	/**
	 * Comprueba que el enum contiene exclusivamente los estados permitidos.
	 */
	@Test
	void enumContieneLosEstadosPermitidos() {
		assertEquals(3, EstadoTarjeta.values().length);
		assertEquals(EstadoTarjeta.ACTIVA, EstadoTarjeta.valueOf("ACTIVA"));
		assertEquals(EstadoTarjeta.BLOQUEADA, EstadoTarjeta.valueOf("BLOQUEADA"));
		assertEquals(EstadoTarjeta.CANCELADA, EstadoTarjeta.valueOf("CANCELADA"));
	}

	/**
	 * Comprueba la representación textual usada para mostrar un titular.
	 */
	@Test
	void titularMuestraIdYNombre() {
		assertEquals("T400, Sofia", new Titular("T400", "Sofia").toString());
	}
}
