package cern.molr.commons.mole.impl;

import cern.molr.commons.domain.Mission;
import cern.molr.commons.domain.impl.MissionImpl;
import cern.molr.commons.mole.MissionMaterializer;
import cern.molr.commons.mole.Mole;
import cern.molr.commons.mole.RunWithMole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of {@link MissionMaterializer} that can instantiate {@link Mission}s from {@link Class}es
 * annotated with {@link RunWithMole}
 *
 * @author tiagomr
 */
public class AnnotatedMissionMaterializer implements MissionMaterializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatedMissionMaterializer.class);

    @Override
    public Optional<Mission> materialize(Class<?> classType) {
        LOGGER.info("Materializing annotated mission class [{}]", classType.getCanonicalName());
        RunWithMole moleAnnotation = classType.getAnnotation(RunWithMole.class);
        LOGGER.debug("Annotation RunWithMole found.");
        Class<? extends Mole> moleClass = moleAnnotation.value();
        LOGGER.debug("Mole class found: [{}]", moleClass.getCanonicalName());
        Optional<Mole> moleOptional = instantiateMole(moleClass);
        if (moleOptional.isPresent()) {
            LOGGER.debug("Mole class instantiated");
            String moleClassName = moleClass.getName();
            LOGGER.debug("Running mole discovery method");
            List<String> methodsNames = moleOptional.get().discover(classType).stream()
                    .map(Method::getName)
                    .collect(Collectors.toList());
            MissionImpl mission = new MissionImpl(moleClassName, classType.getName(), methodsNames);
            LOGGER.debug("Mission created [{}]", mission);
            return Optional.of(mission);
        } else {
            return Optional.empty();
        }
    }

    private static Optional<Mole> instantiateMole(final Class<? extends Mole> moleClass) {
        Mole mole = null;
        try {
            Constructor<? extends Mole> constructor = moleClass.getConstructor();
            mole = constructor.newInstance();
        } catch (Exception exception) {
            LOGGER.error("Could not instantiate Mole of class", moleClass, exception);
        }
        return Optional.ofNullable(mole);
    }
}
