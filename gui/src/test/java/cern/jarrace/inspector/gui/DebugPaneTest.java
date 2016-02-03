/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.gui;

import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;

/**
 * Class that test the {@link DebugPane} features
 *
 * @author tiagomr
 */
public class DebugPaneTest extends ApplicationTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private FlowPane rootPane = new FlowPane();

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(rootPane));
    }

    @Test
    public void testCreateDebugPaneWIthNullString() {
        expectedException.expect(IllegalArgumentException.class);
        rootPane.getChildren().add(new DebugPane(null));
    }

    @Test
    public void testCreateDebugPaneWithEmptyString() {
        DebugPane debugPane = new DebugPane("");
        rootPane.getChildren().add(debugPane);
        assertEquals(0, debugPane.getTextFlow().getChildren().size());
    }

    @Test
    public void testCreateDebugPaneWithOneLineString() {
        DebugPane debugPane = new DebugPane("TEST_LINE");
        rootPane.getChildren().add(debugPane);
        assertEquals(1, debugPane.getTextFlow().getChildren().size());
    }

    @Test
    public void testCreateDebugPaneWithMultipleLineString() {
        DebugPane debugPane = new DebugPane("TEST_LINE\nTEST_LINE");
        rootPane.getChildren().add(debugPane);
        assertEquals(2, debugPane.getTextFlow().getChildren().size());
    }

    @Test
    public void testSetCurrentLineWithZero() {
        expectedException.expect(IllegalArgumentException.class);
        DebugPane debugPane = new DebugPane("TEST_LINE\nTEST_LINE");
        rootPane.getChildren().add(debugPane);
        debugPane.setCurrentLine(0);
    }

    @Test
    public void testSetCurrentLineWithNegativeValue() {
        expectedException.expect(IllegalArgumentException.class);
        DebugPane debugPane = new DebugPane("TEST_LINE\nTEST_LINE");
        rootPane.getChildren().add(debugPane);
        debugPane.setCurrentLine(-1);
    }

    @Test
    public void testSetCurrentLineWithTooBigValue() {
        expectedException.expect(IllegalArgumentException.class);
        DebugPane debugPane = new DebugPane("TEST_LINE\nTEST_LINE");
        rootPane.getChildren().add(debugPane);
        debugPane.setCurrentLine(4);
    }

    @Test
    public void testSetCurrentLineWithValidValue() {
        DebugPane debugPane = new DebugPane("TEST_LINE\nTEST_LINE");
        rootPane.getChildren().add(debugPane);
        debugPane.setCurrentLine(1);
        assertEquals(Color.RED, ((Text) debugPane.getTextFlow().getChildren().get(0)).getFill());
        debugPane.setCurrentLine(2);
        assertEquals(Color.BLACK, ((Text) debugPane.getTextFlow().getChildren().get(0)).getFill());
        assertEquals(Color.RED, ((Text) debugPane.getTextFlow().getChildren().get(1)).getFill());
    }
}