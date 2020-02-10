package io.molr.mole.jdebug.mole;

import cern.molr.commons.domain.JdiMission;
import com.google.common.collect.ImmutableMap;
import io.molr.commons.domain.*;
import io.molr.mole.core.tree.AbstractJavaMole;
import io.molr.mole.core.tree.MissionExecutor;
import io.molr.mole.jdebug.domain.JdiMissions;

import java.util.Map;
import java.util.Set;


/**
 * This is currently a very brute-force version of a mole which in the ends shall be able to  execute (almost) arbitrary
 * java code and controlling it using the java debugging interfacce (jdi). This is the first try in resurrecting
 * the molr code from the early days. It was tried to use quite a top layer of abstraction from the old code, to see if/how
 * we can profit most from the existing code. It might well be that some of these layers are still not required and
 * rather add complexity ... but this has to be seen in later steps. There is still quite some work ahead:
 * <ul>
 *     <li>First of all it seems that there has to be looked quite a lot into the concurrent behaviour of the debugging.
 *     It seems that the old classes are not fully clean on this</li>
 *     <li>A lot of classes to throw away and refactor to one project in this repo.</li>
 *     <li>State handling is currently a big hack on the top layers ... Most probably jdi can do better</li>
 *     <li>Currently only stepping is supported (no pause or resum) ... to be seen if this can be done better
 *     (and still keep track of the cursor)</li>
 *     <li>Multithreading .... completely not tackled </li>
 *     <li>loops will come back (as we saw already in python)</li>
 *     <li>step into!?</li>
 *     <li>How to get output?</li>
 *     <li>parameters?</li>
 *     <li>cleanup of the running java stuff!</li>
 *     <li>... and more to see ;-)</li>
 * </ul>
 * In summary: Just a first step ...
 */
public class JdiMole extends AbstractJavaMole {

    private final Map<Mission, JdiMission> missions;
    private final Map<Mission, JdiMissionStructure> structures;

    public JdiMole(Set<JdiMission> availableMissions) {
        super(JdiMissions.molrMissionsFrom(availableMissions));
        this.missions = JdiMissions.createMap(availableMissions);
        this.structures = createStructures(availableMissions);
    }

    private static Map<Mission, JdiMissionStructure> createStructures(Set<JdiMission> availableMissions) {
        return availableMissions.stream().collect(ImmutableMap.toImmutableMap(JdiMissions::molrMissionOf, JdiMissionStructure::from));
    }

    @Override
    protected MissionExecutor executorFor(Mission mission, Map<String, Object> params) {
        return new JdiMissionExecutor(JdiMissionStructure.from(missions.get(mission)));
    }

    @Override
    protected MissionRepresentation missionRepresentationOf(Mission mission) {
        return structures.get(mission).representation();
    }

    @Override
    protected MissionParameterDescription missionParameterDescriptionOf(Mission mission) {
        return MissionParameterDescription.empty();
    }
}
