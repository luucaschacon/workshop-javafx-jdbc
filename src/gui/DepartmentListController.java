package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable{

	private DepartmentService service;
	
	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId; // 'INTEGER' POIS O ID É UM INTEGER
	
	@FXML
	private TableColumn<Department, String> tableColumnName; // 'STRING' POIS O NOME É UMA STRING
	
	@FXML
	private Button btNew;
	
	private ObservableList<Department> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = gui.util.Utils.currentStage(event);
		Department obj = new Department(); // INICIANDO UM FORMULARIO VAZIO
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}
	
	// INJETANDO DEPENDENCIA FAZENDO INVERSÃO DE CONTROLE:
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() { // INICIANDO O COMPORTAMENTO DAS COLUNAS
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		Stage stage = (Stage) Main.getMainScene().getWindow(); // ACESSANDO A CENA, E O 'GETWINDOW' PEGA A REFERENCIA PARA A JANELA
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty()); // PARA FAZER A TABLE VIEW ACOMPANHAR A ALTURA DA JANELA
	}
	
	// MÉTODO PARA ACESSAR O SERVIÇO, CARREGAR OS DEPARTAMENTOS E JOGAR OS DEPARTAMENTOS NA 'OBSERVABLE LIST' 
	// PARA ASSOCIAR ELE COM O TABLEVIEW, PARA APARECER OS DEPARTAMENTOS NA TELA:
	
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null"); // SE NÃO TIVER FEITO A INJEÇÃO DE DEPENDENCIA
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list); // INSTANCIANDO O OBSERVABLE LIST PEGANDO OS DADOS DA LISTA
		tableViewDepartment.setItems(obsList); // CARREGANDO OS ITENS NA TABLE VIEW E MOSTRAR NA TELA
	}

	// FUNÇÃO PARA CARREGAR A JANELA DO FORMULARIO PARA PREENCHER UM NOVO DEPARTAMENTO:
	
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) { // INFORMANDO QUEM É O STAGE QUE CRIOU A JANELA DE DIALOGO
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			DepartmentFormController controller = loader.getController(); // PEGANDO O CONTROLADOR DA TELA QUE ACABOU DE CARREGAR (}O FORMULÁRIO)
			controller.setDepartment(obj); // INJETAR NO CONTROLADOR O DEPARTAMENTO
			controller.setDepartmentService(new DepartmentService()); // INJETAR O 'DEPARTMENT SERVICE'
			controller.updateFormData(); // CARREGAR OS DADOS DO OBJETO INJETADO NO FORMULÁRIO
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data:");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL); // JANELA TRAVADA ATÉ FECHA-LA
			dialogStage.show();
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}
