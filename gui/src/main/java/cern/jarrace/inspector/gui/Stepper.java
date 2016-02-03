/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.gui;

import cern.jarrace.inspector.gui.rest.Services;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import retrofit2.Response;

import java.io.IOException;
import java.util.Optional;
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

    public static void close() {
        Platform.runLater(Platform::exit);
        LOG_THREAD_POOL.shutdown();
    }

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initUI(primaryStage);
    }

    private void startService(ContainerListTab.EntryPoint entryPoint) {
        System.out.println("Running " + entryPoint);
        try {
            final String response = Services.getEntryService()
                    .startEntry(entryPoint.getName(), entryPoint.getClazz())
                    .execute().body();
            System.out.println("Received response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI(Stage primaryStage) throws IOException {
        ContainerListTab containerList = new ContainerListTab(Services.getContainerService());
        containerList.setPrefSize(700, 400);

        Button startButton = new Button("Start Service");
        startButton.setOnMouseClicked(eventHandler -> {
            Optional<ContainerListTab.EntryPoint> selectedEntryPoint = containerList.getSelectedEntryPoint();
            if(selectedEntryPoint.isPresent()) {
                startService(selectedEntryPoint.get());
            }
        });

        Button debugButton = new Button("Debug Service");
        debugButton.setOnMouseClicked(eventHandler -> {
            Optional<ContainerListTab.EntryPoint> selectedEntryPoint = containerList.getSelectedEntryPoint();
            if(selectedEntryPoint.isPresent()) {
                try {
                    Response<String> execute = Services.getEntryService().readSource(selectedEntryPoint.get().getName(), selectedEntryPoint.get().getClazz()).execute();
                    String body = execute.body();
                    Stage stage = new Stage();
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.initStyle(StageStyle.DECORATED);
                    stage.setTitle("Debug");
                    DebugPane debugPane = new DebugPane(body);
                    debugPane.setPrefSize(700, 400);
                    stage.setScene(new Scene(debugPane, 700, 400));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        rootPane = new FlowPane();
        tabs = new TabPane();
        rootPane.getChildren().add(containerList);
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(15, 12, 15, 12));
        hBox.getChildren().add(startButton);
        hBox.getChildren().add(debugButton);
        rootPane.getChildren().add(hBox);
        Scene scene = new Scene(rootPane);
        primaryStage.setScene(scene);
//        scene.getStylesheets().add(Stepper.class.getResource(STEPPER_CSS).toExternalForm());
        primaryStage.setOnCloseRequest(event -> Stepper.close());
        primaryStage.show();
        rootPane.getChildren().add(tabs);
        primaryStage.setTitle("Inspector");
    }
}
