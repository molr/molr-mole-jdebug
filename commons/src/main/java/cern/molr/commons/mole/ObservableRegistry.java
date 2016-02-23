package cern.molr.commons.mole;

import java.util.Collection;

/**
 * Created by timartin on 23/02/2016.
 */
public interface ObservableRegistry<T> extends Registry<T> {
    void addListener(OnCollectionChangedListener listener);

    public interface OnCollectionChangedListener {
        public void onCollectionChanged(Collection collection);
    }
}
