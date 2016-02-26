/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector.gui;

import cern.molr.inspector.DebugMoleSpawner;
import cern.molr.inspector.controller.StatefulJdiController;
import cern.molr.inspector.domain.Session;
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
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Implementation {@link BorderPane} that shows the source code in a {@link TextFlow} node and allows for the stepping
 * and termination of the execution.
 *
 * @author tiagomr
 * @author mgalilee
 */
public class DebugPane extends BorderPane implements StatefulJdiController.JdiStateObserver {

    //TODO Tests have to be done

    private static final Logger LOGGER = LoggerFactory.getLogger(DebugPane.class);
    private static final DebugMoleSpawner DEBUG_MOLE_SPAWNER = new DebugMoleSpawner();

    private final String missionContentClassName;

    private final Session session;
    private int currentLine = 0;

    /* UI Components */
    private final ScrollPane scrollPane = new ScrollPane();
    private final TextFlow textFlow = new TextFlow();
    private final Button stepOverButton = new Button("Step Over");
    private final Button resumeButton = new Button("Resume");
    private final Button terminateButton = new Button("Terminate");
    private final CheckBox scrollCheckBox = new CheckBox("Automatic Scroll");

    public DebugPane(Session session) {
        super();
        this.session = session;

        initUI();
        missionContentClassName = session.getMission().getMissionContentClassName();
        initData(missionContentClassName);
        session.getController().addObserver(this);
        session.getController()
                .getLastKnownState()
                .ifPresent(this::onLocationChange);

        stepOverButton.setDisable(!canStep());
        resumeButton.setDisable(!canStep());
        terminateButton.setDisable(session.getController().isDead());

        addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            LOGGER.info("WINDOW_CLOSE_REQUEST");
            session.getController().removeObserver(this);
        });
    }

    private boolean canStep() {
        return !session.getController().isDead() && session.getController().getLastKnownState().isPresent();
    }

    private boolean isMissionContentClass(String className) {
        String properClassName = className.replaceFirst("\\.java", "").replaceAll("/", ".");
        return missionContentClassName.equals(properClassName);
    }

    @Override
    public void entryStateChanged() {
        Platform.runLater(() -> {
            stepOverButton.setDisable(!canStep());
            resumeButton.setDisable(!canStep());
        });
        session.getController()
                .getLastKnownState()
                .ifPresent(this::onLocationChange);
    }

    private void onLocationChange(EntryState state) {
        LOGGER.info("onLocationChange {}", state);
        if (isMissionContentClass(state.getClassName())) {
            Platform.runLater(() -> {
                setCurrentLine(state.getLine());
            });
            LOGGER.info("in class {}, stepping available", missionContentClassName);
        } else {
            LOGGER.info("out of class {}, resuming", missionContentClassName);
            session.getController().resume();
        }
    }

    @Override
    public void death() {
        LOGGER.info("onVmDeath received");
        Platform.runLater(() -> {
            stepOverButton.setDisable(!canStep());
            resumeButton.setDisable(!canStep());
            terminateButton.setDisable(session.getController().isDead());
        });
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
            textFlow.getChildren().add(text);
        });
    }

    private void initUI() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        stepOverButton.setOnMouseClicked(event -> {
            session.getController().stepForward();
            stepOverButton.setDisable(!canStep());
            resumeButton.setDisable(!canStep());
        });
        hBox.getChildren().add(stepOverButton);
        resumeButton.setOnMouseClicked(event -> {
            session.getController().resume();
            stepOverButton.setDisable(!canStep());
            resumeButton.setDisable(!canStep());
        });
        hBox.getChildren().add(resumeButton);
        terminateButton.setOnMouseClicked(event -> terminateAction());
        hBox.getChildren().add(terminateButton);
        hBox.getChildren().add(scrollCheckBox);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(15, 12, 15, 12));
        scrollPane.setContent(textFlow);
        setCenter(scrollPane);
        setBottom(hBox);
        setPrefSize(700, 400);
    }

    private void terminateAction() {
        session.getController().terminate();
        stepOverButton.setDisable(!canStep());
        resumeButton.setDisable(!canStep());
    }
}