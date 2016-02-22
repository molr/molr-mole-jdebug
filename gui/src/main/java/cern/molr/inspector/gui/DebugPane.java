package cern.molr.inspector.gui;

import cern.molr.commons.domain.Mission;
import cern.molr.inspector.DebugMoleSpawner;
import cern.molr.inspector.controller.JdiController;
import cern.molr.inspector.entry.EntryListener;
import cern.molr.inspector.entry.EntryState;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Implementation {@link BorderPane} that shows the source code in a {@link TextFlow} node and allows for the stepping
 * and termination of the execution.
 *
 * @author tiagomr
 * @author mgalilee
 */
public class DebugPane extends BorderPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugPane.class);
    private final static DebugMoleSpawner DEBUG_MOLE_SPAWNER = new DebugMoleSpawner();

    private final JdiController jdiController;
    private int currentLine = 0;
    private Optional<Consumer<DebugPane>> onTerminateListener = Optional.empty();

    /* UI Components */
    private final ScrollPane scrollPane = new ScrollPane();
    private final TextFlow textFlow = new TextFlow();
    private final Button stepOverButton = new Button("Step Over");
    private final Button terminateButton = new Button("Terminate");
    private final CheckBox scrollCheckBox = new CheckBox("Automatic Scroll");

    public DebugPane(Mission mission) throws Exception {
        super();

        if (mission == null) {
            throw new IllegalArgumentException("The mission must not be null");
        }
        initUI();
        jdiController = DEBUG_MOLE_SPAWNER.spawnMoleRunner(mission);
        jdiController.setEntryListener(new EntryListener() {
            @Override
            public void onLocationChange(EntryState state) {
                LOGGER.info("onLocationChange {}", state);
                setCurrentLine(state.getLine());
            }

            @Override
            public void onInspectionEnd(EntryState state) {
                LOGGER.info("onInspectionEnd {}", state);
            }

            @Override
            public void onVmDeath() {
                LOGGER.info("onVmDeath received");
            }
        });
        initData(mission.getMissionContentClassName());
    }

    /**
     * Swing compatibility builder for DebugPane.
     * Embeds a DebugPane in a JFrame
     * @param mission a {@link Mission} from a {@link cern.molr.commons.mole.MissionsDiscoverer}
     * @return a {@link JFrame}
     * @throws Exception
     */
    public static JFrame openDebugPaneInJFrame(Mission mission) throws Exception {
        JFrame frame = new JFrame(mission.getMissionContentClassName());
        frame.setSize(500, 900);

        JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);

        DebugPane debugPane = new DebugPane(mission);

        Platform.runLater( () -> {
            Scene scene = new Scene(debugPane);
            fxPanel.setScene(scene);
        });

        return frame;
    }

    /**
     * Highlights the line given as a parameter
     *
     * @param lineNumber Line to be highlighted
     */
    public void setCurrentLine(final int lineNumber) {
        if (lineNumber < 1) {
            throw new IllegalArgumentException("Line number must have a positive value");
        }
        if (lineNumber > textFlow.getChildren().size()) {
            throw new IllegalArgumentException("Line number must not be bigger than the existent number of lines");
        }

        Platform.runLater(() -> {
            Text textLine;
            textLine = (Text) textFlow.getChildren().get(currentLine);
            textLine.setFill(Color.BLACK);
            textLine = (Text) textFlow.getChildren().get(lineNumber - 1);
            textLine.setFill(Color.RED);
            currentLine = lineNumber - 1;
            if (scrollCheckBox.isSelected()) {
                scrollPane(textLine);
            }
        });
    }

    private void scrollPane(Text textLine) {
        double yToCenter = textLine.getBoundsInParent().getMaxY() - (textLine.getBoundsInLocal().getHeight() / 2);
        double maximumScroll = textFlow.getBoundsInLocal().getHeight() - scrollPane.getViewportBounds().getHeight();
        double valueToScroll = yToCenter - (scrollPane.getViewportBounds().getHeight() / 2);

        valueToScroll = valueToScroll < 0 ? 0 : valueToScroll;
        valueToScroll = valueToScroll > maximumScroll ? maximumScroll : valueToScroll;
        scrollPane.setVvalue(valueToScroll / maximumScroll);
    }

    private void initData(String className) {
        String sourceCodeText = DEBUG_MOLE_SPAWNER.getSource(className);
        Arrays.asList(sourceCodeText.split("\n")).forEach(line -> {
            Text text = new Text(line + "\n");
            text.setOnMouseClicked(event -> setCurrentLine(textFlow.getChildren().indexOf(text) + 1));
            textFlow.getChildren().add(text);
        });
    }

    private void initUI() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        stepOverButton.setOnMouseClicked(event -> jdiController.stepForward());
        hBox.getChildren().add(stepOverButton);
        terminateButton.setOnMouseClicked(event -> {
            jdiController.terminate();
            onTerminateListener.ifPresent(listener -> listener.accept(this));
        });
        hBox.getChildren().add(terminateButton);
        hBox.getChildren().add(scrollCheckBox);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(15, 12, 15, 12));
        scrollPane.setContent(textFlow);
        setCenter(scrollPane);
        setBottom(hBox);
        setPrefSize(700, 400);
    }

    /* For testing */
    TextFlow getTextFlow() {
        return textFlow;
    }

    public void setOnTerminate(Consumer<DebugPane> consumer) {
        onTerminateListener = Optional.ofNullable(consumer);
    }
}