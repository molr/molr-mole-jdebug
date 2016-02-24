package cern.molr.commons.exception;

/**
 * Exception to be used whenever its not possible to
 * {@link cern.molr.commons.mission.MissionMaterializer#materialize(Class)} a {@link cern.molr.commons.domain.Mission}
 *
 * @author  timartin
 */
public class MissionMaterializationException extends RuntimeException{

    public MissionMaterializationException(String message) {
        super(message);
    }

    public MissionMaterializationException(Throwable cause) {
        super(cause);
    }

    public MissionMaterializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
