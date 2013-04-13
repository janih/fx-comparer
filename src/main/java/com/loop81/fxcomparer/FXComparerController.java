package com.loop81.fxcomparer;

/*
 * Copyright (c) 2013 http://www.loop81.com
 *
 * See the file license.txt for copying permission.
 */

import java.util.Comparator;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import javafx.util.Callback;

import org.apache.commons.io.FileUtils;

import com.loop81.fxcomparer.comparer.ArchiveException;
import com.loop81.fxcomparer.comparer.ComparableArchive;
import com.loop81.fxcomparer.comparer.Comparer;
import com.loop81.fxcomparer.comparer.ComparisonResult;
import com.loop81.fxcomparer.comparer.ComparisonResult.ComparisonEntry;
import com.loop81.fxcomparer.utils.MessageBundle;
import com.loop81.fxcomparer.utils.VersionProvider;

/**
 * The {@link FXComparerController} is defined as the controller in the FXML <i>/src/main/resources/fxml/layout.fxml</i> 
 * and control the layout of the application and all interaction with the users. 
 * 
 * @author Allitico
 */
public class FXComparerController {
	
	@FXML 
	private AnchorPane anchorPaneMain;
		
	@FXML 
	private Label labelVersion;
	
	@FXML
	private TextField textFieldArchive1;
	
	@FXML
	private Label labelArchive1Info;
	
	@FXML
	private TextField textFieldArchive2;
	
	@FXML
	private Label labelArchive2Info;
	
	@FXML
	private TableView<ComparisonEntry> compareTable;
	
	@FXML
	private TableColumn<ComparisonEntry, String> columnName;
	
	@FXML
	private TableColumn<ComparisonEntry, String> columnState;
	
	@FXML
	private TableColumn<ComparisonEntry, ChangeWrapper> columnChange;
	
	@FXML
	private Label labelCompareResult;
	
	@FXML
	private AnchorPane anchorPaneDropFile1;
	
	@FXML 
	private AnchorPane anchorPaneDropFile2;
	
	/** Provider used to load the version number into "labelVersion". */
	private final VersionProvider versionProvider = new VersionProvider();
	
	private final Comparer comparer = new Comparer();
	
	private ComparableArchive archive1;
	private ComparableArchive archive2;
	
	public void initialize() {
		labelVersion.setText(versionProvider.getVersion());
		initiateTable();
		initiateDragNDropp();
	}
	
