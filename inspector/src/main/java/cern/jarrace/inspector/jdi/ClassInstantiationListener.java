/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.jdi;

import com.sun.jdi.ClassType;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.event.ClassPrepareEvent;
import org.jdiscript.handlers.OnClassPrepare;

import java.util.*;
import java.util.function.Consumer;

/**
 * An {@link OnClassPrepare} implementation which keeps track of new implementations of a given class (or interface).
 * Whenever a class that has not been initialised before is added, the callback given in the constructor is called.
 */
public class ClassInstantiationListener implements OnClassPrepare {

    private final Consumer<ClassType> implementorCallback;
    private final Class<?> implementorInterface;

    private Set<ClassType> currentImplementations = Collections.emptySet();
    private InterfaceType implementorType;

    /**
     * Creates a new listener instructed to search for implementations of the given class. The callback will be
     * called everytime a class or subclass is instantiated that has <i>not</i> been instantiated - i. e. created
     * for the first time.
     * @param implementorClass The class or interface to search for.
     * @param implementorCallback A callback to call whenever a new (unique) class instance is created.
     */
    public ClassInstantiationListener(Class<?> implementorClass, Consumer<ClassType> implementorCallback) {
        this.implementorInterface = implementorClass;
        this.implementorCallback = implementorCallback;
    }

    @Override
    public void classPrepare(ClassPrepareEvent event) {
        ReferenceType referenceType = event.referenceType();
        if (implementorType == null) {
            if (referenceType.name().equals(implementorInterface.getName())) {
                implementorType = (InterfaceType) referenceType;
            }
        } else {
            Set<ClassType> currentInspectableImplementations = new HashSet<>(implementorType.implementors());
            Set<ClassType> difference = differenceLeft(currentImplementations, currentInspectableImplementations);
            currentImplementations = currentInspectableImplementations;
            difference.forEach(implementorCallback::accept);
        }
    }

    /**
     * Creates a new set which contains all elements which are in the second but not in the first {@link Set}.
     * @param first The first set of elements.
     * @param second The second set of elements.
     * @param <T> The type of element in the sets.
     * @return A set containing all the elements which exists in the second set, but not in the first set. May be empty.
     */
    static <T> Set<T> differenceLeft(Set<T> first, Set<T> second) {
        final Set<T> difference = new HashSet<>();
        for (T elem : second) {
            if (!first.contains(elem)) {
                difference.add(elem);
            }
        }
        return Collections.unmodifiableSet(difference);
    }
}
