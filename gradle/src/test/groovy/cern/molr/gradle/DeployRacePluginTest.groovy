/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“.ing this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.gradle

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
        project.pluginManager.apply 'molr'
    }

    @Test
    public void canRunPluginWithProperties() {
        Project project = ProjectBuilder.builder().build();
        project.pluginManager.apply 'java'
        project.pluginManager.apply 'molr'
        project.extensions.configure(MolRExtension, new ClosureBackedAction<MolRExtension>({
            host = "testHost"
        }))
    }

    @Test
    public void canSetDefaultNameToProjectName() {
        Project project = ProjectBuilder.builder().build();
        project.pluginManager.apply 'java'
        project.pluginManager.apply 'molr'
        project.extensions.configure(MolRExtension, new ClosureBackedAction<MolRExtension>({
            host = "testHost"
        }))

        assertEquals(project.molr.name, null)
    }

    @Test
    public void canSetHost() {
        def hostName = "testHost"
        Project project = ProjectBuilder.builder().build();
        project.pluginManager.apply 'java'
        project.pluginManager.apply 'molr'
        project.extensions.configure(MolRExtension, new ClosureBackedAction<MolRExtension>({
            host = hostName
        }))

        assertEquals(project.molr.host, hostName)
    }

    @Test
    public void canSetName() {
        def projectName = "testName"
        Project project = ProjectBuilder.builder().build();
        project.pluginManager.apply 'java'
        project.pluginManager.apply 'molr'
        project.extensions.configure(MolRExtension, new ClosureBackedAction<MolRExtension>({
            name = projectName
        }))

        assertEquals(project.molr.name, projectName)
    }


}