	/** 
	 * Initiate the table by setting the correct width of the columns and adding a custom value-factory for the
	 * size column. 
	 */
	private void initiateTable() {
		columnName.prefWidthProperty().bind(compareTable.widthProperty().divide(5).multiply(3));
		columnName.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<ComparisonEntry, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<ComparisonEntry, String> cell) {
				return new SimpleStringProperty(cell.getValue().getOriginalEntry().getEntryName());
			}
		});
		
		columnState.prefWidthProperty().bind(compareTable.widthProperty().divide(5));
		columnState.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<ComparisonEntry,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<ComparisonEntry, String> cell) {
				return new SimpleObjectProperty<>(MessageBundle.getString(
						"result.state." + cell.getValue().getChangeState().toString().toLowerCase()));
			}
		});
		
		columnChange.prefWidthProperty().bind(compareTable.widthProperty().divide(5));
		columnChange.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ComparisonEntry, 
				ChangeWrapper>, ObservableValue<ChangeWrapper>>() {
			@Override
			public ObservableValue<ChangeWrapper> call(CellDataFeatures<ComparisonEntry, ChangeWrapper> cell) {
				long sizeChange = cell.getValue().getSizeChange();
				if (sizeChange == 0) {
					return new SimpleObjectProperty<ChangeWrapper>(new ChangeWrapper());
				} else if (sizeChange == Long.MIN_VALUE) { 
					return new SimpleObjectProperty<ChangeWrapper>(new ChangeWrapper(
							MessageBundle.getString("general.not_avalible"), 0));
				} else {
					if (sizeChange < FileUtils.ONE_KB && sizeChange > -1 * FileUtils.ONE_KB) {
						return new SimpleObjectProperty<ChangeWrapper>(new ChangeWrapper(
								(sizeChange > 0 ? "+" : "") + FileUtils.byteCountToDisplaySize(sizeChange), 
								sizeChange));
					} else {
						return new SimpleObjectProperty<ChangeWrapper>(new ChangeWrapper(
								(sizeChange > 0 ? "+" : "-") 
										+ FileUtils.byteCountToDisplaySize(Math.abs(sizeChange)) 
										+ " (" + sizeChange + " " + MessageBundle.getString("general.bytes") + ")", 
								sizeChange));
					}
				}
			}
		});
		columnChange.setComparator(new Comparator<ChangeWrapper>() {
			@Override
			public int compare(ChangeWrapper wrapper1, ChangeWrapper wrapper2) {
				return wrapper1.compareTo(wrapper2);
			}
		});
	}
	
	/** Enabled drag & drop support on the anchor-panels for the file selections. */
	private void initiateDragNDropp() {
		anchorPaneDropFile1.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().hasFiles() && comparer.isFileSupported(event.getDragboard().getFiles().get(0))) {
					event.acceptTransferModes(TransferMode.COPY);
				} else {
					event.consume();
				}
			}
		});
		
		anchorPaneDropFile1.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().hasFiles() && comparer.isFileSupported(event.getDragboard().getFiles().get(0))) {
					try {
						archive1 = new ComparableArchive(event.getDragboard().getFiles().get(0));
						handleFile(archive1, textFieldArchive1, labelArchive1Info);
					} catch (ArchiveException e) {
						new AlertDialog(MessageBundle.getString("exceptions.archive.open")).show();
					}
				}
			}
		});
		
		anchorPaneDropFile2.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().hasFiles() && comparer.isFileSupported(event.getDragboard().getFiles().get(0))) {
					event.acceptTransferModes(TransferMode.COPY);
				} else {
					event.consume();
				}
			}
		});
		
		anchorPaneDropFile2.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (event.getDragboard().hasFiles() && comparer.isFileSupported(event.getDragboard().getFiles().get(0))) {
					try {
						archive2 = new ComparableArchive(event.getDragboard().getFiles().get(0));
						handleFile(archive2, textFieldArchive2, labelArchive2Info);
					} catch (ArchiveException e) {
						new AlertDialog(MessageBundle.getString("exceptions.archive.open")).show();
					}
				}
			}
		});
	}
	
	/** Called from the "Exit" menu item, will close the application. */
	@FXML
	protected void onMenuExit(ActionEvent event) {
		event.consume();
		Platform.exit();
	}
	
	/** 
	 * Called from the "Clear" menu item. Trigger a clear of the current select files and resets the application
	 * to a initial state. 
	 */
	@FXML
	protected void onMenuClear(ActionEvent event) {
		event.consume();
		reset();
	}
	
	/** Called from the "About" menu item. Will show the about dialog. */
	@FXML
	protected void onMenuAbout(ActionEvent event) {
		event.consume();
		new AboutDialog().show();
	}
	
	@FXML
	protected void onSelectFile1(ActionEvent event) {
		event.consume();
		archive1 = selectFile(((Node) event.getSource()).getScene().getWindow());
		handleFile(archive1, textFieldArchive1, labelArchive1Info);
	}
 	
	@FXML
	protected void onSelectFile2(ActionEvent event) {
		event.consume();
		archive2 = selectFile(((Node) event.getSource()).getScene().getWindow());
		handleFile(archive2, textFieldArchive2, labelArchive2Info);
	}

	private void reset() {
		archive1 = null;
		archive2 = null;
		textFieldArchive1.setText("");
		textFieldArchive2.setText("");
		labelArchive1Info.setText("");
		labelArchive2Info.setText("");
		labelCompareResult.setText("");
		compareTable.getItems().clear();
	}
	
	private void handleFile(ComparableArchive archive, TextField filePath, Label infoLabel) {
		if (archive != null) {
			filePath.setText(archive.getPath());
			infoLabel.setText(MessageBundle.getString("file.info",
					FileUtils.byteCountToDisplaySize(archive.getSize()), archive.getSize()));
			
			if (archive1 != null && archive2 != null) {
				initCompare();
			}
		}
	}
	
	private ComparableArchive selectFile(Window window) {
		try {
			FileChooser fileChooser = new FileChooser();
			ExtensionFilter filter = new ExtensionFilter(
					MessageBundle.getString("file.selector.text"), "*.zip", "*.jar", "*.war");
			fileChooser.getExtensionFilters().add(filter);
			return new ComparableArchive(fileChooser.showOpenDialog(window));
		} catch (ArchiveException e) {
			new AlertDialog(MessageBundle.getString("exceptions.archive.open")).show();
			return null;
		}
	}
	
	/** 
	 * Run the compare by embedding the call to {@link Comparer#compare(ComparableArchive, ComparableArchive)} into
	 * a {@link Task} since the operation might take some time depending on the client machine and the archives size
	 * and we do not like to hang the UI-thread. 
	 */
	private void initCompare() {
		new Thread(new Task<ComparisonResult>() {
			@Override
			protected ComparisonResult call() throws Exception {
				return comparer.compare(archive1, archive2);
			}
			
			@Override
			protected void succeeded() {
				ComparisonResult result = getValue();
				if (result.isSame()) {
					labelCompareResult.setText(MessageBundle.getString("result.same"));
				} else {
					long diff = archive2.getSize() - archive1.getSize();
					labelCompareResult.setText(MessageBundle.getString("result.different", 
							FileUtils.byteCountToDisplaySize(archive1.getSize()), 
							FileUtils.byteCountToDisplaySize(archive2.getSize()),
							(diff > 0 ? "+" : "") + diff));
				}
				
				compareTable.setItems(FXCollections.observableList(result.getEntries()));
			}
			
			@Override
			protected void failed() {
				new AlertDialog(MessageBundle.getString("exceptions.archive.compare")).show();
				getException().printStackTrace();
			}
		}).start();
	}
	
	/**
	 * Wrapper used to be able to present the difference with the files and support sorting on the acutal 
	 * byte difference.
	 * 
	 * @author Allitico
	 */
	private class ChangeWrapper implements Comparable<ChangeWrapper> {
		private final String sizeText;
		private final long sizeChange;

		public ChangeWrapper() {
			sizeText = "";
			sizeChange = Long.MIN_VALUE;
		}
		
		public ChangeWrapper(String sizeText, long sizeChange) {
			this.sizeText = sizeText;
			this.sizeChange = sizeChange;
		}
		
		@Override
		public String toString() {
			return sizeText;
		}

		@Override
		public int compareTo(ChangeWrapper otherWraper) {
			return Long.compare(otherWraper.sizeChange, sizeChange);
		}
	}
}
