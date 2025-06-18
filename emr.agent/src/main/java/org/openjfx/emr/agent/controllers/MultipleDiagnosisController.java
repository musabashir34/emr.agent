package org.openjfx.emr.agent.controllers;

import java.io.File;

import org.openjfx.emr.agent.models.DefaultExample;
import org.openjfx.emr.agent.models.DiagnosisExample;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class MultipleDiagnosisController extends BaseDiagnosisController{
	private Instances data;
	private CSVLoader loader = new CSVLoader();
	@FXML TableView<DiagnosisExample> predictionsTable;
	@FXML TableColumn<DiagnosisExample, String> idColumn;
	@FXML TableColumn<DiagnosisExample, String> billItemColumn;
	@FXML TableColumn<DiagnosisExample, String> diagnosisColumn;
	@FXML
	public void initialize() {
		idColumn.setCellValueFactory(new PropertyValueFactory<DiagnosisExample, String>("pid"));
		billItemColumn.setCellValueFactory(new PropertyValueFactory<DiagnosisExample, String>("item"));
		diagnosisColumn.setCellValueFactory(new PropertyValueFactory<DiagnosisExample, String>("diagnosis"));
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
				Alert alert = new Alert(Alert.AlertType.INFORMATION, "File Upload Successful and Predictions Computed") {
					
				};
				alert.showAndWait();
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
