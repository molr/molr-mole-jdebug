package io.molr.mole.jdebug.spawner;

import io.molr.mole.jdebug.spawner.examples.HelloWorldExample;
import org.junit.Test;

public class HelloWorldExampleTest {

    @Test(timeout = 2000)
    public void mainDoesNotTimeOut() {
        /* If this test times out, there is likely a classpath problem. For example, one source of not being able to launch the VM was of haveing invalid classpath entried.
        For example, in the original jdiscript documentation it is recommended to have something like: "compile files("${System.properties['java.home']}/../lib/tools.jar")"
        as a dependecy in build.gradle. However, it looks like this is not needed in java 11 anymore and even causes the launch to fail ...*/
        HelloWorldExample.main(new String[]{});
    }

}
