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
    public interface OnCollectionChangedListener {

        /**
         * Method called by an {@link ObservableRegistry} implementation every time a change is done on the registered
         * data
         *
         * @param collection
         */
        public void onCollectionChanged(Collection collection);
    }
}
