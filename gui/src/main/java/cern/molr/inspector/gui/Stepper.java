/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector.gui;

import cern.molr.commons.domain.MoleContainer;
import cern.molr.commons.domain.impl.MissionImpl;
import cern.molr.commons.mole.impl.ObservableInMemoryEntriesRegistry;
import cern.molr.inspector.domain.Session;
import cern.molr.inspector.domain.impl.SessionImpl;
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
 * A user interface that can list and run {@link MoleContainer}s. The containers are known from a remote HTTP REST
 * endpoint.
 */
public class Stepper extends Application {
/*
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
        ObservableInMemoryEntriesRegistry registry = new ObservableInMemoryEntriesRegistry();
        Session session = new SessionImpl(new MissionImpl("MoleClass", "MissionContentClass"), null);
        registry.registerEntry(session);

        Scene scene = new Scene(new SessionsListPane(registry));
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> Stepper.close());
        primaryStage.show();
        primaryStage.setTitle("Stepper");
    }


}
