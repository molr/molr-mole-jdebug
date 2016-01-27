/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.cli;

import java.io.PrintStream;
import java.util.List;

/**
 * A command in a command line interface.
 */
public interface CliCommand {

    int expectedArguments();

    String getDescription();

    String getName();

    void run(List<String> arguments, PrintStream output);

}
