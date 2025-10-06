package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
import model.exceptions.ValidationException;
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
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());
			// SE ACONTECER A EXCEÇÃO CHAMA O MÉTODO 'SETERRORMESSAGES' PASSANDO A COLEÇÃO DE ERROS
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
		
		ValidationException exception = new ValidationException("Validation error"); // INSTANCIANDO A EXCEÇÃO
		
		obj.setId(gui.util.Utils.tryParseToInt(txtId.getText())); // PEGA O ID E CONVERTE PARA INT
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) { // 'TRIM' ELIMINA OS ESPAÇOS EM BRANCO NO INICIO OU FIM
			exception.addError("name", "Field can't be empty");
		// SE O NOME FOR IGUAL A NULO OU IGUAL AO STRING VAZIO, SIGNIFICA QUE A CAIXA ESTA VAZIA
		}
		obj.setName(txtName.getText()); // SETANDO O NOME
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		// SE NA MINHA COLEÇÃO DE ERRORS TEM PELO MENOS 1 ERRO, SE SIM LANÇA A EXCEÇÃO CASO EXISTA UM ERRO
		}
		
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
	
	private void setErrorMessages(Map<String, String> errors ) {
		Set<String> fields = errors.keySet(); // CRIANDO UM CONJUNTO RECEBENDO OS ERROS
		
		if (fields.contains("name")) { // PERCORRENDO O CONJUNTO VERIFICANDO SE CONTEM UM VALOR DE ERRO (NO CASO O VAOR "NAME" QUE ACRESCENTAMOS LÁ NO ERROS)
			labelErrorName.setText(errors.get("name")); // PEGA O LABEL E SETA A MENSAGEM DO ERRO NESSA LABEL 
		}
	}
}
