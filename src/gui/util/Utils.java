package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	
	// ACESSANDO O STAGE (PALCO) ONDE O CONTROLER QUE RECEBEU O EXEMPLO ESTÁ (SE CLICO NO BOTÃO PEGO O STAGE DAQUELE BOTÃO):
	
	public static Stage currentStage(ActionEvent event) {
		return (Stage) ((Node)event.getSource()).getScene().getWindow();
	}

}
