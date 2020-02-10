package io.molr.mole.jdebug.sourcecode;

import cern.molr.commons.annotations.Source;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public final class SourceCodes {

    private SourceCodes() {
        /* only static methods */
    }

    public static String sourceCodeFor(String classname) {
        try {
            Source classSource = (Source) Class.forName(classname + "Source").newInstance();
            String base64Source = classSource.base64Value();
            return new String(Base64.getDecoder().decode(base64Source));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException exception) {
            throw new RuntimeException("Could not load source code for class '" + classname + "'.", exception);
        }
    }

    public static List<String> sourceLinesFor(String className) {
        String sourceCodeText = sourceCodeFor(className);
        return Arrays.asList(sourceCodeText.split("\n"));
    }
}
