package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener{

	private SellerService service;
	
	@FXML
	private TableView<Seller> tableViewSeller;
	
	@FXML
	private TableColumn<Seller, Integer> tableColumnId; // 'INTEGER' POIS O ID É UM INTEGER
	
	@FXML
	private TableColumn<Seller, String> tableColumnName; // 'STRING' POIS O NOME É UMA STRING
	
	@FXML
	private TableColumn<Seller, String> tableColumnEmail;
	
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;
	
	@FXML
	private Button btNew;
	
	private ObservableList<Seller> obsList;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = gui.util.Utils.currentStage(event);
		Seller obj = new Seller(); // INICIANDO UM FORMULARIO VAZIO
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}
	
	// INJETANDO DEPENDENCIA FAZENDO INVERSÃO DE CONTROLE:
	
	public void setSellerService(SellerService service) {
		this.service = service;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() { // INICIANDO O COMPORTAMENTO DAS COLUNAS
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
		Stage stage = (Stage) Main.getMainScene().getWindow(); // ACESSANDO A CENA, E O 'GETWINDOW' PEGA A REFERENCIA PARA A JANELA
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty()); // PARA FAZER A TABLE VIEW ACOMPANHAR A ALTURA DA JANELA
	}
	
	// MÉTODO PARA ACESSAR O SERVIÇO, CARREGAR OS DEPARTAMENTOS E JOGAR OS DEPARTAMENTOS NA 'OBSERVABLE LIST' 
	// PARA ASSOCIAR ELE COM O TABLEVIEW, PARA APARECER OS DEPARTAMENTOS NA TELA:
	
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null"); // SE NÃO TIVER FEITO A INJEÇÃO DE DEPENDENCIA
		}
		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list); // INSTANCIANDO O OBSERVABLE LIST PEGANDO OS DADOS DA LISTA
		tableViewSeller.setItems(obsList); // CARREGANDO OS ITENS NA TABLE VIEW E MOSTRAR NA TELA
		initEditButtons(); // ACRESCENTA UM NOVO BOTÃO COM O TEXTO 'EDIT' EM CADA LINHA DA TABELA, E QUANDO CLICA ELE ABRE O FORMULÁRIO DE EDIÇÃO
		initRemoveButtons();
	}

	// FUNÇÃO PARA CARREGAR A JANELA DO FORMULARIO PARA PREENCHER UM NOVO DEPARTAMENTO:
	
	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) { // INFORMANDO QUEM É O STAGE QUE CRIOU A JANELA DE DIALOGO
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			SellerFormController controller = loader.getController(); // PEGANDO O CONTROLADOR DA TELA QUE ACABOU DE CARREGAR (}O FORMULÁRIO)
			controller.setSeller(obj); // INJETAR NO CONTROLADOR O DEPARTAMENTO
			controller.setServices(new SellerService(), new DepartmentService()); // INJETAR O 'DEPARTMENT SERVICE'
			controller.loadAssociatedObjects(); // CARREGA OS DEPARTAMENTOS DO BANCO DE DADOS E DEIXAR NO CONTROLLER
			controller.subscribeDataChangeListener(this); // ME INSCREVENDO PARA RECEBER O EVENTO E EXECUTA O MÉTODO 'ONDATACHANGE'
			controller.updateFormData(); // CARREGAR OS DADOS DO OBJETO INJETADO NO FORMULÁRIO
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Seller data:");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL); // JANELA TRAVADA ATÉ FECHA-LA
			dialogStage.show();
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {	
		updateTableView(); // QUANDO ALTERA ALGUM DADO, CHAMA A FUNÇÃO 'UPDATE TABLE VIEW'
	}
	
	private void initEditButtons() { // ACRESCENTA UM BOTÃO DE EDITAR EM CADA LINHA DA TABELA:
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");
			
			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
						setGraphic(null);
						return;
				}	
				setGraphic(button);
				button.setOnAction(
				event -> createDialogForm(
						obj, "/gui/SellerForm.fxml",Utils.currentStage(event)));
						// PASSA O DEPARTAMENTO DA LINHA QUE TIVER O BOTÃO DE EDIÇÃO QUE FOR CLICADO
			}
		});
	}
	
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");
			
			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				
				if(obj == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction(EventHandler -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
		
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			}
			catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
