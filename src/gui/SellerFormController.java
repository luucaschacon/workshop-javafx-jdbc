package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable{
	
	private Seller entity; // DEPENDENCIA PARA O DEPARTAMENTO
	
	private SellerService service;
	
	private DepartmentService departmentService;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	// LISTA PARA OS OBJETOS SE INSCREVEREM E RECEBER O EVENTO
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpBirthDate;
	
	@FXML
	private TextField txtBaseSalary;
	
	@FXML
	private ComboBox<Department> comboBoxDepartment;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorBirthDate;
	
	@FXML
	private Label labelErrorBaseSalary;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	private ObservableList<Department> obsList;
	
	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	
	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
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

	private Seller getFormData() { // PEGA OS DADOS DO FORMULÁRIO E RETORNAR UM NOVO OBJETO
		Seller obj = new Seller();
		
		ValidationException exception = new ValidationException("Validation error"); // INSTANCIANDO A EXCEÇÃO
		
		obj.setId(gui.util.Utils.tryParseToInt(txtId.getText())); // PEGA O ID E CONVERTE PARA INT
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) { // 'TRIM' ELIMINA OS ESPAÇOS EM BRANCO NO INICIO OU FIM
			exception.addError("name", "Field can't be empty");
		// SE O NOME FOR IGUAL A NULO OU IGUAL AO STRING VAZIO, SIGNIFICA QUE A CAIXA ESTA VAZIA
		}
		obj.setName(txtName.getText()); // SETANDO O NOME
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) { 
			exception.addError("email", "Field can't be empty");
		}
		obj.setEmail(txtEmail.getText());
		
		if (dpBirthDate.getValue() == null) {
			exception.addError("birthDate", "Field can't be empty");
		}
		else {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault())); // 'START F DAY' CONVERTE A DATA ESCOLHIDA PARA A LOCALIDADE
			obj.setBirthDate(Date.from(instant)); // CONVERTENDO INSTANT P/ DATE
		}
		
		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) { 
			exception.addError("baseSalary", "Field can't be empty");
		}
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));
		
		obj.setDepartment(comboBoxDepartment.getValue()); // PEGA O DEPARTAMENTO DO COMBOBOX E JOGA PRO 'OBJ'
		
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
		Constraints.setTextFieldMaxLength(txtName, 70); // SÓ VAI ACEITAR DIGITAR NO MÁXIMO 30 CARACTERES NO 'TXT NAME'
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		
		initializeComboBoxDepartment();
	}
	
	// MÉTODO PARA PEGAR O OBJETO E PREENCHE O FORMULÁRIO COM OS DADOS:
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId())); // CONVERTENDO O 'ID' QUE É INTEIRO PARA STRING (POIS A TXTFIELD LE STRING)
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		if (entity.getBirthDate() != null) {
		dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault())); // PEGA O FUZO HORÁRIO DO COMPUTADOR DA PESSOA QUE ESTIVER USANDO O SISTEMA
		}
		if (entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
			// SE O DEPARTAMENTO DO VENDEDOR FOR IGUAL A NULO, OU SEJA É UM VENDEDOR NOVO QUE ESTA SENDO CADASTRADO (ELE NÃO TEM DEPARTAMENTO)
			// NESSE CASO DEFININDO PARA QUE O COMBOBOX ESTEJA SELECIONADO NO PRIMEIRO ELEMENTO DELE
		}
		else { 
		comboBoxDepartment.setValue(entity.getDepartment()); // SE NÃO: O DEPARTAMENTO QUE TIVER ASSOCIADO COM O VENDEDOR VAI P/ O COMBOBOX
		}
	}
	
	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService was null");
		}
		List<Department> list = departmentService.findAll(); // CARREGANDO OS DEPARTAMENTOS DO BANCO DE DADOS
		obsList = FXCollections.observableArrayList(list); // JOGANDO OS DEPARTAMNTOS DENTRO DA LISTA
		comboBoxDepartment.setItems(obsList); // SETANDO A LISTA COMO A LISTA ASSOCIADA AO COMBOBOX
	}
	
	private void setErrorMessages(Map<String, String> errors ) {
		Set<String> fields = errors.keySet(); // CRIANDO UM CONJUNTO RECEBENDO OS ERROS
		
//		if (fields.contains("name")) { // PERCORRENDO O CONJUNTO VERIFICANDO SE CONTEM UM VALOR DE ERRO (NO CASO O VAOR "NAME" QUE ACRESCENTAMOS LÁ NO ERROS)
//			labelErrorName.setText(errors.get("name")); // PEGA O LABEL E SETA A MENSAGEM DO ERRO NESSA LABEL 
//		}
//		else {
//			labelErrorName.setText("");
//		}
		
		// TROCANDO O IF E ELSE PARA FICAR MAIS ENCHUTO:
		
		labelErrorName.setText((fields.contains("name") ? errors.get("name") : "")); // "" DEIXA O ESPAÇO EM BRANCO DO LABEL SE ARRUMOU O CAMPO
		labelErrorEmail.setText((fields.contains("email") ? errors.get("email") : ""));
		labelErrorBirthDate.setText((fields.contains("birthDate") ? errors.get("birthDate") : ""));
		labelErrorBaseSalary.setText((fields.contains("baseSalary") ? errors.get("baseSalary") : ""));
	}
	
	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}
}
