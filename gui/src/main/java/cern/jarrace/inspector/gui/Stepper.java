/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.gui;

import cern.jarrace.commons.domain.AgentContainer;
import cern.jarrace.commons.domain.Service;
import cern.jarrace.inspector.gui.rest.ContainerService;
import com.sun.deploy.net.HttpRequest;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.*;
import retrofit2.Converter.Factory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
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

    private static final Factory STRING_FACTORY = new Factory() {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return response -> response.string();
        }

        @Override
        public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return super.requestBodyConverter(type, annotations, retrofit);
        }

        @Override
        public Converter<String, String> stringConverter(Type type, Annotation[] annotations) {
            return null;
        }
    };

    private static FlowPane rootPane;
    private static TabPane tabs;
    private static Retrofit jsonRetrofit = new Retrofit.Builder()
            .baseUrl("http://localhost:8080/jarrace/")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private static ContainerService containerService = jsonRetrofit.create(ContainerService.class);

    private static Retrofit textRetrofit = new Retrofit.Builder()
            .baseUrl("http://localhost:8080/jarrace/")
            .addConverterFactory(STRING_FACTORY)
            .build();
    private static ContainerService entryService = textRetrofit.create(ContainerService.class);


    public static void close() {
        Platform.runLater(Platform::exit);
        LOG_THREAD_POOL.shutdown();
    }

    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Inspector");

        final ContainerListTab containerList = new ContainerListTab(containerService, this::setActiveService);
        containerList.setPrefSize(700, 400);

        rootPane = new FlowPane();
        tabs = new TabPane();
        rootPane.getChildren().add(containerList);

        Scene scene = new Scene(rootPane, 600, 500);
        primaryStage.setScene(scene);
//        scene.getStylesheets().add(Stepper.class.getResource(STEPPER_CSS).toExternalForm());
        primaryStage.setOnCloseRequest(event -> Stepper.close());
        primaryStage.show();
        rootPane.getChildren().add(tabs);
    }

    private void setActiveService(ContainerListTab.EntryPoint entryPoint) {
        System.out.println("Running " + entryPoint);
        try {
            final String response = entryService
                    .startEntry(entryPoint.getName(), entryPoint.getClazz())
                    .execute().body();
            System.out.println("Received response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
