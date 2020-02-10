package io.molr.mole.jdebug.spawner.controller;

import io.molr.mole.jdebug.spawner.entry.EntryListener;
import io.molr.mole.jdebug.spawner.entry.EntryState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.requireNonNull;

/**
 * This is first try of a wrapper implementation around a normal JdiController. This is far from beautiful,
 * but the first goal is to get some useful debugging running, without all the text-based communication.
 * </p>
 * There is some ugliness: As the listener has to be set to the builder of {@link JdiControllerImpl} ... the instance
 * of this stateful controller is first created and then the delegate is set to it. As mentioned: Far from final ;-)
 *
 * @author kaifox
 */
public class LocalDelegatingStatefulJdiController implements StatefulJdiController, EntryListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalDelegatingStatefulJdiController.class);

    private JdiController delegate;

    private boolean dead = false;
    private Optional<EntryState> lastKnownState = Optional.empty();
    private final Set<JdiStateObserver> observers = new HashSet<>();

    private final String missionContentClassName;

    public LocalDelegatingStatefulJdiController(String missionContentClassName) {
        this.missionContentClassName = missionContentClassName;
    }

    public void setDelegate(JdiController jdiController) {
        this.delegate = requireNonNull(jdiController, "delegate JdiController must not be null");
    }

    @Override
    public void onLocationChange(EntryState state) {
        LOGGER.info("onLocationChange {}", state);
        System.out.println(missionContentClassName);
        if (missionContentClassName.equals(state.getClassName())) {
            LOGGER.info("in class {}, stepping available", missionContentClassName);
            lastKnownState = Optional.of(state);
            entryStateChanged();
        } else {
            LOGGER.info("out of class {}, resuming", missionContentClassName);
            resume();
        }
    }

    @Override
    public void onInspectionEnd(EntryState state) {
        // not used
    }

    @Override
    public void onVmDeath() {
        die();
    }

    @Override
    public void stepForward() {
        delegate.stepForward();
        setUnknownEntryState();
    }

    @Override
    public void resume() {
        delegate.resume();
        setUnknownEntryState();
    }

    @Override
    public void terminate() {
        delegate.terminate();
        setUnknownEntryState();
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    @Override
    public Optional<EntryState> getLastKnownState() {
        return lastKnownState;
    }

    private void setUnknownEntryState() {
        if (lastKnownState.isPresent()) {
            lastKnownState = Optional.empty();
            entryStateChanged();
        }
    }

    private void die() {
        setUnknownEntryState();
        dead = true;
        observers.forEach(JdiStateObserver::death);
    }

    private void entryStateChanged() {
        observers.forEach(JdiStateObserver::entryStateChanged);
    }

    public void addObserver(JdiStateObserver jdiStateObserver) {
        observers.add(jdiStateObserver);
    }

    public void removeObserver(JdiStateObserver jdiStateObserver) {
        observers.remove(jdiStateObserver);
    }
}