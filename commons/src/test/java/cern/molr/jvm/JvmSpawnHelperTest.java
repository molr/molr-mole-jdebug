/*
 * � Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file �COPYING�. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.jvm;

import junit.framework.TestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Class that tests the behaviour of {@link JvmSpawnHelper}
 *
 * @author tiagomr
 */
@RunWith(MockitoJUnitRunner.class)
public class JvmSpawnHelperTest extends TestCase {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testAppendToolsJarToClasspathWithNullClasspath() {
        expectedException.expect(IllegalArgumentException.class);
        JvmSpawnHelper.appendToolsJarToClasspath(null);
    }

    @Test
    public void testAppendToolsJarToClasspath() {
        String classpath = "SampleClasspath";
        String appendedClasspath = JvmSpawnHelper.appendToolsJarToClasspath(classpath);
        assertTrue(appendedClasspath.contains(classpath));
        assertTrue(appendedClasspath.contains(JvmSpawnHelper.TOOLS_PATH));
    }
}