/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.cli;

import java.util.Collections;
import java.util.List;

/**
 * A command line entered in a commandline interface by a user.
 */
public class CliEntry {

    private final String command;
    private final List<String> arguments;

    public CliEntry(String command, List<String> arguments) {
        this.command = command;
        this.arguments = Collections.unmodifiableList(arguments);
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getCommand() {
        return command;
    }

}
