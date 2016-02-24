/*
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING“. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */

package cern.molr.commons.annotations;

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
