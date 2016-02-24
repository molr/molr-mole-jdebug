/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“.ing this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector.gui;

import cern.molr.commons.domain.impl.MissionImpl;
import cern.molr.commons.registry.impl.ObservableInMemoryEntriesRegistry;
import cern.molr.inspector.controller.JdiController;
import cern.molr.inspector.domain.Session;
import cern.molr.inspector.domain.impl.SessionImpl;
import cern.molr.inspector.entry.EntryListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A user interface that can list and run {@link MoleContainer}s. The containers are known from a remote HTTP REST
 * endpoint.
 */
public class Stepper extends Application {

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
        Session session1 = new SessionImpl(new MissionImpl("MoleClass1", "MissionContentClass1"), new CustomController());
        registry.registerEntry(session1);
        Session session2 = new SessionImpl(new MissionImpl("MoleClass2", "MissionContentClass2"), new CustomController());
        registry.registerEntry(session2);
        Session session3 = new SessionImpl(new MissionImpl("MoleClass3", "MissionContentClass3"), new CustomController());
        registry.registerEntry(session3);
        Session session4 = new SessionImpl(new MissionImpl("MoleClass4", "MissionContentClass4"), new CustomController());
        registry.registerEntry(session4);
        Session session5 = new SessionImpl(new MissionImpl("MoleClass5", "MissionContentClass5"), new CustomController());
        registry.registerEntry(session5);
        Scene scene = new Scene(new SessionsListPane(registry));
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> Stepper.close());
        primaryStage.show();
        primaryStage.setTitle("Stepper");
    }

/*    @Override
    public void start(Stage primaryStage) {
        ObservableInMemoryEntriesRegistry registry = new ObservableInMemoryEntriesRegistry();
        Scene scene = new Scene();
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> Stepper.close());
        primaryStage.show();
        primaryStage.setTitle("Stepper");
    }*/

    private class CustomController implements JdiController {

        @Override
        public void setEntryListener(EntryListener entryListener) {
            System.out.println("Listener Setted");
        }

        @Override
        public void stepForward() {
            System.out.println("Stepped");
        }

        @Override
        public void resume() {
            System.out.println("Resumed");
        }

        @Override
        public void terminate() {
            System.out.println("I have been terminated");
        }
    }
}
