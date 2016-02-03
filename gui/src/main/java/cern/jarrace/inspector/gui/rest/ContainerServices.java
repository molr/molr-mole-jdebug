package cern.jarrace.inspector.gui.rest;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.JacksonConverterFactory;
import retrofit2.Retrofit;
import rx.Observable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Function;

/**
 * A service which fetches information about {@link cern.jarrace.commons.domain.AgentContainer}s from a remote endpoint..
 */
public class ContainerServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerService.class);

    private static final Converter.Factory STRING_FACTORY = new Converter.Factory() {
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return response -> response.string();
        }

        @Override
        public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return super.requestBodyConverter(type, annotations, retrofit);
        }

        @Override
        public Converter<String, String> stringConverter(Type type, Annotation[] annotations) {
            return null;
        }
    };

    private final ContainerService containerService;

    /**
     * Instantiates this class with the given container service.
     *
     * @param containerService The service to use in this class.
     */
    ContainerServices(ContainerService containerService) {
        this.containerService = containerService;
    }

    /**
     * Creates an observable from a method within a {@link ContainerService} by wrapping it in a
     * {@link java.util.function.Supplier} and calling it periodically to generate a simulated stream of data. The
     * supplier is then given to an {@link Observable} which executes periodically. If the query to the service fails,
     * a log message is issued, and no data is returned. So be advised that the observable created from the function may
     * never emit data if the data could not be fetched from the service.
     *
     * @param supplierFunction A function that gets some data from a {@link ContainerService}. This will be called many
     *                         times during the lifetime of the {@link Observable}.
     * @param <T>              The type of elements to emit from the {@link Observable}.
     * @return An {@link Observable} emitting objects of type {@link T}.
     */
    public <T> Observable<T> createObservable(Function<ContainerService, Call<T>> supplierFunction) {
        return PeriodicObservableBuilder.<Optional<T>>ofSupplier(() -> {
            try {
                return Optional.of(supplierFunction.apply(getContainerService()).execute().body());
            } catch (IOException e) {
                LOGGER.warn("Failed to get data from the container service: ", e);
                return Optional.empty();
            }
        }).build()
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    /**
     * Gets the underlying {@link ContainerService} which is connected to a remote endpoint.
     *
     * @return An instance of a {@link ContainerService}.
     */
    public ContainerService getContainerService() {
        return containerService;
    }

    /**
     * Creates a service which executes all it's HTTP REST requests prefixed with the given URL.
     *
     * @param baseUrl A url describing the HTTP endpoint.
     * @return An instance of a container service.
     */
    public static ContainerServices ofBaseUrl(String baseUrl) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .addConverterFactory(STRING_FACTORY)
                .build();
        return new ContainerServices(retrofit.create(ContainerService.class));
    }

}
