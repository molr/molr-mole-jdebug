/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.inspector.cli;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A commandline reader that can read one or more commands from a command-line.
 */
public class CliReader {

    private static final Pattern commandPattern = Pattern.compile("^([^\\W]+)(\\W+([^\\W]+))*$");

    private CliReader() {
        /* Should not be instantiated */
    }

    public static Optional<CliEntry> readCommandFromLine(String line) {
        Matcher matcher = commandPattern.matcher(line);
        if (matcher.matches()) {
            String command = matcher.group();
            matcher.find();
            List<String> arguments = new ArrayList<>();
            while (matcher.find()) {
                arguments.add(matcher.group());
            }
            return Optional.of(new CliEntry(command, arguments));
        } else {
            return Optional.empty();
        }
    }

}
