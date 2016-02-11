/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.controller;

/**
 * A controller for a JDI instance that can control any running JVM instances by stepping through or terminating them.
 */
public interface JdiController {

    /**
     * Takes one <i>step</i> in an entry by executing one line / instruction in the running JVM.
     */
    void stepForward();

    /**
     * Terminates the running JVM session.
     */
    void terminate();

}
