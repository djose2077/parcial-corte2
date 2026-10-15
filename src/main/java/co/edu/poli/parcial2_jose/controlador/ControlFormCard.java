package co.edu.poli.parcial2_jose.controlador;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import co.edu.poli.parcial2_jose.modelo.Credito;
import co.edu.poli.parcial2_jose.modelo.Debito;
import co.edu.poli.parcial2_jose.modelo.EstadoTarjeta;
import co.edu.poli.parcial2_jose.modelo.Tarjeta;
import co.edu.poli.parcial2_jose.modelo.Titular;
import co.edu.poli.parcial2_jose.servicios.DAOTarjeta;
import co.edu.poli.parcial2_jose.servicios.DAOTitular;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.application.Platform;
import javafx.util.StringConverter;

/**
 * Controla las acciones del formulario para consultar y crear tarjetas.
 */
public class ControlFormCard {

	private static final DateTimeFormatter FORMATO_FECHA_EXPIRACION = DateTimeFormatter.ofPattern("MM/yyyy");

	@FXML
	private Button bttConsulta;
	@FXML
	private TextField txtTarjeta1;
	@FXML
	private TextArea txtAreaResultado;
	@FXML
	private Button bttCreacion;
	@FXML
	private TextField txtTarjeta2;
	@FXML
	private DatePicker datepk1;
	@FXML
	private ComboBox<Titular> cmbTitular;
	@FXML
	private ComboBox<EstadoTarjeta> cmbEstado;
	@FXML
	private RadioButton radio1; // Débito
	@FXML
	private RadioButton radio2; // Crédito
	@FXML
	private ToggleGroup tipo;

	private DAOTarjeta daoTarjeta;

	private DAOTitular daoTitular;

	/**
	 * Inicializa los selectores de fecha, estado y titular del formulario.
	 */
	@FXML
	private void initialize() {
		daoTarjeta = new DAOTarjeta();
		daoTitular = new DAOTitular();
		cmbEstado.getItems().setAll(EstadoTarjeta.values());
		cmbEstado.setConverter(new StringConverter<EstadoTarjeta>() {
			@Override
			public String toString(EstadoTarjeta estado) {
				if (estado == null) {
					return "";
				}
				return estado.name().charAt(0) + estado.name().substring(1).toLowerCase(Locale.ROOT);
			}

			@Override
			public EstadoTarjeta fromString(String valor) {
				return valor == null || valor.isBlank()
						? null
						: EstadoTarjeta.valueOf(valor.toUpperCase(Locale.ROOT));
			}
		});

		datepk1.setConverter(new StringConverter<LocalDate>() {
			@Override
			public String toString(LocalDate date) {
				return date == null ? "" : date.format(FORMATO_FECHA_EXPIRACION);
			}

			@Override
			public LocalDate fromString(String value) {
				return value == null || value.isBlank()
						? null
						: YearMonth.parse(value, FORMATO_FECHA_EXPIRACION).atDay(1);
			}
		});
		datepk1.setValue(LocalDate.now().withDayOfMonth(1));

		try {
			List<Titular> lista = daoTitular.readall();
			cmbTitular.getItems().setAll(lista);
		} catch (Exception e) {
			mostrarAlerta(e.getMessage());
		}

		txtTarjeta1.focusedProperty().addListener((obs, oldVal, newVal) -> {
			if (!newVal)
				validarSoloNumeros(txtTarjeta1);
		});

		txtTarjeta2.focusedProperty().addListener((obs, oldVal, newVal) -> {
			if (!newVal)
				validarSoloNumeros(txtTarjeta2);
		});

	}

	/**
	 * Busca una tarjeta por su número y presenta sus datos.
	 *
	 * @param event evento de acción del botón de consulta
	 */
	@FXML
	private void pressConsulta(ActionEvent event) {
		txtAreaResultado.setText("");
		String numero = txtTarjeta1.getText().trim();
		if (!numero.isEmpty()) {
			try {
				Tarjeta t = daoTarjeta.readone(numero);

				if (t != null)
					txtAreaResultado.setText(t.toString());
				else
					mostrarAlerta("No existe el numero de tarjeta");
			} catch (Exception e) {
				mostrarAlerta(e.getMessage());
			}
		} else {
			mostrarAlerta("Ingrese número de tarjeta");
		}
	}

	/**
	 * Valida los campos y crea una tarjeta del tipo y estado seleccionados.
	 *
	 * @param event evento de acción del botón de creación
	 */
	@FXML
	private void pressCreacion(ActionEvent event) {

		String numero = txtTarjeta2.getText().trim();
		if (numero.isEmpty()) {
			mostrarAlerta("⚠ Ingrese el número de tarjeta.");
			return;
		}

		if (datepk1.getValue() == null) {
			mostrarAlerta("⚠ Seleccione la fecha de expedición.");
			return;
		}
		String fechaExp = datepk1.getValue().format(FORMATO_FECHA_EXPIRACION);

		Titular titular = cmbTitular.getValue();
		if (titular == null) {
			mostrarAlerta("⚠ Seleccione un titular.");
			return;
		}

		EstadoTarjeta estado = cmbEstado.getValue();
		if (estado == null) {
			mostrarAlerta("⚠ Seleccione el estado de la tarjeta.");
			return;
		}

		Tarjeta nueva;

		if (radio1.isSelected()) {
			nueva = new Debito(numero, fechaExp, estado, titular, 0.0);
		} else {
			nueva = new Credito(numero, fechaExp, estado, titular, 1000000);
		}

		try {
			String resultado = daoTarjeta.create(nueva);
			mostrarAlerta(resultado);
			if (resultado.startsWith("✔")) {
				limpiarFormCrear();
			}
		} catch (Exception e) {
			mostrarAlerta(e.getMessage());
		}

	}

	private void mostrarAlerta(String mensaje) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Resultado");
		alert.setHeaderText(null);
		alert.setContentText(mensaje);
		alert.showAndWait();
	}

	private void limpiarFormCrear() {
		txtTarjeta2.clear();
		datepk1.setValue(null);
		cmbTitular.setValue(null);
		cmbEstado.setValue(null);
		radio1.setSelected(true);
	}

	private void validarSoloNumeros(TextField txt) {
		String texto = txt.getText().trim();
		if (!texto.isBlank()) {
			if (!texto.matches("\\d+")) {
				txtAreaResultado.setText("");
				txt.setStyle("-fx-border-color: red;");
				mostrarAlerta("Solo números son permitidos!");
				txt.setText("");
				Platform.runLater(() -> txt.requestFocus());
			} else {
				txt.setStyle("");
			}
		} else
			txt.setStyle("");
	}

}
