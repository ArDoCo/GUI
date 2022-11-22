/* Licensed under MIT 2022. */
package edu.kit.kastel.mcse.ardoco.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import edu.kit.kastel.mcse.ardoco.core.pipeline.ArDoCoRunner;
import edu.kit.kastel.mcse.ardoco.core.pipeline.ArchitectureModelType;

public class PrimaryController {

    @FXML
    private TextField nameTextField;
    @FXML
    private TextField nlsadPath;
    @FXML
    private TextField modelPath;
    @FXML
    private TextField additionalConfigPath;
    @FXML
    private TextField outputDirPath;
    @FXML
    private ComboBox<String> modelType;

    @FXML
    private Button runButton;
    @FXML
    private Text actionTarget;

    @FXML
    protected void openNlsadFileChooser(ActionEvent event) {
        var textFileChooser = new FileChooser();
        textFileChooser.setTitle("Open NLSAD Text File");
        var file = textFileChooser.showOpenDialog(getStage(event));
        if (file == null) {
            return;
        }
        nlsadPath.setText(file.getPath());
    }

    @FXML
    protected void openModelFileChooser(ActionEvent event) {
        var modelFileChooser = new FileChooser();
        modelFileChooser.setTitle("Open NLSAD Text File");
        var files = modelFileChooser.showOpenMultipleDialog(getStage(event));
        if (files == null) {
            return;
        }

        String fileNames = files.stream().map(File::getPath).collect(Collectors.joining(";"));
        modelPath.setText(fileNames);
    }

    public void openAdditionalConfigFileChooser(ActionEvent event) {
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Open Additional Configurations File");
        var file = fileChooser.showOpenDialog(getStage(event));
        if (file == null) {
            return;
        }
        additionalConfigPath.setText(file.getPath());
    }

    public void openOutputDirChooser(ActionEvent event) {
        var directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Output Directory");
        var file = directoryChooser.showDialog(getStage(event));
        if (file == null) {
            return;
        }
        outputDirPath.setText(file.getPath());
    }

    private static Window getStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        return source.getScene().getWindow();
    }

    @FXML
    protected void runArDoCo(ActionEvent event) {
        var nlsadPathText = nlsadPath.getText();
        var modelPathText = modelPath.getText();
        var nameText = nameTextField.getText();
        var modelTypeText = modelType.getValue();
        var additionalConfigText = additionalConfigPath.getText();
        var outputDirText = outputDirPath.getText();

        if (nlsadPathText.isBlank() || modelPathText.isBlank() || nameText.isBlank() || outputDirText.isBlank()) {
            actionTarget.setText("Error! There are empty fields.");
            return;
        }

        var nlsadFile = new File(nlsadPathText);
        if (!isFileValid(nlsadFile)) {
            return;
        }

        var modelPaths = modelPathText.split(";");
        List<File> modelFiles = new ArrayList<>();
        for (var currentModelPath : modelPaths) {
            var modelFile = new File(currentModelPath);
            if (!isFileValid(modelFile)) {
                return;
            }
            modelFiles.add(modelFile);
        }

        File additionalConfigFile = null;
        if (!additionalConfigText.isBlank()) {
            additionalConfigFile = new File(additionalConfigText);
            if (!isFileValid(additionalConfigFile)) {
                return;
            }
        }

        var outputDir = new File(outputDirText);
        if (!outputDir.exists() || !outputDir.isDirectory()) {
            actionTarget.setText("Error! Cannot find output directory.");
            return;
        }

        File finalAdditionalConfigFile = additionalConfigFile;
        Runnable r = () -> startProcessing(nameText, modelTypeText, nlsadFile, modelFiles, finalAdditionalConfigFile, outputDir);

        new Thread(r).start();
    }

    private void startProcessing(String nameText, String modelTypeText, File nlsadFile, List<File> modelFiles, File additionalConfigFile, File outputDir) {
        var runner = createRunner(nameText, nlsadFile, modelFiles.get(0), modelTypeText, outputDir, additionalConfigFile);
        runButton.setDisable(true);
        actionTarget.setText("Starting " + nameText);

        runner.runArDoCo();

        actionTarget.setText("Finished " + nameText);
        runButton.setDisable(false);
    }

    private ArDoCoRunner createRunner(String name, File nlsad, File model, String archType, File outputFolder, File additionalConfig) {
        var architectureModelType = ArchitectureModelType.valueOf(archType);
        var builder = new ArDoCoRunner.Builder(name);
        builder = builder.withInputText(nlsad)
                .withInputModelArchitecture(model)
                .withInputArchitectureModelType(architectureModelType)
                .withOutputDir(outputFolder);
        if (additionalConfig != null) {
            builder = builder.withAdditionalConfigs(additionalConfig);
        }
        return builder.build();
    }

    private boolean isFileValid(File nlsadFile) {
        if (!nlsadFile.exists() || !nlsadFile.isFile()) {
            actionTarget.setText("Error! Cannot load " + nlsadFile.getPath());
            return false;
        }
        return true;
    }

}
