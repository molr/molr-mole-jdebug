/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.gui.rest;

import rx.Observable;
import rx.schedulers.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * An observable that periodically triggers a {@link Supplier} to create a stream of objects as an {@link Observable}.
 * Unless specified otherwise via {@link #setInterval(Duration)}, the supplier is called every five seconds by default.
 * All units are converted to milliseconds, so an interval or initial delay of less than 1 milliseconds will be rounded
 * to nearest millisecond.
 *
 * @param <T> The type of objects observables built with this class produces.
 */
public class PeriodicObservableBuilder<T> {

    private final Supplier<T> supplier;
    private Duration interval = Duration.ofSeconds(5);
    private Duration initialDelay = Duration.ZERO;

    private PeriodicObservableBuilder(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Creates a builder that produces elements with the given supplier.
     *
     * @param supplier The supplier that produces elements.
     * @return An instance of a {@link PeriodicObservableBuilder}
     */
    public static <T> PeriodicObservableBuilder<T> ofSupplier(Supplier<T> supplier) {
        return new PeriodicObservableBuilder<>(supplier);
    }

    /**
     * Builds an observable using the current parameters in the builder.
     *
     * @return A new {@link Observable} which periodically emits objects from the supplier of this builder.
     */
    public Observable<T> build() {
        return Observable
                .interval(initialDelay.toMillis(), interval.toMillis(),
                        TimeUnit.MILLISECONDS, Schedulers.computation())
                .map(id -> supplier.get());
    }

    /**
     * Sets the initial delay that {@link Observable}s will wait before emitting the first element.
     *
     * @param initialDelay A {@link Duration} of time. Cannot be negative.
     * @return The same builder with the initial delay set.
     * @throws IllegalArgumentException If the delay is negative.
     */
    public PeriodicObservableBuilder<T> setInitialDelay(Duration initialDelay) {
        if (initialDelay.isNegative()) {
            throw new IllegalArgumentException("Delay cannot be negative");
        }
        this.initialDelay = initialDelay;
        return this;
    }

    /**
     * Sets the interval with which to emit the elements of {@link Observable}s built with this builder.
     *
     * @param interval A {@link Duration} of time between emitted elements. Cannot be negative.
     * @return The same builder with the interval set.
     * @throws IllegalArgumentException If the delay was negative.
     */
    public PeriodicObservableBuilder<T> setInterval(Duration interval) {
        if (interval.isNegative()) {
            throw new IllegalArgumentException("Interval cannot be negative");
        }
        this.interval = interval;
        return this;
    }


}
