package cern.molr.inspector.gui;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javax.swing.*;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Utilities for integrating JavaFX components with Swing
 *
 * @author tiagomr
 * @author mgalilee
 */
public final class SwingUtils {

    /**
     * Swing compatibility builder embedding a {@link Parent} inside a {@link JFrame}.
     *
     * @param supplier a way to build a {@link Parent} component
     * @return a {@link JFrame} with a freshly built {@link Parent} as its root component
     */
    public static JFrame getJFXFrame(Supplier<Optional<Parent>> supplier) {
        JFrame frame = new JFrame();
        frame.setSize(500, 900);
        JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);

        Platform.runLater(() -> {
            Parent parent = supplier.get()
                    .orElseThrow(() -> new IllegalStateException("No parent to build JFrame from"));
            Scene scene = new Scene(parent);
            fxPanel.setScene(scene);
        });

        return frame;
    }
}
