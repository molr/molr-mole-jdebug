package cern.molr.controller.server.impl;

import cern.molr.commons.domain.Mission;
import cern.molr.commons.mission.MissionsDiscoverer;
import cern.molr.inspector.controller.JdiController;
import cern.molr.inspector.entry.EntryListener;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author timartin
 */
public class EmbeddedController extends ControllerImpl {

    // TODO: 19/02/2016 Finish this class

    @Autowired
    MissionsDiscoverer taskDiscoverer;

    @PostConstruct
    public void discoverServices() {
        List<Mission> discoveredMoles = taskDiscoverer.availableMissions();
    }

    @Override
    public String runMole(String agentPath, Mission mission, String... tasks) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public JdiController debugMole(String agentPath, Mission mission, EntryListener entryListener, String... tasks) throws Exception {
        throw new UnsupportedOperationException();
    }
}
