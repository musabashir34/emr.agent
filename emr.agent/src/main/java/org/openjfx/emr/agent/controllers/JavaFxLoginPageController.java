package org.openjfx.emr.agent.controllers;

import java.io.IOException;

import org.openjfx.emr.agent.scrapers.FBSBaseScraper;
import org.openjfx.emr.agent.scrapers.RelianceHmoBillSubmitter;
import org.openjfx.emr.agent.utilities.Broadcaster;
import org.openjfx.emr.agent.utilities.Event;
import org.openjfx.emr.agent.utilities.UserLoginDetails;
import org.openjfx.emr.agent.utilities.ViewsFxmls;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;

public class JavaFxLoginPageController {
	public UserLoginDetails userlogindetails;
	private Broadcaster broadcaster;
	private BorderPane baseRoot;
	private Service<Void> loginService = new Service<>(){

		@Override
		protected Task<Void> createTask() {
			// TODO Auto-generated method stub
			return new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					FBSBaseScraper baseScraper = new FBSBaseScraper();
					baseScraper.signIn(userlogindetails);
					baseScraper.closeBrowser();
					return null;
				}
				
			};
		}
		
	};
	@FXML HBox signinProgress;
	@FXML TextField usernameField;
	@FXML PasswordField passwordField;
	@FXML Button signInButton;
	@FXML Button cancelSignInButton;
	
	private void loadHomePage(String topic) {
		if (userlogindetails.isValidated()){
		Scene scene =	ViewSwitcher.getScene();
		scene.setRoot(baseRoot);
		ViewSwitcher.switchBaseRootCenter(ViewsFxmls.HOME);}
		else {
			Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to Log you in. Check your connection of Login details") {
				
			};
			alert.showAndWait();

		}
	}
	@FXML
	public void initialize() {
		try {
			baseRoot = (BorderPane) ViewSwitcher.loadFXML(ViewsFxmls.BASE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ViewSwitcher.setBaseRootCenter(baseRoot);
		signinProgress.visibleProperty().bind(loginService.runningProperty());
		broadcaster = Broadcaster.getInstance();
		broadcaster.subscribe(Event.FBS_WEBPAGE_LOGIN, this, this::loadHomePage);
	}
	

	@FXML public void signIn() {
		/*
		 * userlogindetails = new UserLoginDetails();
		 * userlogindetails.setUserName(usernameField.getText());
		 * userlogindetails.setPassWord(passwordField.getText());
		 * loginService.restart();
		 */
		Scene scene =	ViewSwitcher.getScene();
		scene.setRoot(baseRoot);
		ViewSwitcher.switchBaseRootCenter(ViewsFxmls.HOME);
		
	}
	
	

}
