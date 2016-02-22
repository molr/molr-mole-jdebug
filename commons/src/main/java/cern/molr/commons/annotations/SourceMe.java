package cern.molr.commons.annotations;

/**
 * Created by mgalilee on 19/02/2016.
 */
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * annotation to mark classes so their source code is embedded in a companion class.
 * {@link SourceMeProcessor}
 *
 * @author mgalilee
 */
@Retention(RetentionPolicy.CLASS)
public @interface SourceMe {
}
