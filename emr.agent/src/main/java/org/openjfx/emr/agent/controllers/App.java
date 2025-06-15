package org.openjfx.emr.agent.controllers;

import java.io.IOException;

import org.openjfx.emr.agent.scrapers.FBSBaseScraper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

	public static Stage stage;

	@Override
	public void start(Stage stage) throws IOException {
		Scene scene = new Scene(ViewSwitcher.getStartingPageRoot());
		scene.getStylesheets().add(App.class.getResource("application.css").toExternalForm());
		ViewSwitcher.setScene(scene);
		stage.setScene(scene);
		stage.setTitle("EMR Agent");
		Image image = (new Image(App.class.getResource("iconimage.png").toExternalForm()));
		stage.getIcons().add(image);
		stage.show();

	}


	public static void main(String[] args) {
		//FBSBaseScraper scraper = new FBSBaseScraper();
		launch();

	}

}