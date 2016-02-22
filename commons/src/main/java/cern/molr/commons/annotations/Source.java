package cern.molr.commons.annotations;

/**
 * Interface use when generating source classes. {@link SourceMeProcessor}
 * @author mgalilee
 */
public interface Source {
    /**
     * @return the source representation of a class encoded in base64
     */
    String base64Value();
}
