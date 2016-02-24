/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.commons.registry;

import java.util.Collection;

/**
 * Extension of {@link Registry} that notifies registered {@link ObservableRegistry.OnCollectionChangedListener}s
 * every time a change is done on the {@link Registry}
 *
 * @author tiagomr
 */
public interface ObservableRegistry<T> extends Registry<T> {

    /**
     * Registers a {@link ObservableRegistry.OnCollectionChangedListener} to be notified
     *
     * @param listener the {@link ObservableRegistry.OnCollectionChangedListener} to be
     *                 registered
     */
    void addListener(OnCollectionChangedListener listener);

    /**
     * Interface to be implemented by the {@link Object}s which pretend to receive notifications from an
     * {@link ObservableRegistry}
     */
    interface OnCollectionChangedListener {

        /**
         * Method called by an {@link ObservableRegistry} implementation every time a change is done on the registered
         * data
         *
         * @param collection
         */
        void onCollectionChanged(Collection collection);
    }
}
