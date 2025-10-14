package com.loop81.fxcomparer;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;

/**
 * Dialog for displaying textual differences between two files using diff_match_patch.
 */
public class DiffViewerDialog {

    private Stage dialogStage;
    private String file1Content;
    private String file2Content;
    private String fileName;

    public DiffViewerDialog() {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(FXComparer.getMainStage());
        dialogStage.setTitle("Text Diff Viewer");
        dialogStage.setWidth(800);
        dialogStage.setHeight(600);
    }

    /**
     * Show the diff dialog between two files
     */
    public void showDiff(File file1, File file2, String fileName) {
        this.fileName = fileName;
        
        try {
            // Read file contents
            this.file1Content = readFileContent(file1);
            this.file2Content = readFileContent(file2);
            
            createAndShowDialog();
        } catch (IOException e) {
            new AlertDialog("Could not read files for diff comparison: " + e.getMessage()).show();
        }
    }

    private String readFileContent(File file) throws IOException {
        return Files.readString(file.toPath());
    }

    private void createAndShowDialog() {
        BorderPane root = new BorderPane();

        // Create header
        Label headerLabel = new Label("Differences for: " + fileName);
        headerLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px;");
        root.setTop(headerLabel);

        // Create diff text area
        TextArea diffTextArea = new TextArea();
        diffTextArea.setEditable(false);
        diffTextArea.setWrapText(true);
        
        // Generate and format diff
        String formattedDiff = generateFormattedDiff();
        diffTextArea.setText(formattedDiff);

        ScrollPane scrollPane = new ScrollPane(diffTextArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        root.setCenter(scrollPane);

        // Create button area
        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> dialogStage.close());
        
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.getChildren().add(closeButton);
        root.setBottom(buttonBox);

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private String generateFormattedDiff() {
        diff_match_patch dmp = new diff_match_patch();
        LinkedList<Diff> diffs = dmp.diff_main(file1Content, file2Content);
        dmp.diff_cleanupSemantic(diffs);

        StringBuilder result = new StringBuilder();
        result.append("Text differences between files:\n\n");

        for (Diff diff : diffs) {
            String text = diff.text.replace("\t", "    "); // Replace tabs with 4 spaces
            switch (diff.operation) {
                case INSERT:
                    result.append("(+) Added: ")
                          .append(text)
                          .append("\n---\n");
                    break;
                case DELETE:
                    result.append("(-) Removed: ")
                          .append(text)
                          .append("\n---\n");
                    break;
                case EQUAL:
                    // For equal parts, just add a brief mention if the text is short
                    if (text.length() < 100) {
                        result.append("  (Unchanged: ")
                              .append(text.length() > 50 ? text.substring(0, 50) + "..." : text)
                              .append(")\n");
                    } else {
                        result.append("  (Unchanged content - ")
                              .append(text.length())
                              .append(" characters)\n");
                    }
                    break;
            }
        }

        return result.toString();
    }
}