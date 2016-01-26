/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.jdi;

import com.sun.jdi.ClassType;
import com.sun.jdi.event.ClassPrepareEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.function.Consumer;

import static cern.jarrace.inspector.jdi.ClassInstantiationListener.isClassEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class ClassInstantiationListenerTest {

    private static final Class<?> CLASS = ClassInstantiationListenerTest.class;
    private Consumer mockConsumer;
    private ClassInstantiationListener listener;
    private ClassPrepareEvent mockEvent;
    private ClassType mockType;

    @Before
    public void setup() {
        mockConsumer = mock(Consumer.class);
        mockEvent = mock(ClassPrepareEvent.class);
        mockType = mock(ClassType.class);
        when(mockEvent.referenceType()).thenReturn(mockType);
        when(mockType.name()).thenReturn(CLASS.getName());
        listener = new ClassInstantiationListener(CLASS, mockConsumer);
    }

    @Test
    public void canVerifyIfClassTypeAndClassAreEqual() {
        assertTrue(isClassEquals(mockType, CLASS));
    }

    @Test
    public void canVerifyIfClassTypeAndClassAreDifferent() {
        when(mockType.name()).thenReturn("another name");
        assertFalse(isClassEquals(mockType, CLASS));
    }

    @Test
    public void canCallCallback() {
        listener.classPrepare(mockEvent);
        verify(mockConsumer).accept(mockType);
    }

    @Test
    public void canNotFailIfReferenceTypeIsNull() {
        when(mockEvent.referenceType()).thenReturn(null);
        listener.classPrepare(mockEvent);
        verify(mockConsumer, never()).accept(Matchers.any(ClassType.class));
    }

    @Test
    public void canNotCallCallbackIfClassTypeIsNotSameAsClass() {
        when(mockType.name()).thenReturn("I don't exist");
        listener.classPrepare(mockEvent);
        verify(mockConsumer, never()).accept(Matchers.any(ClassType.class));
    }

}
