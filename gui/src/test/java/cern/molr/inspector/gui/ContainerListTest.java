/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector.gui;

import cern.molr.commons.domain.MoleContainer;
import cern.molr.commons.domain.Service;
import cern.molr.inspector.gui.rest.PeriodicObservableBuilder;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.ListViewMatchers;
import rx.Observable;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

public class ContainerListTest extends ApplicationTest {

    private static final Duration TEN_MILLISECONDS = Duration.ofMillis(10);

    private static final List<String> ENTRY_POINTS = Arrays.asList("method1", "method2");
    private static final List<Service> SERVICE_IMPLs = Collections.singletonList(new Service("testAgent", "testClass", ENTRY_POINTS));
    private static final List<MoleContainer> CONTAINERS = Collections.singletonList(new MoleContainer("testName", "testPath", SERVICE_IMPLs));

    private Supplier<List<MoleContainer>> mockedSupplier;
    private Observable<List<MoleContainer>> containerObservable;
    private ContainerList containerList;

    @Override
    public void start(Stage stage) throws Exception {
        mockedSupplier = mock(Supplier.class);
        when(mockedSupplier.get()).thenReturn(CONTAINERS);
        containerObservable = PeriodicObservableBuilder
                .ofSupplier(mockedSupplier)
                .setInterval(TEN_MILLISECONDS)
                .build();
        containerList = new ContainerList(containerObservable);
        Scene scene = new Scene(containerList);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void displaysContainers() throws InterruptedException {
        List<ContainerList.EntryPoint> entryPoints = ContainerList.containersToEntryPoints(CONTAINERS);
        Thread.sleep(100); // Required to spawn JavaFX
        verifyThat(containerList, ListViewMatchers.hasItems(entryPoints.size()));
    }

    @Test
    public void updatesContainers() throws InterruptedException {
        when(mockedSupplier.get()).thenReturn(Collections.emptyList());
        Thread.sleep(10);
        verifyThat(containerList, ListViewMatchers.hasItems(0));
    }

}