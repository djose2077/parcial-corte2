package co.edu.poli.parcial2_jose.modelo;

/**
 * Persona titular de una o más tarjetas.
 */
public class Titular {

	private String id;

	private String nombre;

	/**
	 * Crea un titular.
	 *
	 * @param id identificador único del titular
	 * @param nombre nombre del titular
	 */
	public Titular(String id, String nombre) {
		this.id = id;
		this.nombre = nombre;
	}

	/** @return identificador del titular */
	public String getId() {
		return id;
	}

	/** @param id nuevo identificador del titular */
	public void setId(String id) {
		this.id = id;
	}

	/** @return nombre del titular */
	public String getNombre() {
		return nombre;
	}

	/** @param nombre nuevo nombre del titular */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/** @return identificador y nombre del titular */
	@Override
	public String toString() {
		return id + ", " + nombre;
	}
}
