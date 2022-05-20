package it.polito.tdp.genes;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.genes.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class FXMLController {
	
	private Model model ;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnStatistiche;

    @FXML
    private Button btnRicerca;

    @FXML
    private ComboBox<String> boxLocalizzazione;

    @FXML
    private TextArea txtResult;

    @FXML
    void doRicerca(ActionEvent event) {
    	String localizzazione = boxLocalizzazione.getValue();
    	if(localizzazione==null) {
    		txtResult.appendText("Selezionare un valore nel campo Localizzazione\n");
    		return;
    		}
    	List<String> localizations = this.model.getMaxPath(localizzazione);
    	if(localizations==null)
    	{
    		txtResult.appendText("Non è stato trovato nessun altro vertice collegato a: " + localizzazione + "\n\n");
    		return;
    	}
    	String result="";
    	for(String localization: localizations) {
    		result += localization+"\n";
    	}
    	result+="\n";
    	txtResult.appendText(result);
    	
    }

    @FXML
    void doStatistiche(ActionEvent event) {
    	String localizzazione = boxLocalizzazione.getValue();
    	if(localizzazione==null) {
    		txtResult.appendText("Selezionare un valore nel campo Localizzazione\n");
    		return;
    		}
    	txtResult.appendText(this.model.statistiche(localizzazione));

    }

    @FXML
    void initialize() {
        assert btnStatistiche != null : "fx:id=\"btnStatistiche\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnRicerca != null : "fx:id=\"btnRicerca\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxLocalizzazione != null : "fx:id=\"boxLocalizzazione\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
		List<String> localizations = model.getAllLocalizations();
		for(String s : localizations)
			boxLocalizzazione.getItems().add(s);
		this.model.creaGrafo();
		txtResult.appendText(this.model.infoGrafo() + "\n");
	}
}
