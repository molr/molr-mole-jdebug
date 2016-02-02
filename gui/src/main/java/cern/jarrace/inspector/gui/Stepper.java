/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.gui;

import cern.jarrace.inspector.gui.rest.ContainerService;
import cern.jarrace.inspector.gui.rest.Services;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.*;
import retrofit2.Converter.Factory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
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
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Debug");

            DebugPane debugPane = new DebugPane("asdnkajshdkashdujshkj");
            debugPane.setPrefSize(700, 400);
            debugPane.setBackground(new Background(new BackgroundFill(Color.AQUA, CornerRadii.EMPTY, Insets.EMPTY)));
            stage.setScene(new Scene(debugPane, 700, 400));
            stage.show();
        });

        rootPane = new FlowPane();
        rootPane.setPrefSize(900, 400);
        tabs = new TabPane();
        rootPane.getChildren().add(containerList);
        rootPane.getChildren().add(startButton);
        rootPane.getChildren().add(debugButton);
        Scene scene = new Scene(rootPane, 600, 500);
        primaryStage.setScene(scene);
//        scene.getStylesheets().add(Stepper.class.getResource(STEPPER_CSS).toExternalForm());
        primaryStage.setOnCloseRequest(event -> Stepper.close());
        primaryStage.show();
        rootPane.getChildren().add(tabs);
        primaryStage.setTitle("Inspector");
    }
}
