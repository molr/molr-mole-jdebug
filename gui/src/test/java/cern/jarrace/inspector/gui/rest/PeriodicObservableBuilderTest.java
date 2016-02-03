/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.gui.rest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import rx.Subscription;
import rx.functions.Action1;

import java.time.Duration;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.mockito.Mockito.*;

public class PeriodicObservableBuilderTest {

    private static final Duration ONE_MILLISECOND = Duration.ofMillis(1);
    private static final Random random = new Random();

    private Action1<Integer> mockedAction;
    private Subscription subscription;
    private Supplier<Integer> mockedSupplier;

    @Before
    public void setup() {
        mockedAction = mock(Action1.class);
        mockedSupplier = mock(Supplier.class);
        when(mockedSupplier.get()).then(invocation -> random.nextInt());
    }

    @After
    public void teardown() {
        subscription.unsubscribe();
    }

    @Test
    public void buildsObservable() throws InterruptedException {
        build();
        verify(mockedAction, atLeastOnce()).call(Matchers.anyInt());
    }

    @Test
    public void callsSupplierPeriodically() throws InterruptedException {
        build();
        Thread.sleep(5);
        verify(mockedSupplier, atLeast(5)).get();
    }

    @Test
    public void callsSubscriberPeriodically() throws InterruptedException {
        build();
        Thread.sleep(5);
        verify(mockedAction, atLeast(5)).call(Matchers.anyInt());
    }

    private Subscription build() {
        return build(builder -> {});
    }

    private Subscription build(Consumer<PeriodicObservableBuilder<Integer>> modification) {
        PeriodicObservableBuilder<Integer> builder = PeriodicObservableBuilder
                .ofSupplier(mockedSupplier)
                .setInterval(ONE_MILLISECOND);
        modification.accept(builder);
        subscription = builder.build().subscribe(mockedAction);
        return subscription;
    }

}