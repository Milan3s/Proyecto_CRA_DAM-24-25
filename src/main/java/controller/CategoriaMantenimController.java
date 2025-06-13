
package controller;


import dao.CategoriaDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.LoggerUtils;
import java.net.URL;
import java.util.ResourceBundle;
import model.Categoria;

// controlador del formulario para agregar o editar una categoría; Initializable-> para decir que haga eso al empezar
public class CategoriaMantenimController implements Initializable {

    // campos  y botones del formulario
    @FXML private TextField txtNombre;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    // la categoría que se edita 
    private Categoria categoria;

    // sirve para trabajar con la base de datos
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    //  se ejecuta al abrir la ventana
    @Override
    public void initialize(URL url, ResourceBundle rb) { // se ejecuta para preparar la ventana; initialize-> para iniciar 
        //  si haces clic en Guardar , se ejecuta guardarCategoria()
        btnGuardar.setOnAction(e -> guardarCategoria());
    }

    // este método recibe la categoría que se va a editar
    
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
        if (categoria != null) {
            // si es una categoría existente, llena el campo de texto con su nombre
            txtNombre.setText(categoria.getNombre());
        }
    }

    //  para guardar la categoría 
    private void guardarCategoria() {
        String nombre = txtNombre.getText().trim(); // Toma el nombre que escribió el usuario

        // cuando el campo está vacío, muestra una advertencia y no hace nada
        if (nombre.isEmpty()) {
            mostrarAlerta("Campo vacío", "Por favor, ingresa un nombre para la categoría.", Alert.AlertType.WARNING);
            LoggerUtils.logWarning("CATEGORIAS", "Campo nombre vacío en el formulario.");
            return;
        }

        // cuando la categoría es nueva  se inserta
        if (categoria == null) {
            boolean insertado = categoriaDAO.insertarCategoria(nombre);
            if (insertado) {
                mostrarAlerta("Éxito", "Categoría agregada con éxito.", Alert.AlertType.INFORMATION);
                cerrarVentana(); 
            }
        } else {
            // si la categoría ya existe, se actualiza su nombre
            boolean actualizado = categoriaDAO.actualizarCategoria(categoria.getCodigo(), nombre);
            if (actualizado) {
                mostrarAlerta("Éxito", "Categoría actualizada correctamente.", Alert.AlertType.INFORMATION);
                cerrarVentana(); // Cierra el formulario
            }
        }
    }

    // Acción del botón Cancelar
    @FXML
    private void btnActionCancelar() {
        cerrarVentana();
    }

    // sirve para cerrar la ventana del formulario
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    // muestra  mensajes al usuario 
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);//crea una nueva ventana de alerta
        alerta.setTitle(titulo);//le pone un título a la ventana de alerta
        alerta.setHeaderText(null); //quita el texto del encabezado de la alerta
        alerta.setContentText(mensaje); // escribe el mensaje que quieres mostrar dentro de la alerta.
        alerta.showAndWait(); // muestra la ventana y espera a que el usuario la cierre
    }
}