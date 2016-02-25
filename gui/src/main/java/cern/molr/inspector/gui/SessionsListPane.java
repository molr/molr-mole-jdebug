/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector.gui;

import cern.molr.commons.registry.ObservableRegistry;
import cern.molr.inspector.domain.Session;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implementation {@link BorderPane} that allows the user to view and manipulate all the currently active
 * {@link Session}s
 *
 * @author tiagomr
 */
public class SessionsListPane extends BorderPane implements ObservableRegistry.OnCollectionChangedListener<Session> {

    //TODO tests have to be done

    private final ListView<Session> listView = new ListView<>();
    private final ObservableList<Session> sessions = FXCollections.observableArrayList();
    private final ObservableRegistry<Session> sessionsRegistry;
    private final Button terminateButton = new Button("Terminate");
    private final Button terminateAllButton = new Button("Terminate all");

    public SessionsListPane(ObservableRegistry<Session> sessionsRegistry) {
        super();
        this.sessionsRegistry = sessionsRegistry;
        initUI();
        initData();
    }

    private void initUI() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        terminateButton.setOnMouseClicked(event -> terminateSession(listView.getSelectionModel().getSelectedItem()));
        terminateButton.setDisable(true);
        hBox.getChildren().add(terminateButton);
        terminateAllButton.setOnMouseClicked(event -> terminateAllSessions());
        terminateAllButton.setDisable(true);
        hBox.getChildren().add(terminateAllButton);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(15, 12, 15, 12));
        setCenter(listView);
        setBottom(hBox);
        setPrefSize(500, 900);
        listView.setItems(sessions);
        listView.setCellFactory(param -> new SessionCell());
        listView.setOnMouseClicked(event -> {
            if (listView.getSelectionModel().getSelectedItem() != null) {
                terminateButton.setDisable(false);
            } else {
                terminateButton.setDisable(true);
            }
        });
    }

    private void initData() {
        sessions.addAll(sessionsRegistry.getEntries());
        sessionsRegistry.addListener(this);
        if (!sessions.isEmpty()) {
            terminateAllButton.setDisable(false);
        }
    }

    @Override
    public void onCollectionChanged(Collection<Session> collection) {
        Platform.runLater(() -> {
            sessions.clear();
            sessions.addAll(collection);
            if (sessions.isEmpty()) {
                terminateAllButton.setDisable(true);
                terminateButton.setDisable(true);
            } else {
                terminateAllButton.setDisable(false);
            }
        });
    }

    static class SessionCell extends ListCell<Session> {
        @Override
        public void updateItem(Session session, boolean empty) {
            super.updateItem(session, empty);
            if (empty) {
                setGraphic(null);
            } else {
                Label label = new Label(session.getMission().getMissionContentClassName());
                setGraphic(label);
            }
        }
    }

    private void terminateAllSessions() {
        sessions.stream()
                .collect(Collectors.toList())
                .forEach(this::terminateSession);
    }

    private void terminateSession(Session session) {
        session.getController().terminate();
        sessionsRegistry.removeEntry(session);
    }
}
