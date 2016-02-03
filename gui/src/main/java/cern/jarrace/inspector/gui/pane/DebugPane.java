package cern.jarrace.inspector.gui.pane;

import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Arrays;

/**
 * Class that extends {@link ScrollPane} and contains a single {@link TextFlow} component for showing rich text
 *
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