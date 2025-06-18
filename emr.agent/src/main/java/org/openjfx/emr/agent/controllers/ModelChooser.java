package org.openjfx.emr.agent.controllers;

import org.openjfx.emr.agent.utilities.ViewsFxmls;

import javafx.fxml.FXML;

public class ModelChooser {

	@FXML public void switchToSingleDefaultPrediction() {
		ViewSwitcher.switchBaseRootCenter(ViewsFxmls.SINGLE_DEFAULT);
	}

	@FXML public void switchToMultipleDefaultPrediction() {
		ViewSwitcher.switchBaseRootCenter(ViewsFxmls.MULTIPLE_DEFAULT);
	}

	@FXML public void switchToSingleDiagnosisPrediction() {
		ViewSwitcher.switchBaseRootCenter(ViewsFxmls.SINGLE_DIAGNOSIS);
	}

	@FXML public void switchToMultipleDiagnosisPrediction() {
		ViewSwitcher.switchBaseRootCenter(ViewsFxmls.MULTIPLE_DIAGNOSIS);
	}

}
