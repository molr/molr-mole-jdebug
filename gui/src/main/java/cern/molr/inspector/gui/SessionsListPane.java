package cern.molr.inspector.gui;

import cern.molr.commons.domain.impl.MissionImpl;
import cern.molr.commons.mole.ObservableRegistry;
import cern.molr.inspector.domain.Session;
import cern.molr.inspector.domain.impl.SessionImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Collection;

/**
 * Implementation {@link BorderPane} that allows the user to view and manipulate all the currently active
 * {@link Session}s
 *
 * @author tiagomr
 */
public class SessionsListPane extends BorderPane implements ObservableRegistry.OnCollectionChangedListener {

    //TODO tests have to be done

    private final ListView<Session> listView = new ListView();
    private final ObservableList<Session> sessions = FXCollections.observableArrayList();
    private final ObservableRegistry<Session> sessionsRegistry;
    private final Button debugButton = new Button("Debug");
    private final Button terminateButton = new Button("Terminate");
    private final Button terminateAllButton = new Button("Terminate all");

    public SessionsListPane(ObservableRegistry<Session> sessionsRegistry) throws Exception {
        super();
        this.sessionsRegistry = sessionsRegistry;
        initUI();
        initData();
    }

    private void initUI() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        debugButton.setOnMouseClicked(event -> createDebugPane(listView.getSelectionModel().getSelectedItem()));
        hBox.getChildren().add(debugButton);
        terminateButton.setOnMouseClicked(event -> terminateSession(listView.getSelectionModel().getSelectedItem()));
        hBox.getChildren().add(terminateButton);
        terminateAllButton.setOnMouseClicked(event -> terminateAllSessions());
        hBox.getChildren().add(terminateAllButton);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(15, 12, 15, 12));
        setCenter(listView);
        setBottom(hBox);
        setPrefSize(700, 400);
        listView.setItems(sessions);
        listView.setCellFactory(param -> new SessionCell());
    }

    private void createDebugPane(Session session) {
        Stage stage = new Stage();
        stage.setScene(new Scene(new DebugPane(session)));
        stage.showAndWait();
    }

    private void initData() {
        sessions.addAll(sessionsRegistry.getEntries());
        sessionsRegistry.addListener(this);
    }

    @Override
    public void onCollectionChanged(Collection collection) {
        sessions.clear();
        sessions.addAll(collection);
    }

    static class SessionCell extends ListCell<Session> {
        @Override
        public void updateItem(Session session, boolean empty) {
            super.updateItem(session, empty);
            if (!empty) {
                Label label = new Label(session.getMission().getMissionContentClassName());
                setGraphic(label);
            }
        }
    }

    private void terminateAllSessions() {
        sessions.stream().forEach(this::terminateSession);
    }

    private void terminateSession(Session session) {
        session.getController().terminate();
        sessionsRegistry.removeEntry(session);
    }
}
