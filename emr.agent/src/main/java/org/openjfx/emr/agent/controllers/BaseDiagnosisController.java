package org.openjfx.emr.agent.controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.openjfx.emr.agent.models.DefaultExample;
import org.openjfx.emr.agent.models.DiagnosisExample;
import org.openjfx.emr.agent.predictors.DebtDefaultPredictor;
import org.openjfx.emr.agent.predictors.DiagnosisPredictor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class BaseDiagnosisController {
	protected ObservableList<DiagnosisExample> observableList = FXCollections.observableArrayList();
	protected DiagnosisPredictor predictor = new DiagnosisPredictor();
	@FXML 
	public void clearPredictions() {
		if (!observableList.isEmpty())
			observableList.clear();
	}
	
	@FXML
	public void savePredictions() {
		 FileChooser fileChooser = new FileChooser();
		 fileChooser.setTitle("Save Predictions");
		 fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV Files", "*.csv"));
		 File selectedFile = fileChooser.showSaveDialog(App.stage);
		 if (selectedFile != null) {
		        Writer writer = null;
		        try {
		        	writer = new BufferedWriter(new FileWriter(selectedFile));
		        	String header = "PID" + "," +"Bill Item" + ","+"Diagnosis" + "\n";
		        	writer.write(header);
					for(DiagnosisExample example : observableList) {
						String text = example.getPid() + "," +example.getItem() + "," +example.getDiagnosis() +"\n" ;
						 writer.write(text);
					}
				} catch (IOException e) {
					Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to Save the Predictions") {
						
					};
					alert.showAndWait();
				}
		        finally {
		            
		            try {
						writer.flush();
						writer.close();
						Alert alert = new Alert(Alert.AlertType.INFORMATION, "Predictions Successfully Saved") {
							
						};
						alert.showAndWait();
					} catch (IOException e) {
						Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to Save the Predictions") {
							
						};
						alert.showAndWait();
					}
		             
		        } 
		 }
	}

}
