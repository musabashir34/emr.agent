package org.openjfx.emr.agent.controllers;

import java.io.File;
import java.io.IOException;

import org.openjfx.emr.agent.predictors.DebtDefaultPredictor;
import org.openjfx.emr.agent.predictors.DiagnosisPredictor;
import org.openjfx.emr.agent.utilities.Broadcaster;
import org.openjfx.emr.agent.utilities.Event;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import javafx.scene.layout.HBox;

public class ModelsEvaluationController {
	private Broadcaster broadcaster;
	private Instances data;
	private CSVLoader loader;
	private DebtDefaultPredictor debtDefaultPredictor = new DebtDefaultPredictor();
	private DiagnosisPredictor diagnosisPredictor = new DiagnosisPredictor();
	@FXML BarChart<String, Integer> debtDefaultModelPerformance;
	@FXML Label footnote;
	@FXML BarChart<String, Integer> diagnosisModelPerformance;
	@FXML Label footnote1;
	@FXML HBox debtevaulationprogress;
	@FXML HBox diagnosisevaulationprogress;
	@FXML public void initialize() {
		broadcaster = Broadcaster.getInstance();
		broadcaster.subscribe(Event.DEBT_DEFAULT_MODEL_EVALUATION, this, this::displayDebtDefaultModelPerformance);
		broadcaster.subscribe(Event.DIAGNOSIS_MODEL_EVALUATION, this, this::displayDiagnosisModelPerformance);
		debtevaulationprogress.visibleProperty().bind(debtDefaultEvaluationService.runningProperty());
		diagnosisevaulationprogress.visibleProperty().bind(diagnosisEvaluationService.runningProperty());
	}
	private void displayDebtDefaultModelPerformance(String event) {
		debtDefaultModelPerformance.setAnimated(false);
		 debtDefaultModelPerformance.getData().addAll(debtDefaultPredictor.getSeriesArray());
		 debtDefaultModelPerformance.visibleProperty().set(true);
		 footnote.visibleProperty().set(true);
		 debtDefaultPredictor.getSeriesArray().clear();
	}
	private void displayDiagnosisModelPerformance(String event) {
		diagnosisModelPerformance.setAnimated(false);
		diagnosisModelPerformance.getData().addAll(diagnosisPredictor.getSeriesArray());
		diagnosisModelPerformance.visibleProperty().set(true);
		 footnote1.visibleProperty().set(true);
		 diagnosisPredictor.getSeriesArray().clear();
	}
	@FXML public void evaluateDiagnosisModel() {
		diagnosisModelPerformance.getData().clear();
		diagnosisModelPerformance.visibleProperty().set(false);
		 footnote1.visibleProperty().set(false);
		FileChooser fileChooser = new FileChooser();
		 fileChooser.setTitle("Evalute Diagnosis Predition Model");
		 fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV Files", "*.csv"));
		 File selectedFile = fileChooser.showOpenDialog(App.stage);
		 if (selectedFile != null) {
			 loader = new CSVLoader();
			 try {
				loader.setSource(selectedFile);
				data = loader.getDataSet();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		 }
		 diagnosisEvaluationService.restart();
	}
	private Service<Void> debtDefaultEvaluationService = new Service<>(){

		@Override
		protected Task<Void> createTask() {
			// TODO Auto-generated method stub
			return new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					debtDefaultPredictor.evaluateModel(data);
					return null;
				}
				
			};
		}
		
	};
	private Service<Void> diagnosisEvaluationService = new Service<>(){

		@Override
		protected Task<Void> createTask() {
			// TODO Auto-generated method stub
			return new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					diagnosisPredictor.evaluateModel(data);
					return null;
				}
				
			};
		}
		
	};

	@FXML public void evaluateDebtDefaultModel() {
		debtDefaultModelPerformance.getData().clear();
		debtDefaultModelPerformance.visibleProperty().set(false);
		 footnote.visibleProperty().set(false);
		FileChooser fileChooser = new FileChooser();
		 fileChooser.setTitle("Evalute Debt Default Predition Model");
		 fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV Files", "*.csv"));
		 File selectedFile = fileChooser.showOpenDialog(App.stage);
		 if (selectedFile != null) {
			 loader = new CSVLoader();
			 try {
				loader.setSource(selectedFile);
				data = loader.getDataSet();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
				
		 }
		 debtDefaultEvaluationService.restart();
	}

}
