package com.loop81.fxcompare;

/*
 * Copyright (c) 2013 http://www.loop81.com
 *
 * See the file license.txt for copying permission.
 */

import java.util.ResourceBundle;

import com.loop81.fxcompare.utils.MessageBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class for the whole application. Will setup the {@link MessageBundle} and initiate the application by loading
 * the FXML which is controlled by {@link FXCompareController}.
 * 
 * @author Allitico
 */
public class FXCompare extends Application {
	
	public static final String RESOURCE_TEXTS = "text.fx_compare"; 

	private static Stage mainStage;
	
	@Override
	public void start(Stage stage) throws Exception {
		FXCompare.mainStage = stage;
		
		// Load the message bundle.
		ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE_TEXTS);
		MessageBundle.initialize(resourceBundle);
		
		stage.setTitle(MessageBundle.getString("application.title"));
		stage.setScene(new Scene(FXMLLoader.<Parent>load(getClass().getResource("/fxml/layout.fxml"), resourceBundle)));
		stage.getScene().getStylesheets().add("styles/style.css");
		stage.show();
	}
	
	/** 
	 * Return the main stage of the application. Used within the dialog to position the dialogs in the center of 
	 * the stage. 
	 */
	public static Stage getMainStage() {
		return mainStage;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
