package cern.molr.controller.server.impl;

import cern.molr.commons.mole.Mole;
import cern.molr.commons.mole.ServiceDiscoverer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author timartin
 */
public class EmbeddedController extends ControllerImpl {

    @Autowired
    ServiceDiscoverer taskDiscoverer;

    @PostConstruct
    public void discoverServices() {
        Map<Mole, Map<Class<?>, List<Method>>> discoveredMoles = taskDiscoverer.discover();
    }
}
