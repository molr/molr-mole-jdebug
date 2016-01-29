/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.gui;

import cern.jarrace.commons.domain.AgentContainer;
import cern.jarrace.commons.domain.Service;
import cern.jarrace.commons.domain.Service;
import cern.jarrace.inspector.gui.rest.ContainerService;
import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import com.sun.glass.ui.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import rx.Observable;
import rx.schedulers.JavaFxScheduler;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ContainerListTab extends ListView<ContainerListTab.EntryPoint> {

    private ObservableList<EntryPoint> list = FXCollections.emptyObservableList();

    public ContainerListTab(ContainerService containerService, Consumer<EntryPoint> onEntryPointActive) throws IOException {
        super();
        setItems(list);

        Observable
                .interval(0, 5, TimeUnit.SECONDS, Schedulers.computation())
                .<List<AgentContainer>>map(id -> {
                    try {
                        return containerService.getContainers().execute().body();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return Collections.emptyList();
                    }
                })
                .subscribeOn(JavaFxScheduler.getInstance())
                .subscribe(this::setContainers);

        setOnMouseClicked(event -> {
            EntryPoint selection = getSelectionModel().getSelectedItem();
            if (selection != null)
                onEntryPointActive.accept(selection);
        });
    }

    public void setContainers(List<AgentContainer> containers) {
        Application.invokeLater(() -> {
            ArrayList<EntryPoint> serviceList = new ArrayList<>();
            containers.stream().forEach(agent -> agent.getServices().forEach(service -> {
                service.getEntryPoints().forEach(entry -> {
                    final EntryPoint entryPoint = new EntryPoint(agent.getContainerName(),
                            service.getClazz().substring(service.getClazz().lastIndexOf(".") + 1), entry);
                    serviceList.add(entryPoint);
                });
            }));
            list = FXCollections.observableList(serviceList);
            setItems(list);
        });
    }

    public static class EntryPoint {

        private final String name;
        private final String clazz;
        private final String entry;

        private EntryPoint(String name, String clazz, String entry) {
            this.name = name;
            this.clazz = clazz;
            this.entry = entry;
        }

        public String getClazz() {
            return clazz;
        }

        public String getName() {
            return name;
        }

        public String getEntry() {
            return entry;
        }

        @Override
        public String toString() {
            return name + ": " + clazz + " " + entry;
        }

    }

}
