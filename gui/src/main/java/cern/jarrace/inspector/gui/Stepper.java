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
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Debug");

            DebugPane debugPane = new DebugPane(TEXT);
            debugPane.setPrefSize(700, 400);
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

    private static final String TEXT = "package cern.jarrace.inspector.gui;\n" +
            "\n" +
            "import cern.jarrace.commons.domain.AgentContainer;\n" +
            "import cern.jarrace.inspector.gui.rest.ContainerService;\n" +
            "import com.sun.glass.ui.Application;\n" +
            "import javafx.collections.FXCollections;\n" +
            "import javafx.collections.ObservableList;\n" +
            "import javafx.scene.control.ListView;\n" +
            "import rx.Observable;\n" +
            "import rx.schedulers.JavaFxScheduler;\n" +
            "import rx.schedulers.Schedulers;\n" +
            "\n" +
            "import java.io.IOException;\n" +
            "import java.util.ArrayList;\n" +
            "import java.util.Collections;\n" +
            "import java.util.List;\n" +
            "import java.util.Optional;\n" +
            "import java.util.concurrent.TimeUnit;\n" +
            "\n" +
            "public class ContainerListTab extends ListView<ContainerListTab.EntryPoint> {\n" +
            "\n" +
            "    private ObservableList<EntryPoint> list = FXCollections.emptyObservableList();\n" +
            "    private Optional<EntryPoint> selectedEntryPoint = Optional.empty();\n" +
            "\n" +
            "    public ContainerListTab(ContainerService containerService) throws IOException {\n" +
            "        super();\n" +
            "        setItems(list);\n" +
            "\n" +
            "        Observable\n" +
            "                .interval(0, 5, TimeUnit.SECONDS, Schedulers.computation())\n" +
            "                .<List<AgentContainer>>map(id -> {\n" +
            "                    try {\n" +
            "                        return containerService.getContainers().execute().body();\n" +
            "                    } catch (IOException e) {\n" +
            "                        e.printStackTrace();\n" +
            "                        return Collections.emptyList();\n" +
            "                    }\n" +
            "                })\n" +
            "                .subscribeOn(JavaFxScheduler.getInstance())\n" +
            "                .subscribe(this::setContainers);\n" +
            "\n" +
            "        setOnMouseClicked(event -> {\n" +
            "            selectedEntryPoint = Optional.of(getSelectionModel().getSelectedItem());\n" +
            "        });\n" +
            "    }\n" +
            "\n" +
            "    public void setContainers(List<AgentContainer> containers) {\n" +
            "        Application.invokeLater(() -> {\n" +
            "            ArrayList<EntryPoint> serviceList = new ArrayList<>();\n" +
            "            containers.stream().forEach(agent -> agent.getServices().forEach(service -> {\n" +
            "                service.getEntryPoints().forEach(entry -> {\n" +
            "                    final EntryPoint entryPoint = new EntryPoint(agent.getContainerName(),\n" +
            "                            service.getClassName().substring(service.getClassName().lastIndexOf(\".\") + 1), entry);\n" +
            "                    serviceList.add(entryPoint);\n" +
            "                });\n" +
            "            }));\n" +
            "            setItems(FXCollections.observableList(serviceList));\n" +
            "        });\n" +
            "    }\n" +
            "\n" +
            "    public Optional<EntryPoint> getSelectedEntryPoint() {\n" +
            "        return selectedEntryPoint;\n" +
            "    }\n" +
            "\n" +
            "    public static class EntryPoint {\n" +
            "\n" +
            "        private final String name;\n" +
            "        private final String clazz;\n" +
            "        private final String entry;\n" +
            "\n" +
            "        private EntryPoint(String name, String clazz, String entry) {\n" +
            "            this.name = name;\n" +
            "            this.clazz = clazz;\n" +
            "            this.entry = entry;\n" +
            "        }\n" +
            "\n" +
            "        public String getClazz() {\n" +
            "            return clazz;\n" +
            "        }\n" +
            "\n" +
            "        public String getName() {\n" +
            "            return name;\n" +
            "        }\n" +
            "\n" +
            "        public String getEntry() {\n" +
            "            return entry;\n" +
            "        }\n" +
            "\n" +
            "        @Override\n" +
            "        public String toString() {\n" +
            "            return name + \": \" + clazz + \" \" + entry;\n" +
            "        }\n" +
            "\n" +
            "    }\n" +
            "}";
}
