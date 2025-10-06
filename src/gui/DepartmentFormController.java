package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable{
	
	private Department entity; // DEPENDENCIA PARA O DEPARTAMENTO
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	@FXML
	public void onBtSaveAction() {
		System.out.println("onBtSaveAction");
	}
	
	@FXML
	public void onBtCancelAction() {
		System.out.println("onBtCancelAction");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes(); // CHAMANDO A INICIALIZAÇÃO
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId); // SÓ VAI ACEITAR NÚMERO INTEIRO NO 'TXT ID'
		Constraints.setTextFieldMaxLength(txtName, 30); // SÓ VAI ACEITAR DIGITAR NO MÁXIMO 30 CARACTERES NO 'TXT NAME'
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId())); // CONVERTENDO O 'ID' QUE É INTEIRO PARA STRING (POIS A TXTFIELD LE STRING)
		txtName.setText(entity.getName());
	}
}
