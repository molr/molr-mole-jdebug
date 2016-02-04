/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.gui;

import cern.jarrace.commons.domain.AgentContainer;
import cern.jarrace.inspector.gui.rest.ContainerService;
import cern.jarrace.inspector.gui.rest.ContainerServices;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 * A user interface that can list and run {@link AgentContainer}s. The containers are known from a remote HTTP REST
 * endpoint.
 */
public class Stepper extends Application {

    public static final String STEPPER_CSS = "stepper.css";

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

        FlowPane rootPane = new FlowPane();
        rootPane.setPrefSize(900, 400);
        rootPane.getChildren().add(containerList);
        Scene scene = new Scene(rootPane, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> Stepper.close());
        primaryStage.show();
        primaryStage.setTitle("Stepper");
    }

}
