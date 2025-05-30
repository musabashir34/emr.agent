package org.openjfx.emr.agent.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;

public class DefaultDialogController {
	@FXML public void initialize() {
		roomChoice.setValue("Male Ward-Bed 1");
		roomChoice.getItems().addAll("Male Ward-Bed 1", "Male Ward-Bed 2", "Male Ward-Bed 3", "Male Ward-Bed 4",
				"Female Ward 1", "Female Ward 2", "Female Ward 3", "Female Ward 4","Labor Room", "Amenity 1", "Amenity 2",
				"Amenity 3", "Amenity 4", "Amenity 5", "VIP 1", "VIP 2", "Maternity Ward", "NSCBU");
		financialClassChoice.setValue("Single Folder");
		financialClassChoice.getItems().addAll("Single Folder","Family Folder");
		
	}

	public String getId() {
		return idTextField.getText();
	}
	public  String getRoomChoice() {
		return (String) roomChoice.getSelectionModel().getSelectedItem();
	}
	public String getFinancialClassChoice() {
		return (String) financialClassChoice.getSelectionModel().getSelectedItem();
	}
	public double getTotalDeposit() {
		if(totalDepositTextField.getText().equals(""))
			return 0;
		return Integer.valueOf(totalDepositTextField.getText());
	}
	public double getTotalBill() {
		return Integer.valueOf(totalBillTextField.getText());
	}
	public int getDaysOnAdmission() {
		return Integer.valueOf(daysOnAdmissionTextField.getText());
	}
	@FXML TextField idTextField;
	@FXML ChoiceBox roomChoice;
	@FXML ChoiceBox financialClassChoice;
	@FXML TextField totalDepositTextField;
	@FXML TextField totalBillTextField;
	@FXML TextField daysOnAdmissionTextField;

}
