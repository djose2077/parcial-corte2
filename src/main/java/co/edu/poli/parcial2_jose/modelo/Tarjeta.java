package co.edu.poli.parcial2_jose.modelo;

/**
 * Representa los datos comunes de una tarjeta de débito o crédito.
 * La clase es abstracta porque cada tarjeta debe pertenecer a un tipo concreto.
 */
public abstract class Tarjeta {

	private String numero;

	private String fechaExp;

	private EstadoTarjeta estado;

	private Titular titular;

	/**
	 * Crea una tarjeta con sus datos generales.
	 *
	 * @param numero número único de la tarjeta
	 * @param fechaExp fecha de vencimiento en formato MM/AAAA
	 * @param estado estado actual de la tarjeta
	 * @param titular titular propietario de la tarjeta
	 */
	public Tarjeta(String numero, String fechaExp, EstadoTarjeta estado, Titular titular) {
		this.numero = numero;
		this.fechaExp = fechaExp;
		this.estado = estado;
		this.titular = titular;
	}

	/** @return número de la tarjeta */
	public String getNumero() {
		return numero;
	}

	/** @param numero nuevo número de la tarjeta */
	public void setNumero(String numero) {
		this.numero = numero;
	}

	/** @return fecha de vencimiento en formato MM/AAAA */
	public String getFechaExp() {
		return fechaExp;
	}

	/** @param fechaExp nueva fecha de vencimiento en formato MM/AAAA */
	public void setFechaExp(String fechaExp) {
		this.fechaExp = fechaExp;
	}

	/** @return estado actual de la tarjeta */
	public EstadoTarjeta getEstado() {
		return estado;
	}

	/** @param estado nuevo estado de la tarjeta */
	public void setEstado(EstadoTarjeta estado) {
		this.estado = estado;
	}

	/** @return titular propietario de la tarjeta */
	public Titular getTitular() {
		return titular;
	}

	/** @param titular nuevo titular propietario de la tarjeta */
	public void setTitular(Titular titular) {
		this.titular = titular;
	}

	/** @return representación textual de los datos comunes de la tarjeta */
	@Override
	public String toString() {
		return "numero=" + numero + ", fechaExp=" + fechaExp + ", estado=" + estado + ", titular=" + titular;
	}
}
