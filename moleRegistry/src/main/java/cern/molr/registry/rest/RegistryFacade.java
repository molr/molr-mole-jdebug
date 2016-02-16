package cern.molr.registry.rest;

import cern.molr.registry.MoleRegistry;
import cern.molr.registry.domain.MoleRegistration;
import cern.molr.registry.rest.filter.MoleRegistrationDeserializerFilter;
import com.google.gson.Gson;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static spark.Spark.*;

/**
 * REST facade to access a {@link MoleRegistry} instance features
 *
 * @author timartin
 */
public class RegistryFacade {

    private static final Gson GSON = new Gson();
    private static final String ROOT_PATH = "/molr/registry/";
    private static final String LIST_ACTION = "list";
    private static final String REGISTER_ACTION = "register";
    private static final String FILTER_PARAM_MOLE_CLASS_NAME = "moleClassName";
    private static final String FILTER_PARAM_HOST = "host";

    private MoleRegistry moleRegistry;

    public RegistryFacade(MoleRegistry moleRegistry) {
        this.moleRegistry = moleRegistry;
    }

    public void publish() {
        get(ROOT_PATH + LIST_ACTION, this::getMoleRegistrations, GSON::toJson);
        post(ROOT_PATH + REGISTER_ACTION, this::registerMole, GSON::toJson);
        before(ROOT_PATH + REGISTER_ACTION, new MoleRegistrationDeserializerFilter());
    }

    public void stop() {
        stop();
    }

    private final Set<MoleRegistration> getMoleRegistrations(Request request, Response response) {
        Map<String, String[]> params = request.queryMap().toMap();
        Set<MoleRegistration> registeredMoles = null;
        if (params.containsKey(FILTER_PARAM_MOLE_CLASS_NAME) || params.containsKey(FILTER_PARAM_HOST)) {
            return moleRegistry.getRegisteredMoles(moleRegistration -> {
                if (params.containsKey(FILTER_PARAM_MOLE_CLASS_NAME) &&
                        !Arrays.asList(params.get(FILTER_PARAM_MOLE_CLASS_NAME)).contains(moleRegistration.getMoleClassName())) {
                    return false;

                }
                if (params.containsKey(FILTER_PARAM_HOST) &&
                        !Arrays.asList(params.get(FILTER_PARAM_HOST)).contains(moleRegistration.getHost())) {
                    return false;
                }
                return true;
            });
        } else {
            return moleRegistry.getRegisteredMoles();
        }
    }

    private final Set<MoleRegistration> getFilteredMoleRegistrations(Request request, Response response) {
        return null;
    }

    private final boolean registerMole(Request request, Response response) {
        return moleRegistry.registerMole(request.attribute(MoleRegistrationDeserializerFilter.DESERIALIZED_ATTRIBUTE_NAME));
    }

    private final MoleRegistration deserializeMoleRegistrationFromJson() {
        return null;
    }
}
