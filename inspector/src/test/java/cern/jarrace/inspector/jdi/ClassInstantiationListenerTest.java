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

    private static final String CLASS = ClassInstantiationListenerTest.class.getName();
    private Consumer mockedConsumer;
    private ClassInstantiationListener listener;
    private ClassPrepareEvent mockedEvent;
    private ClassType mockedType;

    @Before
    public void setup() {
        mockedConsumer = mock(Consumer.class);
        mockedEvent = mock(ClassPrepareEvent.class);
        mockedType = mock(ClassType.class);
        when(mockedEvent.referenceType()).thenReturn(mockedType);
        when(mockedType.name()).thenReturn(CLASS);
        listener = new ClassInstantiationListener(CLASS, mockedConsumer);
    }

    @Test
    public void canVerifyIfClassTypeAndClassAreEqual() {
        assertTrue(isClassEquals(mockedType, CLASS));
    }

    @Test
    public void canVerifyIfClassTypeAndClassAreDifferent() {
        when(mockedType.name()).thenReturn("another name");
        assertFalse(isClassEquals(mockedType, CLASS));
    }

    @Test
    public void canCallCallback() {
        listener.classPrepare(mockedEvent);
        verify(mockedConsumer).accept(mockedType);
    }

    @Test
    public void canNotFailIfReferenceTypeIsNull() {
        when(mockedEvent.referenceType()).thenReturn(null);
        listener.classPrepare(mockedEvent);
        verify(mockedConsumer, never()).accept(Matchers.any(ClassType.class));
    }

    @Test
    public void canNotCallCallbackIfClassTypeIsNotSameAsClass() {
        when(mockedType.name()).thenReturn("I don't exist");
        listener.classPrepare(mockedEvent);
        verify(mockedConsumer, never()).accept(Matchers.any(ClassType.class));
    }

}
