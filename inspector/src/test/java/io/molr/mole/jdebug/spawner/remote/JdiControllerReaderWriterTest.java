package io.molr.mole.jdebug.spawner.remote;

import io.molr.mole.jdebug.spawner.controller.JdiController;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.time.Duration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JdiControllerReaderWriterTest {

    private static final Duration ONE_MILLISECOND = Duration.ofMillis(1);
    private JdiController mockedController;
    private JdiControllerWriter writer;

    @Before
    public void setup() throws IOException {
        mockedController = mock(JdiController.class);
        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream(inputStream);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        PrintWriter printWriter = new PrintWriter(outputStream);

        writer = new JdiControllerWriter(printWriter){};
        new JdiControllerReader(bufferedReader, mockedController, ONE_MILLISECOND);
    }

    @Test
    public void forwardsStepCommands() throws InterruptedException {
        writer.stepForward();
        Thread.sleep(100);
        verify(mockedController).stepForward();
    }

    @Test
    public void forwardsTerminateCommands() throws InterruptedException {
        writer.terminate();
        Thread.sleep(100);
        verify(mockedController).terminate();
    }

}