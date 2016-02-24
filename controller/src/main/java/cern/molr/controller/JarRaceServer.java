/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“.ing this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr.controller;

import cern.molr.controller.io.IOConfiguration;
import cern.molr.controller.manager.ManagementConfiguration;
import cern.molr.controller.rest.RestConfiguration;
import cern.molr.controller.server.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

/**
 * Main entry point for the controller, instanciates an embedded Jetty container server, loads the spring
 * {@link org.springframework.web.servlet.DispatcherServlet} and configures all the
 * {@link org.springframework.web.bind.annotation.RestController}s
 *
 * @author tiagomr
 */

@SpringBootApplication
@Import({ManagementConfiguration.class, RestConfiguration.class, IOConfiguration.class, ServerConfiguration.class})
public class JarRaceServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(JarRaceServer.class);

    public static void main(String args[]) {
        ApplicationContext context = SpringApplication.run(JarRaceServer.class, args);
    }
}