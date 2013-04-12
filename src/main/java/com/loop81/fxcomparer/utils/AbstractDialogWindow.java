package com.loop81.fxcomparer.utils;

/*
 * Copyright (c) 2013 http://www.loop81.com
 *
 * See the file license.txt for copying permission.
 */

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.loop81.fxcomparer.FXComparer;

/**
 * The base for all dialogs within the application. When the dialog is created a basic stage centered in
 * the application is created. The dialog is shown calling {@link AbstractDialogWindow#show()}. 
 * 
 * @author Allitico
 */
public abstract class AbstractDialogWindow {

	/** The stage which the dialog uses to present node within. */
	protected Stage dialogStage;

	protected AbstractDialogWindow(int width, int height) {
		dialogStage = new Stage();
		dialogStage.initOwner(FXComparer.getMainStage());
		dialogStage.initModality(Modality.APPLICATION_MODAL);
		dialogStage.initStyle(StageStyle.UTILITY);
		dialogStage.setResizable(false);
		dialogStage.setIconified(false);
		
		// Set the location of the dialog, using known values. The dialog do not have any dimensions at this stage.
		dialogStage.setX(FXComparer.getMainStage().getX() + FXComparer.getMainStage().getWidth() / 2 - width / 2);
		dialogStage.setY(FXComparer.getMainStage().getY() + FXComparer.getMainStage().getHeight() / 2 - height / 2);
		
		// Load the nodes found within the FXML into the dialogs stage and apply the application styles.
		try {
			Scene scene = new Scene(FXMLLoader.<Parent>load(
					getClass().getResource(getFXMLResourcePath()), MessageBundle.getResourceBundle()));
			scene.getStylesheets().add("styles/style.css");
			dialogStage.setScene(scene);
		} catch (IOException e) {
			throw new RuntimeException("FXML '" + getFXMLResourcePath() + "' not found", e);
		}
	}

	/** Returns the path to the FXML which this dialog should use to create its layout. */
	protected abstract String getFXMLResourcePath();
	
	public void show() {
		dialogStage.show();
	}
}
