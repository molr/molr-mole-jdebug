package cern.molr.registry.impl;

import cern.molr.registry.MoleRegistry;
import cern.molr.registry.domain.MoleRegistration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of {@link cern.molr.registry.MoleRegistry} that stores {@link cern.molr.registry.domain.MoleRegistration}s
 * in memory only. This implementation does not provide state persistence features, meaning that all the registrations are
 * lost on restart.
 *
 * @author timartin
 */
public class InMemoryMoleRegistry implements MoleRegistry {

    private final Set<MoleRegistration> moleRegistrations = new HashSet<>();

    @Override
    public Set<MoleRegistration> getRegisteredMoles() {
        return new HashSet<>(moleRegistrations);
    }

    @Override
    public Set<MoleRegistration> getRegisteredMoles(Predicate predicate) {
        moleRegistrations.stream()
                .filter(predicate)
                .collect(Collectors.toList());
        return new HashSet<>();
    }

    @Override
    public boolean registerMole(MoleRegistration moleRegistration) {
        return moleRegistrations.add(moleRegistration);
    }
}
