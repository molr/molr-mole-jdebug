package cern.molr.gradle;

import cern.molr.commons.domain.Service;
import cern.molr.commons.mole.impl.ClasspathAnnotatedServiceDiscoverer;

import java.util.List;

/**
 * Created by mgalilee on 18/02/2016.
 */
public class ClasspathDiscovererTry {

    public static void main(String[] args) {
        ClasspathAnnotatedServiceDiscoverer discoverer = new ClasspathAnnotatedServiceDiscoverer();
        List<Service> services = discoverer.availableServices();
        System.out.println(services);
    }
}
