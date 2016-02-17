/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector.gui;

import cern.molr.inspector.controller.JdiController;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.testfx.framework.junit.ApplicationTest;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;

/**
 * Class that test the {@link DebugPane} features
 *
 * @author tiagomr
 */

@RunWith(MockitoJUnitRunner.class)
public class DebugPaneTest extends ApplicationTest {

    @Mock
    private JdiController jdiController;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private FlowPane rootPane = new FlowPane();

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(rootPane));
    }

    @Test
    public void testCreateDebugPaneWithNullString() {
        expectedException.expect(IllegalArgumentException.class);
        DebugPane debugPane = new DebugPane(null);
        debugPane.setJdiController(jdiController);
        rootPane.getChildren().add(debugPane);
    }

    @Test
    public void testCreateDebugPaneWithEmptyString() throws InterruptedException {
        DebugPane debugPane = new DebugPane("");
        debugPane.setJdiController(jdiController);
        addDebugPane(debugPane);
        assertEquals(0, debugPane.getTextFlow().getChildren().size());
    }

    @Test
    public void testCreateDebugPaneWithOneLineString() throws InterruptedException {
        DebugPane debugPane = new DebugPane("TEST_LINE");
        debugPane.setJdiController(jdiController);
        addDebugPane(debugPane);
        assertEquals(1, debugPane.getTextFlow().getChildren().size());
    }

    @Test
    public void testCreateDebugPaneWithMultipleLineString() throws InterruptedException {
        DebugPane debugPane = new DebugPane("TEST_LINE\nTEST_LINE");
        debugPane.setJdiController(jdiController);
        addDebugPane(debugPane);
        assertEquals(2, debugPane.getTextFlow().getChildren().size());
    }

    private void addDebugPane(DebugPane debugPane) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            rootPane.getChildren().add(debugPane);
            latch.countDown();
        });
        latch.await();
    }

    @Test
    public void testSetCurrentLineWithZero() throws InterruptedException {
        expectedException.expect(IllegalArgumentException.class);
        DebugPane debugPane = new DebugPane("TEST_LINE\nTEST_LINE");
        debugPane.setJdiController(jdiController);
        addDebugPane(debugPane);
        debugPane.setCurrentLine(0);
    }

    @Test
    public void testSetCurrentLineWithNegativeValue() throws InterruptedException {
        expectedException.expect(IllegalArgumentException.class);
        DebugPane debugPane = new DebugPane("TEST_LINE\nTEST_LINE");
        debugPane.setJdiController(jdiController);
        addDebugPane(debugPane);
        debugPane.setCurrentLine(-1);
    }

    @Test
    public void testSetCurrentLineWithTooBigValue() throws InterruptedException {
        expectedException.expect(IllegalArgumentException.class);
        DebugPane debugPane = new DebugPane("TEST_LINE\nTEST_LINE");
        debugPane.setJdiController(jdiController);
        addDebugPane(debugPane);
        debugPane.setCurrentLine(4);
    }

    @Test
    public void testSetCurrentLineWithValidValue() throws InterruptedException {
        DebugPane debugPane = new DebugPane("TEST_LINE\nTEST_LINE");
        debugPane.setJdiController(jdiController);
        addDebugPane(debugPane);
        debugPane.setCurrentLine(1);
        Thread.sleep(100);
        assertEquals(Color.RED, ((Text) debugPane.getTextFlow().getChildren().get(0)).getFill());
        debugPane.setCurrentLine(2);
        Thread.sleep(100);
        assertEquals(Color.BLACK, ((Text) debugPane.getTextFlow().getChildren().get(0)).getFill());
        assertEquals(Color.RED, ((Text) debugPane.getTextFlow().getChildren().get(1)).getFill());
    }
}