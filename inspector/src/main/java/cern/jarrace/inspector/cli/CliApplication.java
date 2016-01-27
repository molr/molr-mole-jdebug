/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.cli;

import java.io.Closeable;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A command line interface application that can run commands in the form of {@link String}s.
 */
public abstract class CliApplication implements Closeable {

    private final CliCommand EXIT_COMMAND = new CliCommand() {

        @Override
        public int expectedArguments() {
            return 0;
        }

        @Override
        public String getDescription() {
            return "Exits the application";
        }

        @Override
        public String getName() {
            return "exit";
        }

        @Override
        public void run(List<String> arguments, PrintStream output) {
            try {
                close();
            } catch (Exception e) {
                e.printStackTrace(output);
            }
        }
    };

    private final Map<String, CliCommand> commands = new HashMap<>();

    public CliApplication(List<CliCommand> commands) {
        commands.forEach(command -> this.commands.put(command.getName(), command));
        this.commands.put(EXIT_COMMAND.getName(), EXIT_COMMAND);
    }

    public void execute(String line, PrintStream outputStream) {
        final CliEntry entry = CliReader.readCommandFromLine(line)
                .orElseThrow(() -> new IllegalArgumentException("Unknown command " + line));
        CliCommand command = commands.get(entry.getCommand());
        if (command == null) {
            outputStream.println("Unknown command " + entry.getCommand());
            outputStream.println(getHelp());
        } else if (command.expectedArguments() > entry.getArguments().size()) {
            outputStream.printf("Too few arguments for command %s. Expected %d but found %d", command.getName(),
                    command.expectedArguments(), entry.getArguments().size());
            outputStream.println(getHelp());
        } else {
            command.run(entry.getArguments(), outputStream);
        }
    }

    public abstract String getHelp();

}
