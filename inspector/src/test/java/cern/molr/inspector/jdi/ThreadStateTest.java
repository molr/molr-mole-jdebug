/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.inspector.jdi;

import com.sun.jdi.Location;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThreadStateTest {

    private LocationRange mockedRange;
    private Location mockedLocation;

    @Before
    public void setup() {
        mockedLocation = mock(Location.class);
        mockedRange = mock(LocationRange.class);
    }

    @Test
    public void canGetStartLocation() {
        Location startLocation = mock(Location.class);
        when(mockedRange.getStart()).thenReturn(startLocation);
        assertEquals(startLocation, new ThreadState(mockedRange, mockedLocation).getStartLocation());
    }

    @Test
    public void canGetEndLocation() {
        Location endLocation = mock(Location.class);
        when(mockedRange.getStart()).thenReturn(endLocation);
        assertEquals(endLocation, new ThreadState(mockedRange, mockedLocation).getStartLocation());
    }

    @Test
    public void canGetLocation() {
        assertEquals(mockedLocation, new ThreadState(mockedRange, mockedLocation).getCurrentLocation());
    }

    @Test
    public void canChangeLocationOnSet() {
        final ThreadState state1 = new ThreadState(mockedRange, mockedLocation);
        final ThreadState state2 = state1.setLocation(mock(Location.class));
        assertNotEquals(state1.getCurrentLocation(), state2.getCurrentLocation());
        assertNotEquals(state1, state2);
    }

    @Test
    public void canNotChangeRangeWhenSettingCurrentLocation() {
        final ThreadState state1 = new ThreadState(mockedRange, mockedLocation);
        final ThreadState state2 = state1.setLocation(mock(Location.class));
        assertEquals(state1.getStartLocation(), state2.getStartLocation());
        assertEquals(state1.getEndLocation(), state2.getEndLocation());
    }


}
