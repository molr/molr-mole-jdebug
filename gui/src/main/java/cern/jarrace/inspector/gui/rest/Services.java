package cern.jarrace.inspector.gui.rest;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.JacksonConverterFactory;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by timartin on 02/02/2016.
 */
public class Services {

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

    private static final Retrofit JSON_RETROFIT = new Retrofit.Builder()
            .baseUrl("http://localhost:8080/jarrace/")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private static final Retrofit TEXT_RETROFIT = new Retrofit.Builder()
            .baseUrl("http://localhost:8080/jarrace/")
            .addConverterFactory(STRING_FACTORY)
            .build();

    private static ContainerService containerService = JSON_RETROFIT.create(ContainerService.class);
    private static ContainerService entryService = TEXT_RETROFIT.create(ContainerService.class);

    public static ContainerService getContainerService() {
        return containerService;
    }

    public static ContainerService getEntryService() {
        return entryService;
    }
}
