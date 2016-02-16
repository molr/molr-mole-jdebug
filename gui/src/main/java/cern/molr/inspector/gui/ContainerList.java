/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector.gui;

import cern.molr.commons.domain.MoleContainer;
import com.sun.glass.ui.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import rx.Observable;
import rx.schedulers.JavaFxScheduler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A list of {@link MoleContainer}s.
 */
public class ContainerList extends ListView<ContainerList.EntryPoint> {

    private ObservableList<EntryPoint> list = FXCollections.emptyObservableList();

    public ContainerList(Observable<List<MoleContainer>> containerObservable) throws IOException {
        super();
        setItems(list);

        containerObservable
                .subscribeOn(JavaFxScheduler.getInstance())
                .subscribe(this::setContainers);
    }

    public void setContainers(List<MoleContainer> containers) {
        Application.invokeLater(() -> {
            final List<EntryPoint> entryPoints =  containersToEntryPoints(containers);
            setItems(FXCollections.observableList(entryPoints));
        });
    }

    static List<EntryPoint> containersToEntryPoints(List<MoleContainer> containers) {
        ArrayList<EntryPoint> serviceList = new ArrayList<>();
        containers.stream().forEach(agent -> agent.getServices().forEach(service -> {
            service.getEntryPoints().forEach(entry -> {
                final EntryPoint entryPoint = new EntryPoint(agent.getContainerName(),
                        service.getServiceClassName(), entry);
                serviceList.add(entryPoint);
            });
        }));
        return serviceList;
    }

    public Optional<ContainerList.EntryPoint> getSelectedEntryPoint() {
        return Optional.ofNullable(getSelectionModel().getSelectedItem());
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
