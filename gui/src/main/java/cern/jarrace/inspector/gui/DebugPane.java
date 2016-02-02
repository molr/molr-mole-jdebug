package cern.jarrace.inspector.gui;

import cern.jarrace.inspector.gui.rest.Services;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import retrofit2.Response;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author tiagomr
 */
public class DebugPane extends ScrollPane {

    private final TextFlow textFlow = new TextFlow();
    private int currentLine = 0;

    public DebugPane(String sourceCodeText) {
        super();
        Arrays.asList(sourceCodeText.split("\n")).forEach(line -> {
            Text text = new Text(line + "\n");
            text.setOnMouseClicked(event -> setCurrentLine(textFlow.getChildren().indexOf(text)));
            textFlow.getChildren().add(text);
        });
        setContent(textFlow);
    }

    public void setCurrentLine(int lineNumber) {
        Text textLine = (Text) textFlow.getChildren().get(currentLine);
        textLine.setFill(Color.BLACK);
        textLine = (Text) textFlow.getChildren().get(lineNumber);
        textLine.setFill(Color.RED);
        currentLine = lineNumber;
    }
}