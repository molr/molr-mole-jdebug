package cern.jarrace.inspector.gui;

import cern.jarrace.inspector.gui.rest.Services;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import retrofit2.Response;

import java.io.IOException;

/**
 * @author tiagomr
 */
public class DebugPane extends ScrollPane {

    private final TextFlow textFlow = new TextFlow();

    public DebugPane(String containerName, String sourceFile) throws IOException {
        setContent(textFlow);
        Response<String> serviceResponse = Services.getEntryService().readSource(containerName, sourceFile).execute();
        textFlow.getChildren().add(new Text(serviceResponse.body()));
    }
}