package com.loop81.fxcompare;

/*
 * Copyright (c) 2013 http://www.loop81.com
 *
 * See the file license.txt for copying permission.
 */

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import com.loop81.fxcompare.utils.AbstractDialogWindow;
import com.loop81.fxcompare.utils.MessageBundle;

/**
 * The alert dialog will create a pop-up window with a title telling the user that something went wrong. In the dialog
 * will be a text describing the error and a button to click "OK".
 * 
 * <p>The layout of the dialog is loaded from <i>fxml/dialog.fxml</i>.</p>
 * 
 * @author Allitico
 */
public class AlertDialog extends AbstractDialogWindow {

	private static final int DIALOG_HEIGHT = 120;
	
	private static final int DIALOG_WIDTH = 300;
	
	public AlertDialog(String message) {
		super(DIALOG_WIDTH, DIALOG_HEIGHT);
		
		dialogStage.setTitle(MessageBundle.getString("dialog.title"));
		
		// Show the message.
		((Label) dialogStage.getScene().lookup("#errorText")).setText(message);
		
		// Handle the click on the OK-button, simply close the dialog.
		((Button) dialogStage.getScene().lookup(".button")).setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dialogStage.close();
				event.consume();
			}
		});
	}
	
	@Override
	protected String getFXMLResourcePath() {
		return "/fxml/dialog.fxml";
	}
}
