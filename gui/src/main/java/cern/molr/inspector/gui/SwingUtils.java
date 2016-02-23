package cern.molr.inspector.gui;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javax.swing.*;

/**
 * Utilities for integrating JavaFX components with Swing
 *
 * @author tiagomr
 */
public class SwingUtils {

    /**
     * Swing compatibility builder for returning JavaFX's {@link Parent} instances inside of a Swing {@link JFXPanel}
     *
     * @param root The {@link Parent} to serve as root of the {@link JFXPanel}
     * @return a {@link JFXPanel} with the given {@link Parent} as its root component
     * @throws Exception
     */
    public static JFrame getJFXFrame(Parent root) {
        JFrame frame = new JFrame("Sessions List");
        frame.setSize(500, 900);
        JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        Platform.runLater(() -> {
            Scene scene = new Scene(root);
            fxPanel.setScene(scene);
        });
        return frame;
    }
}
