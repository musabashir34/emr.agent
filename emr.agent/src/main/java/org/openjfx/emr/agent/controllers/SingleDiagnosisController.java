package org.openjfx.emr.agent.controllers;

import java.io.IOException;
import java.util.Optional;

import org.openjfx.emr.agent.models.DefaultExample;
import org.openjfx.emr.agent.models.DiagnosisExample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class SingleDiagnosisController  extends BaseDiagnosisController{
	FXMLLoader loader;
	private VBox dialogContent;
	@FXML TableView<DiagnosisExample> predictionsTable;
	@FXML TableColumn<DiagnosisExample, String> idColumn;
	@FXML TableColumn<DiagnosisExample, String> billItemColumn;
	@FXML TableColumn<DiagnosisExample, String> diagnosisColumn;
	@FXML
	public void initialize() {
		loader = new FXMLLoader(SingleDiagnosisController.class.getResource("diagnosisdialogContent.fxml"));
		try {
			dialogContent = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		idColumn.setCellValueFactory(new PropertyValueFactory<DiagnosisExample, String>("pid"));
		billItemColumn.setCellValueFactory(new PropertyValueFactory<DiagnosisExample, String>("item"));
		diagnosisColumn.setCellValueFactory(new PropertyValueFactory<DiagnosisExample, String>("diagnosis"));
		predictionsTable.setItems(observableList);
	}
private class DiagnosisExampleDialog extends Dialog<DiagnosisExample> {
		
		private DiagnosisDialogController dialogContentController;
		public DiagnosisExampleDialog(FXMLLoader loader) {
			dialogContentController = loader.getController();
			DialogPane dp = getDialogPane();

	        setTitle( "Single Bill Item Data" );
	        setResultConverter( this::createExample );

	        ButtonType bt = new ButtonType("Predict", ButtonData.OK_DONE);
	        dp.getButtonTypes().addAll( bt, ButtonType.CANCEL );
	        dp.setContent( dialogContent );
	        final Button btnCancel = (Button) dp.lookupButton( ButtonType.CANCEL);
	        btnCancel.addEventFilter(ActionEvent.ACTION, event -> {
	        event.consume();
	        close();
	    });
		}
	
		private DiagnosisExample createExample(ButtonType bt) {
			DiagnosisExample example = null;
			if( bt.getButtonData() == ButtonData.OK_DONE ) {
				example = predictor.makeSinglePrediction(dialogContentController);
			}
			return example;
			
		}
	}

	

	@FXML public void makeSinglePrediction() {
		DiagnosisExampleDialog dialog = new DiagnosisExampleDialog(loader);
		Optional<DiagnosisExample> example = dialog.showAndWait();
		if (example.isEmpty()) {
Alert alert = new Alert(Alert.AlertType.ERROR,
		"Failed to Make the Prediction. Instance data entered not in profer format. Click Help button to learn more about the data format") {
			};
			alert.showAndWait();
		}else {
			observableList.add(example.get());
			Alert alert = new Alert(Alert.AlertType.INFORMATION, "Instance Successfully Created and Prediction Computed") {
				
			};
			alert.showAndWait();
		}
	}

}
