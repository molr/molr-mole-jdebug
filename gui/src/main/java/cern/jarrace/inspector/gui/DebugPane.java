package cern.jarrace.inspector.gui;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.Arrays;

/**
 * @author tiagomr
 */
public class DebugPane extends BorderPane {

    private final ScrollPane scrollPane = new ScrollPane();
    private final TextFlow textFlow = new TextFlow();
    private int currentLine = 0;

    public DebugPane(String sourceCodeText) {
        super();
        initUI();
        initData(sourceCodeText);
    }

    public void setCurrentLine(int lineNumber) {
        Text textLine = (Text) textFlow.getChildren().get(currentLine);
        textLine.setFill(Color.BLACK);
        textLine = (Text) textFlow.getChildren().get(lineNumber);
        textLine.setFill(Color.RED);
        currentLine = lineNumber;
        scrollPane(textLine);
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
        Arrays.asList(sourceCodeText.split("\n")).forEach(line -> {
            Text text = new Text(line + "\n");
            text.setOnMouseClicked(event -> setCurrentLine(textFlow.getChildren().indexOf(text)));
            textFlow.getChildren().add(text);
        });
    }

    private void initUI() {
        HBox hBox = new HBox();
        Button stepOverButton = new Button("StepOver");
        stepOverButton.setOnMouseClicked(event -> setCurrentLine(currentLine + 1));
        hBox.getChildren().add(stepOverButton);
        Button terminateButton = new Button("Terminate");
        terminateButton.setOnMouseClicked(event -> ((Stage) getScene().getWindow()).close());
        hBox.getChildren().add(terminateButton);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(15, 12, 15, 12));
        scrollPane.setContent(textFlow);
        setCenter(scrollPane);
        setBottom(hBox);
    }
}