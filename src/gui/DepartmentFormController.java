package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable{
	
	private Department entity; // DEPENDENCIA PARA O DEPARTAMENTO
	
	private DepartmentService service;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	// LISTA PARA OS OBJETOS SE INSCREVEREM E RECEBER O EVENTO
	
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
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	// MÉTODO PARA INSCREVER O LISTENER NA LISTA (ADICIONAR NA LISTA)
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null"); // PARA AVISAR CASO TENHA ESQUECIDO DE INJETAR AS DEPENDENCIAS
		}
		try {
			entity = getFormData(); // PEGA OS DADOS DA CAIXA DO FORMULÁRIO E INSTANCIA UM DEPARTAMENTO
			service.saveOrUpdate(entity); // SALVANDO NO BANCO DE DADOS
			notifyDataChangeListeners(); // NOTIFICAR OS DADOS ALTERADOS
			Utils.currentStage(event).close(); // SALVANDO COM SUCESSO FECHA A JANELA
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	// PARA CADA ALTERAÇÃO DA LISTA ELE NOTIFICA
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Department getFormData() { // PEGA OS DADOS DO FORMULÁRIO E RETORNAR UM NOVO OBJETO
		Department obj = new Department();
		
		obj.setId(gui.util.Utils.tryParseToInt(txtId.getText())); // PEGA O ID E CONVERTE PARA INT
		obj.setName(txtName.getText());
		
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
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
