package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;

public class DepartmentListController implements Initializable{

	@FXML
	private TableView<Department> tableViewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnId; // 'INTEGER' POIS O ID É UM INTEGER
	
	@FXML
	private TableColumn<Department, String> tableColumnName; // 'STRING' POIS O NOME É UMA STRING
	
	@FXML
	private Button btNew;
	
	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
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

}
