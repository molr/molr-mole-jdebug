package cern.molr.inspector.gui;

import cern.molr.inspector.controller.JdiController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.Arrays;

/**
 * Implementation {@link BorderPane} that shows the source code in a {@link TextFlow} node and allows for the stepping
 * and termination of the execution.
 *
 * @author tiagomr
 */
public class DebugPane extends BorderPane {

    private final JdiController jdiController;
    private final ScrollPane scrollPane = new ScrollPane();
    private final TextFlow textFlow = new TextFlow();
    private final Button stepOverButton = new Button("Step Over");
    private final Button terminateButton = new Button("Terminate");
    private final CheckBox scrollCheckBox = new CheckBox("Automatic Scroll");
    private int currentLine = 0;

    public DebugPane(String sourceCodeText, JdiController jdiController) {
        super();
        this.jdiController = jdiController;
        if (sourceCodeText == null) {
            throw new IllegalArgumentException("Source code text must not be null");
        }
        initUI();
        initData(sourceCodeText);
    }

    /**
     * Highlights the line given as a parameter
     *
     * @param lineNumber Line to be highlighted
     */
    public void setCurrentLine(int lineNumber) {
        if (lineNumber < 1) {
            throw new IllegalArgumentException("Line number must have a positive value");
        }
        if (lineNumber > textFlow.getChildren().size()) {
            throw new IllegalArgumentException("Line number must not be bigger than the existent number of lines");
        }

        lineNumber--;
        Text textLine;
        textLine = (Text) textFlow.getChildren().get(currentLine);
        textLine.setFill(Color.BLACK);
        textLine = (Text) textFlow.getChildren().get(lineNumber - 1);
        textLine.setFill(Color.RED);
        currentLine = lineNumber - 1;
        if (scrollCheckBox.isSelected()) {
            scrollPane(textLine);
        }
    }

    private void scrollPane(Text textLine) {
        double yToCenter = textLine.getBoundsInParent().getMaxY() - (textLine.getBoundsInLocal().getHeight() / 2);
        double maximumScroll = textFlow.getBoundsInLocal().getHeight() - scrollPane.getViewportBounds().getHeight();
        double valueToScroll = yToCenter - (scrollPane.getViewportBounds().getHeight() / 2);

        valueToScroll = valueToScroll < 0 ? 0 : valueToScroll;
        valueToScroll = valueToScroll > maximumScroll ? maximumScroll : valueToScroll;
        scrollPane.setVvalue(valueToScroll / maximumScroll);
    }

    private void initData(String sourceCodeText) {
        if (!sourceCodeText.isEmpty()) {
            Arrays.asList(sourceCodeText.split("\n")).forEach(line -> {
                Text text = new Text(line + "\n");
                text.setOnMouseClicked(event -> setCurrentLine(textFlow.getChildren().indexOf(text) + 1));
                textFlow.getChildren().add(text);
            });
        }
    }

    private void initUI() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        stepOverButton.setOnMouseClicked(event -> jdiController.stepForward());
        hBox.getChildren().add(stepOverButton);
        terminateButton.setOnMouseClicked(event -> {
            jdiController.terminate();
            ((Stage)getScene().getWindow()).close();
        });
        hBox.getChildren().add(terminateButton);
        hBox.getChildren().add(scrollCheckBox);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(15, 12, 15, 12));
        scrollPane.setContent(textFlow);
        setCenter(scrollPane);
        setBottom(hBox);
    }

    /* For testing */
    TextFlow getTextFlow() {
        return textFlow;
    }

    public Button getStepOverButton() {
        return stepOverButton;
    }
    public Button getTerminateButton() {
        return terminateButton;
    }
}