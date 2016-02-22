package cern.molr.commons.mole;

import cern.molr.commons.domain.Mission;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by timartin on 22/02/2016.
 */
public interface MolrRegistry {
    public List<Mission> getMissions();
    public List<Mission> getMissions(Predicate<Mission> predicate);
    public void registerMission(Mission mission);
    public void registerMissions(List<Mission> missions);
}
