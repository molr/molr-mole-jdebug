/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package io.molr.mole.jdebug.spawner.gui;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.util.function.Supplier;

/**
 * Utilities for integrating JavaFX components with Swing
 *
 * @author tiagomr
 * @author mgalilee
 */
public final class SwingUtils {

    // TODO tests have to be done

    private static final Logger LOGGER = LoggerFactory.getLogger(SwingUtils.class);

    /**
     * Swing compatibility builder embedding a {@link Parent} inside a {@link JFrame}.
     *
     * @param supplier a way to build a {@link Parent} component
     * @return a {@link JFrame} with a freshly built {@link Parent} as its root component
     */
    public static <T extends Parent> JFrame getJFXFrame(Supplier<T> supplier) {
        JFrame frame = new JFrame();
        frame.setSize(500, 900);
        JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);

        Platform.runLater(() -> {
            Parent parent = supplier.get();
            Scene scene = new Scene(parent);
            fxPanel.setScene(scene);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent event) {
                    LOGGER.info("windowClosing", event);
                    Platform.runLater(() -> parent.fireEvent(new WindowEvent(scene.getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST)));
                }
            });
        });

        return frame;
    }
}
