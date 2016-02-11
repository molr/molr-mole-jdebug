package cern.jarrace.inspector.remote;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Duration;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

public class RemoteReaderTest {

    private static final Duration ONE_MILLISECOND = Duration.ofMillis(1);

    private BufferedReader mockedReader;
    private Consumer<BufferedReader> mockedConsumer;
    private RemoteReader reader;

    @Before
    public void setup() {
        mockedReader = mock(BufferedReader.class);
        mockedConsumer = mock(Consumer.class);
        reader = new RemoteReader(mockedReader, ONE_MILLISECOND) {
            @Override
            protected void readCommand(BufferedReader reader) {
                mockedConsumer.accept(reader);
            }
        };
    }

    @Test
    public void schedulesCallsToReadCommand() throws InterruptedException {
        Thread.sleep(2);
        verify(mockedConsumer, atLeast(2)).accept(mockedReader);
    }

    @Test
    public void closesReader() throws IOException {
        reader.close();
        verify(mockedReader).close();
    }

}