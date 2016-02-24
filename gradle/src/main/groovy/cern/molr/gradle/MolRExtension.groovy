/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“.ing this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.gradle;

/**
 * A Gradle extension defining the host of the Jarrace endpoint (where to deploy the jar; required) and
 * the name of the project (optional).
 * If the host is not defined, an exception will be thrown at runtime.
 * If the name of the project is not defined, the project name will be used as default.
 */
public class MolRExtension {

    def String host
    def String name

}
