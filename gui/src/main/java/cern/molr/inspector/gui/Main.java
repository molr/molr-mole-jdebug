package cern.molr.inspector.gui;

import cern.molr.commons.domain.impl.MissionImpl;
import cern.molr.commons.mole.impl.ObservableInMemoryEntriesRegistry;
import cern.molr.inspector.domain.Session;
import cern.molr.inspector.domain.impl.SessionImpl;

/**
 * Created by timartin on 23/02/2016.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        ObservableInMemoryEntriesRegistry registry = new ObservableInMemoryEntriesRegistry();
        Session session = new SessionImpl(new MissionImpl("MoleClass", "MissionContentClass"), null);
        registry.registerEntry(session);
        SessionsListPane.getJFrame(registry);
    }
}
