package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;

public class MainViewController implements Initializable {
	
	@FXML
	private MenuItem menuItemSeller;
	
	@FXML
	private MenuItem menuItemDepartment;
	
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
	}
	
	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> { // FUNÇÃO DE INICIALIZAÇÃO
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {		
	}
	
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) { // CARREGANDO A VIEW
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			Scene mainScene = Main.getMainScene(); // REFERENCIANDO A 'CENA' DO MAIN
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			// O 'GETROOT' PEGA A PRIMEIRA
			
			// MANIPULANDO A CENA PRINCIPAL, INCLUINDO NELA ALÉM DO MAIN MENU, OS FILHOS DA JANELA QUE TIVER ABRINDO:
			
			Node mainMenu = mainVBox.getChildren().get(0); // PEGANDO O PRIMEIRO FILHO DO VBOX DA JANELA PRINCIPAL (MAIN MENU)
			mainVBox.getChildren().clear(); // LIMPANDO TODOS OS FILHOS DO MAINVBOX
			mainVBox.getChildren().add(mainMenu); // ADICIONANDO O MAIN MENU E OS FILHOS DO MAIN VBOX NO MAIN VBOX
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			T controller = loader.getController(); // O GET CONTROLER VAI RETORNAR O CONTROLADOR DO TIPO QUE FOR CHAMADO
			initializingAction.accept(controller); // EXECUTANDO A AÇÃO 'INITIALIZING ACTION'
		} 
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}
