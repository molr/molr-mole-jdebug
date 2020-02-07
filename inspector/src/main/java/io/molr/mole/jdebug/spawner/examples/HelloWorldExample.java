package io.molr.mole.jdebug.spawner.examples;

import org.jdiscript.JDIScript;
import org.jdiscript.util.VMLauncher;

import static org.jdiscript.util.Utils.unchecked;

public class HelloWorldExample {

    public static void main(final String[] args) {
        String OPTIONS = "-cp " + System.getProperty("java.class.path");

        String MAIN = HelloWorld.class.getName();

        System.out.println(OPTIONS);

        JDIScript j = new JDIScript(new VMLauncher(OPTIONS, MAIN).start());

        j.onFieldAccess(MAIN, "helloTo", e -> {
            j.onStepInto(e.thread(), j.once(se -> {
                unchecked(() -> e.object().setValue(e.field(), j.vm().mirrorOf("JDIScript!")));
            }));
        });

        j.run();
    }
}
