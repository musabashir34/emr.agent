package org.openjfx.emr.agent.controllers;
	
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.openjfx.emr.agent.models.Bill;
import org.openjfx.emr.agent.models.DefaultExample;
import org.openjfx.emr.agent.predictors.DiagnosisPredictor;
import org.openjfx.emr.agent.scrapers.FBSBillsScraper;
import org.openjfx.emr.agent.scrapers.FBSInpatientDataScraper;
import org.openjfx.emr.agent.utilities.Broadcaster;
import org.openjfx.emr.agent.utilities.Event;
import org.openjfx.emr.agent.utilities.Retainer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class BillingController extends BaseDefaultController {
	private static DiagnosisPredictor diagnosisPredictor= new DiagnosisPredictor();
	ObservableList<Bill> observableBillsList = FXCollections.observableArrayList();
	private Broadcaster broadcaster;
	private DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("M/dd/YYYY");

	@FXML TableView<DefaultExample> predictionsTable;
	@FXML TableColumn<DefaultExample, String> idColumn;
	@FXML TableColumn<DefaultExample, String> classColumn;
	@FXML TableColumn<DefaultExample, String> roomColumn;
	@FXML TableColumn<DefaultExample, Double> totalDepositColumn;
	@FXML TableColumn<DefaultExample, Double> totalBillColumn;
	@FXML TableColumn<DefaultExample, Integer> admissionDaysColumn;
	@FXML TableColumn<DefaultExample, String> defaultRiskColumn;
	@FXML TableView<Bill> billsTable;
	@FXML HBox payingInpatientsIndicatorBox;
	@FXML ProgressIndicator payingInpatientsIndicator;
	@FXML HBox billingIndicatorBox;
	@FXML ProgressIndicator billingIndicator;
	@FXML ChoiceBox<String> hmo_retainerChoice;
	@FXML DatePicker fromDate;
	@FXML DatePicker toDate;
	@FXML TableColumn<Bill, String> pidColumn;
	@FXML TableColumn <Bill, String> dateColumn;
	@FXML TableColumn<Bill, String> patientNameColumn;
	@FXML TableColumn<Bill, String> serviceDescriptionColumn;
	@FXML TableColumn<Bill, Integer> quantityColumn;
	@FXML TableColumn<Bill, Double> amountColumn;
	@FXML TableColumn<Bill, Double> totalAmountColumn;
	@FXML TableColumn<Bill, String> isInpatientColumn;
	@FXML TableColumn<Bill, String> diagnosisColumn;
	@FXML
	public void initialize() {
		idColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, String>("pid"));
		classColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, String>("financialClass"));
		roomColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, String>("room"));
		totalDepositColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, Double>("totalDepositedAmount"));
		totalBillColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, Double>("totalBill"));
		admissionDaysColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, Integer>("daysOnAdmission"));
		defaultRiskColumn.setCellValueFactory(new PropertyValueFactory<DefaultExample, String>("willDefault"));
		 dateColumn.setCellValueFactory(new PropertyValueFactory<Bill, String>("date"));
		 pidColumn.setCellValueFactory(new PropertyValueFactory<Bill, String>("pid"));
		 patientNameColumn.setCellValueFactory(new PropertyValueFactory<Bill, String>("patientName"));
		 serviceDescriptionColumn.setCellValueFactory(new PropertyValueFactory<Bill, String>("serviceDescription"));
		 quantityColumn.setCellValueFactory(new PropertyValueFactory<Bill, Integer>("quantity"));
		 amountColumn.setCellValueFactory(new PropertyValueFactory<Bill, Double>("amount"));
		 totalAmountColumn.setCellValueFactory(new PropertyValueFactory<Bill, Double>("totalAmount"));
		 isInpatientColumn.setCellValueFactory(new PropertyValueFactory<Bill, String>("isInpatient"));
		 diagnosisColumn.setCellValueFactory(new PropertyValueFactory<Bill, String>("diagnosis"));
		predictionsTable.setItems(observableList);
		billsTable.setItems(observableBillsList);
		payingInpatientsIndicatorBox.visibleProperty().bind(inpatientReportService.runningProperty());
		billingIndicatorBox.visibleProperty().bind(billingService.runningProperty());
		payingInpatientsIndicator.progressProperty().bind(inpatientReportService.progressProperty());
		billingIndicator.progressProperty().bind(billingService.progressProperty());
		hmo_retainerChoice.setValue(Retainer.values()[0].getHMO());
		for (Retainer retainer:Arrays.asList(Retainer.values()))
		hmo_retainerChoice.getItems().add(retainer.getHMO());
		broadcaster = Broadcaster.getInstance();
		broadcaster.subscribe(Event.PAYING_INPATIENTS_REPORT, this, this::updateInpatientsPredictionsTable);
		broadcaster.subscribe(Event.RETAINERS_BILLS_REPORT, this, this::updateBillsPredictionsTable);
		
	}

	@FXML public void predictInpatientsDebtRisk() {
		if (!observableList.isEmpty())
			observableList.clear();
		inpatientReportService.restart();
	}

	private Service<Void> inpatientReportService = new Service<>(){

		@Override
		protected Task<Void> createTask() {
			// TODO Auto-generated method stub
			return new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					FBSInpatientDataScraper inpatientDataScraper = new FBSInpatientDataScraper(predictor);
					inpatientDataScraper.getFbsBillingPage().setInpatientProgressUpdate((workDone, totalWork) -> 
                    updateProgress(workDone, totalWork));
					inpatientDataScraper.predictInpatientsDebtRisk();
					inpatientDataScraper.closeBrowser();
					return null;
				}
				
			};
		}
		
	};
	
	private void updateInpatientsPredictionsTable(String topic) {
		observableList.addAll(predictor.getExamplesList());
		String information = (observableList.isEmpty())? "There are no Paying Patients on Admission Currently":"Inpatient Report Successfully Generated";
		Alert alert = new Alert(Alert.AlertType.INFORMATION, information) {
			
		};
		alert.showAndWait();
		
	}
	
	private Service<Void> billingService = new Service<>(){

		@Override
		protected Task<Void> createTask() {
			// TODO Auto-generated method stub
			return new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					LocalDate from = fromDate.getValue();
					LocalDate to = toDate.getValue();
					FBSBillsScraper billingScraper = new FBSBillsScraper(diagnosisPredictor);
					billingScraper.getFbsBillingPage().setBillProgressUpdate((workDone, totalWork) -> 
                    updateProgress(workDone, totalWork));
					billingScraper.generateBills((String) hmo_retainerChoice.getSelectionModel().getSelectedItem(), dateformatter.format(from), dateformatter.
							format(to));
					billingScraper.closeBrowser();
					return null;
				}
				
			};
		}
		
	};
	
	@FXML public void generateBill() {
		if (!observableBillsList.isEmpty())
			observableBillsList.clear();
		billingService.restart();
		
		
	}
	
	private void updateBillsPredictionsTable(String topic) {
		observableBillsList.addAll(diagnosisPredictor.getBillsArray());
		String information = (observableBillsList.isEmpty())? "There are no Bills for "+(String) hmo_retainerChoice.getSelectionModel().getSelectedItem()+ 
				"in the Selected Period":"Bills Successfully Generated";
		Alert alert = new Alert(Alert.AlertType.INFORMATION, information) {
			
		};
		alert.showAndWait();
		
	}

	
	@FXML public void saveBill() {}

	@FXML public void clearBill() {
		if (!observableBillsList.isEmpty())
			observableBillsList.clear();
	}

}
