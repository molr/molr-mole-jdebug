package io.molr.mole.jdebug.spawner.remote;

import io.molr.mole.jdebug.spawner.entry.EntryListener;
import io.molr.mole.jdebug.spawner.entry.EntryState;
import io.molr.mole.jdebug.spawner.entry.impl.EntryStateImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.time.Duration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EntryListenerReaderWriterTest {

    private static final EntryState state = new EntryStateImpl("testClass", "testMethod", 42);
    private static final Duration ONE_MILLISECOND = Duration.ofMillis(1);

    private EntryListener mockedListener = mock(EntryListener.class);
    private EntryListenerWriter writer;

    @Before
    public void setup() throws IOException {
        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream(inputStream);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        PrintWriter printWriter = new PrintWriter(outputStream);

        writer = new EntryListenerWriter(printWriter);
        new EntryListenerReader(bufferedReader, mockedListener, ONE_MILLISECOND);
    }

    @Test
    public void readsOnLocationChange() throws InterruptedException {
        writer.onLocationChange(state);
        Thread.sleep(100);
        verify(mockedListener).onLocationChange(state);
    }

    @Test
    public void readsOnInspectionEnd() throws InterruptedException {
        writer.onInspectionEnd(state);
        Thread.sleep(100);
        verify(mockedListener).onInspectionEnd(state);
    }

    @Test
    public void readsOnVmDeath() throws InterruptedException {
        writer.onVmDeath();
        Thread.sleep(100);
        verify(mockedListener).onVmDeath();
    }

}