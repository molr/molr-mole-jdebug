package cern.molr.registry.rest.filter;

import cern.molr.commons.domain.Service;
import cern.molr.registry.domain.MoleRegistration;
import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import spark.Filter;
import spark.Request;
import spark.Response;

import java.util.List;

import static spark.Spark.halt;

/**
 * @author timartin
 */
public class MoleRegistrationDeserializerFilter implements Filter {

    public static final String DESERIALIZED_ATTRIBUTE_NAME = "DESSERIALIZED_MOLE_REGISTRATION";
    private final Gson gson = new Gson();

    @Override
    public void handle(Request request, Response response) throws Exception {
        MoleRegistration moleRegistration = gson.fromJson(request.body(), MoleRegistration.class);
        if (moleRegistration == null) {
            haltRequest("Invalid syntax");
        }
        if (moleRegistration.getHost() == null || moleRegistration.getHost().isEmpty()) {
            haltRequest("No host found");
        }
        if (moleRegistration.getMoleClassName() == null || moleRegistration.getMoleClassName().isEmpty()) {
            haltRequest("no mole class name found");
        }
        List<Service> services = moleRegistration.getServices();
        if (services == null || services.isEmpty()) {
            haltRequest("At least one service must be defined");
        }
        assert services != null;
        services.forEach(service -> {
            if (service.getMoleClassName() == null || service.getMoleClassName().isEmpty()) {
                haltRequest("All Services must have mole class name defined and not empty");
            }
            if (service.getServiceClassName() == null || service.getServiceClassName().isEmpty()) {
                haltRequest("All Services must have service class name defined and not empty");
            }
            List<String> tasks = service.getTasks();
            if (tasks == null || tasks.isEmpty()) {
                haltRequest("All services must have at least one task");
            }
            tasks.forEach(task -> {
                if(task == null || task.isEmpty()) {
                    haltRequest("Task cannot be null or empty");
                }
            });
        });
        request.attribute(DESERIALIZED_ATTRIBUTE_NAME, moleRegistration);
    }

    private static final void haltRequest(String errorMessage) {
        halt(HttpStatus.BAD_REQUEST_400, errorMessage);
    }
}
