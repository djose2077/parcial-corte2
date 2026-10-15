package co.edu.poli.parcial2_jose.modelo;

/**
 * Tarjeta de crédito con un límite máximo de financiación.
 */
public class Credito extends Tarjeta {

	private double limite;

	/**
	 * Crea una tarjeta de crédito.
	 *
	 * @param numero número de la tarjeta
	 * @param fechaExp fecha de vencimiento en formato MM/AAAA
	 * @param estado estado inicial de la tarjeta
	 * @param titular titular propietario
	 * @param limite límite de crédito asignado
	 */
	public Credito(String numero, String fechaExp, EstadoTarjeta estado, Titular titular, double limite) {
		super(numero, fechaExp, estado, titular);
		this.limite = limite;
	}

	/** @return límite de crédito asignado */
	public double getLimite() {
		return limite;
	}

	/** @param limite nuevo límite de crédito */
	public void setLimite(double limite) {
		this.limite = limite;
	}

	/** @return representación textual de la tarjeta y su límite */
	@Override
	public String toString() {
		return "Credito [" + super.toString() + ", limite=" + limite + "]";
	}
}
