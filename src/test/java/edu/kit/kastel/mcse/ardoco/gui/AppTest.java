/* Licensed under MIT 2022. */
package edu.kit.kastel.mcse.ardoco.gui;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextMatchers.hasText;

import java.awt.*;
import java.io.IOException;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

@Disabled
class AppTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws IOException {
        new App().start(stage);

    }

    @Test
    void emptyInputs() {
        clickOn("#runButton");
        verifyThat("#actionTarget", hasText("Error! There are empty fields."));
    }

    @Test
    void invalidNlsad() {
        clickOn("#nameTextField").write("Test");
        clickOn("#nlsadPath").write("NLSAD");
        clickOn("#modelPath").write("Model");
        clickOn("#modelType");
        type(KeyCode.DOWN);
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#outputDirPath").write("Output");
        clickOn("#runButton");
        verifyThat("#actionTarget", hasText("Error! Cannot load NLSAD"));
    }

    @Test
    void invalidModel() {
        clickOn("#nameTextField").write("Test");
        clickOn("#nlsadPath").write("./src/test/resources/teastore.txt", 1);
        clickOn("#modelPath").write("Model");
        clickOn("#modelType");
        type(KeyCode.DOWN);
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#outputDirPath").write("Output");
        clickOn("#runButton");
        verifyThat("#actionTarget", hasText("Error! Cannot load Model"));
    }

    @Test
    void invalidOutput() {
        clickOn("#nameTextField").write("Test");
        clickOn("#nlsadPath").write("./src/test/resources/teastore.txt", 1);
        clickOn("#modelPath").write("./src/test/resources/teastore.repository", 1);
        clickOn("#modelType");
        type(KeyCode.DOWN);
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#outputDirPath").write("Output");
        clickOn("#runButton");
        verifyThat("#actionTarget", hasText("Error! Cannot find output directory."));
    }

}
