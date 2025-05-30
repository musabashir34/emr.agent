package org.openjfx.emr.agent.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;

public class JavaFxHomePageController {



	@FXML public void makequeries() {
		ViewSwitcher.switchBaseRootCenter(ViewsFxmls.PREDICTIONS);
	}

	@FXML public void bill() {
		ViewSwitcher.switchBaseRootCenter(ViewsFxmls.BILLING);
	}

	@FXML public void toHome() {
		ViewSwitcher.switchBaseRootCenter(ViewsFxmls.HOME);
	}

	@FXML public void logout() {
		Scene scene = ViewSwitcher.getScene();
		scene.setRoot(ViewSwitcher.getStartingPageRoot());
		
	}

}
