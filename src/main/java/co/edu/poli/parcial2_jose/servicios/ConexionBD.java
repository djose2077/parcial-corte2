package co.edu.poli.parcial2_jose.servicios;

import java.sql.Connection;
import java.sql.DriverManager;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Administra la conexión compartida con la base de datos MySQL.
 * Las credenciales se leen de las variables DB_URL, DB_USER y DB_PASSWORD
 * definidas en el archivo de entorno.
 */
public class ConexionBD {

	private static ConexionBD instancia;
	private Connection conexion;

	/**
	 * Abre una conexión usando la configuración del entorno.
	 *
	 * @throws Exception si falta configuración o no se puede conectar a MySQL
	 */
	private ConexionBD() throws Exception {

		Dotenv dotenv = Dotenv.load();

		String url = dotenv.get("DB_URL");
		String user = dotenv.get("DB_USER");
		String pass = dotenv.get("DB_PASSWORD");

		if (url == null || user == null || pass == null) {
			throw new RuntimeException("Faltan variables en el .env");
		}

		Class.forName("com.mysql.cj.jdbc.Driver");
		conexion = DriverManager.getConnection(url, user, pass);
	}

	/**
	 * Obtiene la instancia única del administrador de conexión.
	 *
	 * @return instancia compartida
	 * @throws Exception si no se puede establecer la conexión
	 */
	public static ConexionBD getInstancia() throws Exception {
		if (instancia == null) {
			instancia = new ConexionBD();
		}
		return instancia;
	}

	/**
	 * Devuelve una conexión abierta, creando una nueva si hace falta.
	 *
	 * @return conexión a la base de datos
	 * @throws Exception si no se puede recuperar o abrir la conexión
	 */
	public Connection getConexion() throws Exception {
		if (conexion == null || conexion.isClosed()) {
			instancia = new ConexionBD();
			return instancia.conexion;
		}
		return conexion;
	}
}