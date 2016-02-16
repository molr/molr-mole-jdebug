package cern.molr.registry;

import cern.molr.registry.impl.InMemoryMoleRegistry;
import cern.molr.registry.rest.RegistryFacade;

/**
 * @author timartin
 */
public class RegistryMain {
    public static void main(String[] args) {
        RegistryFacade facade = new RegistryFacade(new InMemoryMoleRegistry());
        facade.publish();
    }
}
