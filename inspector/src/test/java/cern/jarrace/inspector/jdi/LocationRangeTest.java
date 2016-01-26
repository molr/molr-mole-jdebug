/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.jdi;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocationRangeTest {

    private Method mockMethod;
    private Location mockLocation;

    @Before
    public void setup() {
        mockMethod = mock(Method.class);
        mockLocation = mock(Location.class);
    }

    @Test
    public void canCreateARangeFromAMethod() throws AbsentInformationException {
        List<Location> locations = new ArrayList<>();
        locations.add(mockLocation);
        locations.add(mockLocation);
        when(mockMethod.allLineLocations()).thenReturn(locations);
        assertNotNull(LocationRange.ofMethod(mockMethod));
    }

    @Test
    public void canCreateARangeFromAMethodWithOneLine() throws AbsentInformationException {
        List<Location> locations = Collections.singletonList(mockLocation);
        when(mockMethod.allLineLocations()).thenReturn(locations);
        LocationRange range = LocationRange.ofMethod(mockMethod);
        assertEquals(mockLocation, range.getStart());
        assertEquals(mockLocation, range.getEnd());
    }

    @Test(expected = IllegalArgumentException.class)
    public void canFailWhenCreatingARangeFromAMethodWithNoLines() throws AbsentInformationException {
        when(mockMethod.allLineLocations()).thenReturn(Collections.emptyList());
        LocationRange.ofMethod(mockMethod);
    }

}
