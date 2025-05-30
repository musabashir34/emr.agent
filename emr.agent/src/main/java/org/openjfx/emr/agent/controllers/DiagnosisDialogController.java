package org.openjfx.emr.agent.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

public class DiagnosisDialogController {

	public String getId() {
		return idTextField.getText();
	}
	public String getBillItem() {
		return billItemTextArea.getText();
	}
	@FXML TextField idTextField;
	@FXML TextArea billItemTextArea;

}
