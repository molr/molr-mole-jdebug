package cern.molr.registry.rest;

import cern.molr.registry.MoleRegistry;
import cern.molr.registry.domain.MoleRegistration;
import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;

import java.util.Set;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * REST facade to access a {@link MoleRegistry} instance features
 *
 * @author timartin
 */
public class RegistryFacade {

    private static final String ROOT_PATH = "/molr/registry/";
    private static final String LIST_ACTION = "list";
    private static final String LIST_FILTER_ACTION = LIST_ACTION + "/filter";
    private static final String REGISTER_ACTION = "register";
    private static final Gson GSON = new Gson();

    private MoleRegistry moleRegistry;

    public RegistryFacade(MoleRegistry moleRegistry) {
        this.moleRegistry = moleRegistry;
    }

    public void publish() {
        get(ROOT_PATH + LIST_ACTION, this::getMoleRegistrations, GSON::toJson);
        get(ROOT_PATH + LIST_FILTER_ACTION, this::getFilteredMoleRegistrations, GSON::toJson);
        post(ROOT_PATH + REGISTER_ACTION, this::registerMole, GSON::toJson);
    }

    public void stop() {
        stop();
    }

    private final Set<MoleRegistration> getMoleRegistrations(Request request, Response response) {
        return moleRegistry.getRegisteredMoles();
    }

    private final Set<MoleRegistration> getFilteredMoleRegistrations(Request request, Response response) {
        return null;
    }

    private final boolean registerMole(Request request, Response response) {
        MoleRegistration moleRegistration = GSON.fromJson(request.body(), MoleRegistration.class);
        if(moleRegistration == null) {
            response.status(HttpStatus.BAD_REQUEST_400);
            response.body("Invalid syntax");
        }
        return moleRegistry.registerMole(moleRegistration);
    }

    private final MoleRegistration deserializeMoleRegistrationFromJson(){
        return null;
    }
}
