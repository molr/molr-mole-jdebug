/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.gui;

import cern.jarrace.commons.domain.AgentContainer;
import cern.jarrace.inspector.gui.rest.ContainerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.util.List;

/**
 * Created by jepeders on 1/28/16.
 */
public class ContainerListTab extends ListView<AgentContainer> {

    private final ObservableList<AgentContainer> list = FXCollections.emptyObservableList();

    public ContainerListTab(ContainerService containerService) throws IOException {
        super();

        setContainers(containerService.getContainers().execute().body());

//        stepButton = new Button();
//        stepButton.setText("Execute next instruction");
//        root.getChildren().add(stepButton);
    }

    public void setContainers(List<AgentContainer> containers) {
        list.clear();
        list.addAll(containers);
    }

}
