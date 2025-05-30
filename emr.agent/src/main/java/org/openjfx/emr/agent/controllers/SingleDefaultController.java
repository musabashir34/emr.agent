package org.openjfx.emr.agent.controllers;

import java.io.IOException;
import java.util.Optional;

import org.openjfx.emr.agent.models.DefaultExample;

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

public class SingleDefaultController  extends BaseDefaultController{
	FXMLLoader loader;
	private VBox dialogContent;
	@FXML TableView<DefaultExample> predictionsTable;
	@FXML TableColumn<DefaultExample, String> idColumn;
	@FXML TableColumn<DefaultExample, String> classColumn;
	@FXML TableColumn<DefaultExample, String> roomColumn;
	@FXML TableColumn<DefaultExample, Double> totalDepositColumn;
	@FXML TableColumn<DefaultExample, Double> totalBillColumn;
	@FXML TableColumn<DefaultExample, Integer> admissionDaysColumn;
	@FXML TableColumn<DefaultExample, String> defaultRiskColumn;
	@FXML
	public void initialize() {
		loader = new FXMLLoader(SingleDefaultController.class.getResource("defaultdialogContent.fxml"));
		try {
			dialogContent = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		idColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, String>("pid"));
		classColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, String>("financialClass"));
		roomColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, String>("room"));
		totalDepositColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, Double>("totalDepositedAmount"));
		totalBillColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, Double>("totalBill"));
		admissionDaysColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, Integer>("daysOnAdmission"));
		defaultRiskColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, String>("willDefault"));
		predictionsTable.setItems(observableList);
	}
private class DefaultExampleDialog extends Dialog<DefaultExample> {
		
		private DefaultDialogController dialogContentController;
		public DefaultExampleDialog(FXMLLoader loader) {
			dialogContentController = loader.getController();
			DialogPane dp = getDialogPane();

	        setTitle( "Single Inpatient Data" );
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
	
		private DefaultExample createExample(ButtonType bt) {
			DefaultExample example = null;
			if( bt.getButtonData() == ButtonData.OK_DONE ) {
				example = predictor.makeSinglePrediction(dialogContentController);
			}
			return example;
			
		}
	}

	

	@FXML public void makeSinglePrediction() {
		DefaultExampleDialog dialog = new DefaultExampleDialog(loader);
		Optional<DefaultExample> example = dialog.showAndWait();
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
