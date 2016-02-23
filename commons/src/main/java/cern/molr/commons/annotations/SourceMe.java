package cern.molr.commons.annotations;

/**
 * Created by mgalilee on 19/02/2016.
 */
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to mark classes so their source code is embedded in a companion class.
 * Use it to annotate classes run by a Mole (ie annotated with {@link cern.molr.commons.mole.RunWithMole}) so that their
 * source code is available at runtime. This is especially needed for step by step / debugging execution in a GUI.
 *
 * @author mgalilee
 */
@Retention(RetentionPolicy.CLASS)
public @interface SourceMe {
}
