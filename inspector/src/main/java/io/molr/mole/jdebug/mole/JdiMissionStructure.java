package io.molr.mole.jdebug.mole;

import cern.molr.commons.domain.JdiMission;
import com.google.common.collect.ImmutableMap;
import io.molr.commons.domain.Block;
import io.molr.commons.domain.ImmutableMissionRepresentation;
import io.molr.commons.domain.MissionRepresentation;
import io.molr.mole.jdebug.sourcecode.SourceCodes;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class JdiMissionStructure {

    private final JdiMission jdiMission;
    private final MissionRepresentation representation;
    private final Map<Integer, Block> numberedLines;

    public JdiMissionStructure(JdiMission jdiMission, MissionRepresentation representation, Map<Integer, Block> numberedLines) {
        this.jdiMission = jdiMission;
        this.representation = representation;
        this.numberedLines = numberedLines;
    }

    public static final JdiMissionStructure from(JdiMission mission) {
        String className = mission.getMissionContentClassName();
        List<String> codeLines = SourceCodes.sourceLinesFor(className);
        return fromLines(mission, codeLines);
    }

    public MissionRepresentation representation() {
        return this.representation;
    }

    public static JdiMissionStructure fromLines(JdiMission mission, List<String> lines) {
        AtomicLong nextId = new AtomicLong(0);

        ImmutableMap.Builder<Integer, Block> mapBuilder = ImmutableMap.builder();

        Block root = Block.idAndText("" + nextId.getAndIncrement(), mission.getMissionContentClassName());
        ImmutableMissionRepresentation.Builder builder = ImmutableMissionRepresentation.builder(root);
        for (int i = 0; i < lines.size(); i++) {
            Block block = Block.idAndText("" + nextId.getAndIncrement(), lines.get(i));
            builder.parentToChild(root, block);
            /* line numbers start with one ... so we have to do +1 here*/
            mapBuilder.put(i + 1, block);
        }

        return new JdiMissionStructure(mission, builder.build(), mapBuilder.build());
    }

    public Block blockForLine(int line) {
        return numberedLines.get(line);
    }

    public JdiMission jdiMission() {
        return jdiMission;
    }
}
