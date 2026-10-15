package co.edu.poli.parcial2_jose.modelo;

/**
 * Tarjeta cuyo saldo se debita directamente de los fondos disponibles.
 */
public class Debito extends Tarjeta {

	private double saldo;

	/**
	 * Crea una tarjeta de débito.
	 *
	 * @param numero número de la tarjeta
	 * @param fechaExp fecha de vencimiento en formato MM/AAAA
	 * @param estado estado inicial de la tarjeta
	 * @param titular titular propietario
	 * @param saldo saldo inicial
	 */
	public Debito(String numero, String fechaExp, EstadoTarjeta estado, Titular titular, double saldo) {
		super(numero, fechaExp, estado, titular);
		this.saldo = saldo;
	}

	/** @return saldo actual de la tarjeta */
	public double getSaldo() {
		return saldo;
	}

	/** @param saldo nuevo saldo de la tarjeta */
	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	/** @return representación textual de la tarjeta y su saldo */
	@Override
	public String toString() {
		return "Debito [" + super.toString() + ", saldo=" + saldo + "]";
	}
}