package cern.molr.gradle;

import cern.molr.commons.domain.Mission;
import cern.molr.commons.mole.impl.ClasspathAnnotatedMissionDiscoverer;

import java.util.List;

/**
 * Created by mgalilee on 18/02/2016.
 */
public class ClasspathDiscovererTry {

    public static void main(String[] args) {
        ClasspathAnnotatedMissionDiscoverer discoverer = new ClasspathAnnotatedMissionDiscoverer();
        List<Mission> services = discoverer.availableMissions();
        System.out.println(services);
    }
}
