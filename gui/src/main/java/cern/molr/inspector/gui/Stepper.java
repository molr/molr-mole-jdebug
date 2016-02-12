/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector.gui;

import cern.molr.commons.domain.Mole;
import cern.molr.inspector.gui.rest.ContainerService;
import cern.molr.inspector.gui.rest.ContainerServices;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Optional;

/**
 * A user interface that can list and run {@link Mole}s. The containers are known from a remote HTTP REST
 * endpoint.
 */
public class Stepper extends Application {

    public static final String STEPPER_CSS = "stepper.css";

    //    private static Inspector inspector;
    private static final ContainerServices containers = ContainerServices.ofBaseUrl("http://localhost:8080/jarrace/");

    /**
     * Closes the JavaFX platform.
     */
    public static void close() {
        Platform.runLater(Platform::exit);
    }

    /**
     * Runs the JavaFX application.
     *
     * @param args Arguments sent to the application.
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ContainerList containerList = new ContainerList(containers.createObservable(ContainerService::getContainers));

        containerList.setPrefSize(700, 400);

        Button startButton = new Button("Start Service");
        startButton.setOnMouseClicked(eventHandler -> {
            Optional<ContainerList.EntryPoint> selectedEntryPoint = containerList.getSelectedEntryPoint();
            if (selectedEntryPoint.isPresent()) {
                startService(selectedEntryPoint.get());
            }
        });

        Button debugButton = new Button("Debug Service");
        debugButton.setOnMouseClicked(eventHandler -> {
            Optional<ContainerList.EntryPoint> selectedEntryPoint = containerList.getSelectedEntryPoint();

            if (selectedEntryPoint.isPresent()) {
                try {
                    String response = containers.getTextService().readClass(selectedEntryPoint.get().getName(),
                            selectedEntryPoint.get().getClazz()).execute().body();
                    Stage stage = new Stage();
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initStyle(StageStyle.DECORATED);
                    stage.setTitle("Debug");
                    //DebugPane debugPane = new DebugPane(response);
                    //debugPane.setPrefSize(700, 400);
                    //stage.setScene(new Scene(debugPane, 700, 400));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Button exitButton = new Button("Exit");
        exitButton.setOnMouseClicked(event -> Stepper.close());

        FlowPane rootPane = new FlowPane();
        rootPane.getChildren().add(containerList);
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(15, 12, 15, 12));
        hBox.getChildren().add(startButton);
        hBox.getChildren().add(debugButton);
        hBox.getChildren().add(exitButton);
        rootPane.getChildren().add(hBox);
        Scene scene = new Scene(rootPane);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> Stepper.close());
        primaryStage.show();
        primaryStage.setTitle("Stepper");
    }

    private void startService(ContainerList.EntryPoint entryPoint) {
        System.out.println("Running " + entryPoint);
        try {
            final String response = containers.getTextService()
                    .startEntry(entryPoint.getName(), entryPoint.getClazz())
                    .execute().body();
            System.out.println("Received response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
