package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
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

}
