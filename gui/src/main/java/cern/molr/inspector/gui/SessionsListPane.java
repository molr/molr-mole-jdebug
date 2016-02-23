package cern.molr.inspector.gui;

import cern.molr.commons.domain.impl.MissionImpl;
import cern.molr.commons.mole.ObservableRegistry;
import cern.molr.inspector.domain.Session;
import cern.molr.inspector.domain.impl.SessionImpl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import javax.swing.*;
import java.util.Collection;

/**
 * Created by timartin on 23/02/2016.
 */
public class SessionsListPane extends BorderPane implements ObservableRegistry.OnCollectionChangedListener {

    private final ListView<Session> listView = new ListView();
    private final ObservableList<Session> sessions = FXCollections.observableArrayList();
    private final ObservableRegistry<Session> sessionsRegistry;
    private final Button killAllButton = new Button("Terminate all");

    public SessionsListPane(ObservableRegistry<Session> sessionsRegistry) throws Exception {
        super();
        this.sessionsRegistry = sessionsRegistry;
        initUI();
        initData();
    }

    private void initUI() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        killAllButton.setOnMouseClicked(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("You clicked terminate all!!");
            alert.setContentText("Are you insane? You want to terminate EVERYTHING?!?");
            alert.showAndWait();
        });
        hBox.getChildren().add(killAllButton);
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(15, 12, 15, 12));
        setCenter(listView);
        setBottom(hBox);
        setPrefSize(700, 400);
        listView.setItems(sessions);
        listView.setCellFactory(param -> new SessionCell());
    }

    private void initData() {
        sessions.add(new SessionImpl(new MissionImpl("MoleClass", "MissionClass"), null));
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
            if(!empty) {
                Label label = new Label(session.getMission().getMissionContentClassName());
                setGraphic(label);
            }
        }
    }

    public static JFrame getJFrame(ObservableRegistry<Session> registry) throws Exception {
        JFrame frame = new JFrame("Sessions List");
        frame.setSize(500, 900);

        JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);

        SessionsListPane serviceListPane = new SessionsListPane(registry);

        Platform.runLater(() -> {
            Scene scene = new Scene(serviceListPane);
            fxPanel.setScene(scene);
        });

        return frame;
    }
}
