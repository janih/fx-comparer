package com.loop81.fxcomparer;

/*
 * Copyright (c) 2013 http://www.loop81.com
 *
 * See the file license.txt for copying permission.
 */

import java.io.IOException;

import javafx.scene.control.TextArea;

import org.apache.commons.io.IOUtils;

import com.loop81.fxcomparer.utils.AbstractDialogWindow;

/**
 * The about dialog will show a dialog with some build information and the license for the applicaiton. The information
 * is loaded from about.txt and license.txt.
 *
 * <p>The layout of the dialog is loaded from <i>fxml/about.fxml</i>.</p>
 * 
 * @author Allitico
 */
public class AboutDialog extends AbstractDialogWindow {

	private static final int DIALOG_WIDTH = 600;
	
	private static final int DIALOG_HEIGHT = 371; 
	
	protected AboutDialog() {
		super(DIALOG_WIDTH, DIALOG_HEIGHT);
		
		try {
			String aboutText = IOUtils.toString(getClass().getResourceAsStream("/about.txt"), "UTF-8"); 
			aboutText += IOUtils.toString(getClass().getResourceAsStream("/license.txt"), "UTF-8");  
			((TextArea) dialogStage.getScene().lookup(".text-area")).setText(aboutText);
		} catch (IOException e) {
			// Will never happen since the about.txt and license.txt should always be there.
		}
	}

	@Override
	protected String getFXMLResourcePath() {
		return "/fxml/about.fxml";
	}
}
