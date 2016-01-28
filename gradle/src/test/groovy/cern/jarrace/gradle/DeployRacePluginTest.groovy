/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.jarrace.gradle

import org.gradle.api.Project
import org.gradle.api.internal.ClosureBackedAction
import org.gradle.api.internal.plugins.PluginApplicationException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import static org.junit.Assert.assertEquals

public class DeployRacePluginTest {

    @Test(expected = PluginApplicationException)
    public void canFailWhenMissingProperties() {
        Project project = ProjectBuilder.builder().build();
        project.pluginManager.apply 'cern.jarrace.gradle'
    }

    @Test
    public void canRunPluginWithProperties() {
        Project project = ProjectBuilder.builder().build();
        project.pluginManager.apply 'java'
        project.pluginManager.apply 'cern.jarrace.gradle'
        project.extensions.configure(DeployRaceExtension, new ClosureBackedAction<DeployRaceExtension>({
            host = "testHost"
        }))
    }

    @Test
    public void canSetDefaultNameToProjectName() {
        Project project = ProjectBuilder.builder().build();
        project.pluginManager.apply 'java'
        project.pluginManager.apply 'cern.jarrace.gradle'
        project.extensions.configure(DeployRaceExtension, new ClosureBackedAction<DeployRaceExtension>({
            host = "testHost"
        }))

        assertEquals(project.jarrace.name, null)
    }

    @Test
    public void canSetHost() {
        def hostName = "testHost"
        Project project = ProjectBuilder.builder().build();
        project.pluginManager.apply 'java'
        project.pluginManager.apply 'cern.jarrace.gradle'
        project.extensions.configure(DeployRaceExtension, new ClosureBackedAction<DeployRaceExtension>({
            host = hostName
        }))

        assertEquals(project.jarrace.host, hostName)
    }

    @Test
    public void canSetName() {
        def projectName = "testName"
        Project project = ProjectBuilder.builder().build();
        project.pluginManager.apply 'java'
        project.pluginManager.apply 'cern.jarrace.gradle'
        project.extensions.configure(DeployRaceExtension, new ClosureBackedAction<DeployRaceExtension>({
            name = projectName
        }))

        assertEquals(project.jarrace.name, projectName)
    }


}
