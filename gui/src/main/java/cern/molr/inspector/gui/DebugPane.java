package cern.molr.inspector.gui;

import cern.molr.commons.domain.Mission;
import cern.molr.commons.mole.Registry;
import cern.molr.inspector.DebugMoleSpawner;
import cern.molr.inspector.domain.Session;
import cern.molr.inspector.entry.EntryListener;
import cern.molr.inspector.entry.EntryState;
import javafx.application.Platform;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

    //TODO Tests have to be redone

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugPane.class);
    private static final DebugMoleSpawner DEBUG_MOLE_SPAWNER = new DebugMoleSpawner();

    private final Session session;
    private final Registry<Session> sessionRegistry;
    private int currentLine = 0;
    private Optional<Consumer<DebugPane>> onTerminateListener = Optional.empty();

    /* UI Components */
    private final ScrollPane scrollPane = new ScrollPane();
    private final TextFlow textFlow = new TextFlow();
    private final Button stepOverButton = new Button("Step Over");
    private final Button terminateButton = new Button("Terminate");
    private final CheckBox scrollCheckBox = new CheckBox("Automatic Scroll");

    public DebugPane(Session session, Registry<Session> sessionRegistry) {
        super();
        this.sessionRegistry = sessionRegistry;
        this.session = session;
        sessionRegistry.registerEntry(session);
        initUI();
        initData(session.getMission().getMissionContentClassName());
        session.getController().setEntryListener(new EntryListener() {
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
    }

    public DebugPane(Mission mission, Registry<Session> sessionRegistry) throws IOException {
        this(DEBUG_MOLE_SPAWNER.spawnMoleRunner(mission), sessionRegistry);
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
        stepOverButton.setOnMouseClicked(event -> session.getController().stepForward());
        hBox.getChildren().add(stepOverButton);
        terminateButton.setOnMouseClicked(event -> {
            session.getController().terminate();
            onTerminateListener.ifPresent(listener -> {
                sessionRegistry.removeEntry(session);
                listener.accept(this);
            });
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