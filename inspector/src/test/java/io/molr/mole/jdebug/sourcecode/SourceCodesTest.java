package io.molr.mole.jdebug.sourcecode;

import io.molr.mole.jdebug.spawner.PrimitiveTestingMain;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static io.molr.mole.jdebug.sourcecode.SourceCodes.sourceCodeFor;
import static org.assertj.core.api.Assertions.assertThat;

public class SourceCodesTest {

    @Test
    public void sourceCodeIsAvailable() {
        assertThat(sourceCodeFor(classname())).isNotBlank();
    }

    @Test
    public void sourceCodeLineAreAvailable() {
        assertThat(SourceCodes.sourceLinesFor(classname()).size()).isGreaterThan(5);
    }

    private String classname() {
        return PrimitiveTestingMain.class.getName();
    }

}
