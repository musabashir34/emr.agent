package org.openjfx.emr.agent.controllers;

import static javafx.scene.layout.BackgroundPosition.CENTER;
import static javafx.scene.layout.BackgroundRepeat.NO_REPEAT;
import static javafx.scene.layout.BackgroundRepeat.REPEAT;
import static javafx.scene.layout.BackgroundSize.DEFAULT;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openjfx.emr.agent.utilities.ViewsFxmls;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;

public class ViewSwitcher {
	private static Map<ViewsFxmls, Parent> cache = new HashMap<>();

	private static Scene scene;
	
	private static BorderPane loginRoot;

	private static BorderPane baseRoot;
	
	public static void setScene(Scene scn) {
		ViewSwitcher.scene = scn;
	}

	public static Scene getScene() {
		return scene;
	}
	
	static void setBaseRootCenter(BorderPane root) {
		baseRoot = root;
	}

	static Parent loadFXML(ViewsFxmls fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(ViewSwitcher.class.getResource(fxml.getFileName() + ".fxml"));
		return fxmlLoader.load();
	}
	
	public static void switchBaseRootCenter(ViewsFxmls view) {
		if (baseRoot == null) {
			System.out.println("No baseRoot was set");
			return;
		}

		try {
			Parent center;

			if (cache.containsKey(view)) {
				System.out.println("Loading from cache");

				center = cache.get(view);
			} else {
				System.out.println("Loading from FXML");

				center = loadFXML(view);

				cache.put(view, center);
			}

			baseRoot.setCenter(center);
			BorderPane.setMargin(center, new Insets(10));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static BorderPane getStartingPageRoot() {
		if(loginRoot ==null) {
		try {

			loginRoot = (BorderPane)loadFXML(ViewsFxmls.LOGIN);
			loginRoot.setPadding(new javafx.geometry.Insets(5, 5, 5, 5));
			Image image = (new Image(ViewSwitcher.class.getResource("loginpage.PNG").toExternalForm()));
			loginRoot.setBackground(new Background(new BackgroundImage(image, REPEAT, NO_REPEAT, CENTER, DEFAULT)));
		} catch (IOException e) {
			e.printStackTrace();
		}}
		return loginRoot;
	}


}
