package org.openjfx.emr.agent.controllers;

import java.io.File;

import org.openjfx.emr.agent.models.DefaultExample;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class MultipleDefaultController extends BaseDefaultController{
	private Instances data;
	private CSVLoader loader = new CSVLoader();
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
		idColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, String>("pid"));
		classColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, String>("financialClass"));
		roomColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, String>("room"));
		totalDepositColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, Double>("totalDepositedAmount"));
		totalBillColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, Double>("totalBill"));
		admissionDaysColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, Integer>("daysOnAdmission"));
		defaultRiskColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, String>("willDefault"));
		predictionsTable.setItems(observableList);
	}

	@FXML public void makePredictions() {
		if (!observableList.isEmpty())
			observableList.clear();
		 FileChooser fileChooser = new FileChooser();
		 fileChooser.setTitle("Make Predictions");
		 fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV Files", "*.csv"));
		 File selectedFile = fileChooser.showOpenDialog(App.stage);
		 if (selectedFile != null) {
		    try {
				loader.setSource(selectedFile);
				data = loader.getDataSet();
				predictor.makePredictions(data);
				updatePredictionsTable();
				if(!observableList.isEmpty()) {
				Alert alert = new Alert(Alert.AlertType.INFORMATION, "File Upload Successful and Predictions Computed") {
					
				};
				alert.showAndWait();}
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
	}

	private void updatePredictionsTable() {
		observableList.addAll(predictor.getExamplesList());
		
	}

}
