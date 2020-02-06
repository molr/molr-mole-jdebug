package cern.molr.inspector.remote;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Duration;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemoteReaderTest {

    private static final Duration ONE_MILLISECOND = Duration.ofMillis(1);

    @Mock
    private BufferedReader mockedReader;
    @Mock
    private Consumer<BufferedReader> mockedConsumer;
    private RemoteReader reader;

    @Before
    public void setup() throws IOException {
        Mockito.when(mockedReader.ready()).thenReturn(true);
        reader = new RemoteReader(mockedReader, ONE_MILLISECOND) {
            @Override
            protected void readCommand(BufferedReader reader) {
                mockedConsumer.accept(reader);
            }
        };
    }

    @Test
    public void schedulesCallsToReadCommand() throws InterruptedException {
        Thread.sleep(100);
        verify(mockedConsumer, atLeast(2)).accept(mockedReader);
    }

    @Test
    public void closesReader() throws IOException {
        reader.close();
        verify(mockedReader).close();
    }

}