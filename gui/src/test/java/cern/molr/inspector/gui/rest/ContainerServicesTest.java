/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector.gui.rest;

import cern.molr.commons.domain.Mole;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ContainerServicesTest {

    private static final List<Mole> CONTAINER_LIST = Collections.emptyList();

    private ContainerService mockedService;

    @Before
    public void setup() throws IOException {
        mockedService = mock(ContainerService.class);
        Call<List<Mole>> mockedCall = mock(Call.class);
        Response<List<Mole>> response = Response.success(CONTAINER_LIST);
        when(mockedService.getContainers()).thenReturn(mockedCall);
        when(mockedCall.execute()).thenReturn(response);
    }

    @Test
    public void createsAContainerService() {
        ContainerServices.ofBaseUrl("http://testHost/");
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsToCreateServiceWithWrongUrl() {
        ContainerServices.ofBaseUrl("wrongUrl");
    }

    @Test
    public void createsPeriodicObservableFromAContainerService() {
        Action1<List<Mole>> mockedAction = mock(Action1.class);
        Observable<List<Mole>> observable = new ContainerServices(mockedService, mockedService)
                .createObservable(ContainerService::getContainers);
        observable.subscribe(mockedAction);
        verify(mockedAction).call(CONTAINER_LIST);
    }

    @Test
    public void returnsUnderlyingContainerService() {
        assertEquals(mockedService, new ContainerServices(mockedService, mockedService).getContainerService());
    }

}