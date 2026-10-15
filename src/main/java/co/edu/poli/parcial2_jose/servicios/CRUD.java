package co.edu.poli.parcial2_jose.servicios;

import java.util.List;

/**
 * Define operaciones CRUD básicas para una entidad de tipo {@code T}.
 *
 * @param <T> tipo de entidad gestionada
 */
public interface CRUD<T> {

	/**
	 * Persiste una entidad.
	 *
	 * @param t entidad que se va a crear
	 * @return mensaje con el resultado de la operación
	 * @throws Exception si ocurre un error al persistir la entidad
	 */
	String create(T t) throws Exception;

	/**
	 * Busca una entidad por su identificador.
	 *
	 * @param <K> tipo del identificador
	 * @param id identificador de búsqueda
	 * @return entidad encontrada, o {@code null} si no existe
	 * @throws Exception si ocurre un error durante la consulta
	 */
	<K> T readone(K id) throws Exception;

	/**
	 * Recupera todas las entidades disponibles.
	 *
	 * @return lista de entidades
	 * @throws Exception si ocurre un error durante la consulta
	 */
	List<T> readall() throws Exception;
}
