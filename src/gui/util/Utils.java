package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {

	// ACESSANDO O STAGE (PALCO) ONDE O CONTROLER QUE RECEBEU O EXEMPLO ESTÁ (SE CLICO NO BOTÃO PEGO O STAGE DAQUELE BOTÃO):

	public static Stage currentStage(ActionEvent event) {
		return (Stage) ((Node)event.getSource()).getScene().getWindow();
	}

	public static Integer tryParseToInt(String str) {
		try {
			return Integer.parseInt(str);
		}
		catch (NumberFormatException e) { // SE CASO O VALOR DIGITADO ESTEJA DIVERGENTE E NÃO DE PARA CONVERTER, ELE VOLTA NULO
			return null;
		}
	}
}
