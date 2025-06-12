package org.openjfx.emr.agent.controllers;

import org.openjfx.emr.agent.models.DefaultExample;
import org.openjfx.emr.agent.scrapers.FBSInpatientDataScraper;
import org.openjfx.emr.agent.utilities.Broadcaster;
import org.openjfx.emr.agent.utilities.Event;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class BillingController extends BaseDefaultController {
	private Broadcaster broadcaster;

	@FXML TableView<DefaultExample> predictionsTable;
	@FXML TableColumn<DefaultExample, String> idColumn;
	@FXML TableColumn<DefaultExample, String> classColumn;
	@FXML TableColumn<DefaultExample, String> roomColumn;
	@FXML TableColumn<DefaultExample, Double> totalDepositColumn;
	@FXML TableColumn<DefaultExample, Double> totalBillColumn;
	@FXML TableColumn<DefaultExample, Integer> admissionDaysColumn;
	@FXML TableColumn<DefaultExample, String> defaultRiskColumn;
	@FXML TableView billsTable;
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
		broadcaster = Broadcaster.getInstance();
		broadcaster.subscribe(Event.PAYING_INPATIENTS_REPORT, this, this::updatePredictionsTable);
		
	}

	@FXML public void predictInpatientsDebtRisk() {
		if (!observableList.isEmpty())
			observableList.clear();
		inpatientReportService.restart();
	}

	@FXML public void saveBill() {}

	@FXML public void clearBill() {}
	private Service<Void> inpatientReportService = new Service<>(){

		@Override
		protected Task<Void> createTask() {
			// TODO Auto-generated method stub
			return new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					FBSInpatientDataScraper inpatientDataScraper = new FBSInpatientDataScraper(predictor);
					inpatientDataScraper.predictInpatientsDebtRisk();
					return null;
				}
				
			};
		}
		
	};
	private void updatePredictionsTable(String topic) {
		observableList.addAll(predictor.getExamplesList());
		String information = (observableList.isEmpty())? "There are no Paying Patients on Admission":"Inpatient Report Successfully Generated";
		Alert alert = new Alert(Alert.AlertType.INFORMATION, information) {
			
		};
		alert.showAndWait();
		
	}

}
