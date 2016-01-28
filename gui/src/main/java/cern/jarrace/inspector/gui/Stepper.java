/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.gui;

import cern.jarrace.inspector.gui.rest.ContainerService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import retrofit2.JacksonConverterFactory;
import retrofit2.Retrofit;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A user interface that can step over code.
 */
public class Stepper extends Application {

    public static final String STEPPER_CSS = "stepper.css";

    private static final ExecutorService LOG_THREAD_POOL = Executors.newFixedThreadPool(2);

    private static FlowPane rootPane;
    private static TabPane tabs;
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://localhost:8080/jarrace/")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private static ContainerService containerService = retrofit.create(ContainerService.class);

    public static void close() {
        Platform.runLater(() -> Platform.exit());
        LOG_THREAD_POOL.shutdown();
    }

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Inspector");

        final ContainerListTab containerList = new ContainerListTab(containerService);

        rootPane = new FlowPane();
        tabs = new TabPane();
        rootPane.getChildren().add(containerList);

        Scene scene = new Scene(rootPane, 300, 500);
        primaryStage.setScene(scene);
//        scene.getStylesheets().add(Stepper.class.getResource(STEPPER_CSS).toExternalForm());
        primaryStage.setOnCloseRequest(event -> Stepper.close());
        primaryStage.show();
        rootPane.getChildren().add(tabs);
    }

}
