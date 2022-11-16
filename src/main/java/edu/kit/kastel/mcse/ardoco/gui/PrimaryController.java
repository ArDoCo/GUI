package edu.kit.kastel.mcse.ardoco.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PrimaryController {
    @FXML private Text actionTarget;
    @FXML private TextField nlsadPath;
    @FXML private TextField modelPath;


    @FXML protected void openNlsadFileChooser(ActionEvent event) {
        var textFileChooser = new FileChooser();
        textFileChooser.setTitle("Open NLSAD Text File");
        var file = textFileChooser.showOpenDialog(getStage(event));
        if (file == null) {
            return;
        }
        nlsadPath.setText(file.getPath());
    }

    @FXML protected void openModelFileChooser(ActionEvent event) {
        var modelFileChooser = new FileChooser();
        modelFileChooser.setTitle("Open NLSAD Text File");
        var files = modelFileChooser.showOpenMultipleDialog(getStage(event));
        if (files == null) {
            return;
        }

        String fileNames = files.stream().map(File::getPath).collect(Collectors.joining(";"));
        modelPath.setText(fileNames);
    }

    private static Window getStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        return source.getScene().getWindow();
    }

    @FXML protected void runArDoCo(ActionEvent event) {
        var nlsadPathText = nlsadPath.getText();
        var modelPathText = modelPath.getText();

        if (nlsadPathText.isBlank() || modelPathText.isBlank()) {
            actionTarget.setText("Error!");
            return;
        }

        var nlsadFile = new File(nlsadPathText);
        if (!isFileValid(nlsadFile)) {
            return;
        }

        var modelPaths = modelPathText.split(";");
        List<File> modelFiles = new ArrayList<>();
        for (var modelPath : modelPaths) {
            var modelFile = new File(modelPath);
            if (!isFileValid(modelFile)) {
                return;
            }
            modelFiles.add(modelFile);
        }

        actionTarget.setText("Run");
    }

    private boolean isFileValid(File nlsadFile) {
        if (!nlsadFile.exists() || !nlsadFile.isFile()) {
            actionTarget.setText("Error! Cannot load " + nlsadFile.getPath());
            return false;
        }
        return true;
    }

}
