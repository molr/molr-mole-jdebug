/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.controller;

import cern.jarrace.inspector.EntryState;

import java.io.Closeable;

/**
 * A controller for a JDI instance that can
 */
public interface JdiController extends Closeable {

    /**
     * Takes one <i>step</i> in a class file by executing one line / instruction in the given class running instance.
     *
     * @param className The name of the class to advance.
     * @return A {@link EntryState} indicating the current state of the class.
     */
    EntryState stepForward(String className);

}
