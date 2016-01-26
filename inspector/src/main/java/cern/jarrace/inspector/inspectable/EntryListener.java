/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.inspectable;

import cern.jarrace.inspector.ThreadState;

/**
 * Handles callbacks from the running VM for a single method entry.
 */
public interface EntryListener {

    void onLocationChange(ThreadState state);

    void onInspectionEnd();

}
