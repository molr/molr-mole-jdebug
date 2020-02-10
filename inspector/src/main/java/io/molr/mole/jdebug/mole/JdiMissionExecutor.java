package io.molr.mole.jdebug.mole;

import io.molr.commons.domain.*;
import io.molr.mole.core.tree.MissionExecutor;
import io.molr.mole.jdebug.spawner.JdiMissionSpawner;
import io.molr.mole.jdebug.spawner.controller.StatefulJdiController;
import io.molr.mole.jdebug.spawner.entry.EntryState;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import static io.molr.commons.domain.Result.FAILED;
import static io.molr.commons.domain.Result.SUCCESS;
import static io.molr.commons.domain.RunState.*;
import static io.molr.commons.domain.StrandCommand.STEP_OVER;

public class JdiMissionExecutor implements MissionExecutor {

    private final Strand onlyStrand = Strand.ofId("0");

    private final JdiMissionStructure structure;

    private final ReplayProcessor<MissionState> stateSink = ReplayProcessor.cacheLast();
    private final Flux<MissionState> stateStream = stateSink.publishOn(Schedulers.newSingle("Jdi MissionState publisher"));


    /* variables for mission state ...*/
    private final AtomicReference<Block> actualBlock = new AtomicReference<>();
    private final AtomicReference<RunState> onlyStrandRunState = new AtomicReference<>(UNDEFINED);
    private final ConcurrentHashMap<Block, RunState> blockRunStates = new ConcurrentHashMap();
    private final ConcurrentHashMap<Block, Result> blockResults = new ConcurrentHashMap<>();


    private final StatefulJdiController jdiController;

    public JdiMissionExecutor(JdiMissionStructure structure) {
        this.structure = structure;
        this.jdiController = JdiMissionSpawner.start(structure.jdiMission());

        jdiController.addObserver(new StatefulJdiController.JdiStateObserver() {
            @Override
            public void death() {
                Block actual = actualBlock.getAndSet(null);
                if (actual != null) {
                    blockRunStates.put(actual, FINISHED);
                    /* How to define success? */
                    blockResults.put(actual, SUCCESS);
                }
                onlyStrandRunState.set(FINISHED);
                publishState(Collections.emptySet());
                /* TODO: What to do here? */
            }

            @Override
            public void entryStateChanged() {
                /*TODO: watch out for concurrency!?*/
                Optional<EntryState> state = jdiController.getLastKnownState();
                boolean dead = jdiController.isDead();

                Optional<Block> block = state.map(e -> structure.blockForLine(e.getLine()));
                updateBlock(block);

                publishState(allowedCommands(state, dead));
            }
        });
    }


    private void publishState(Set<StrandCommand> allowedCommands) {
        MissionState.Builder builder = MissionState.builder(Result.summaryOf(blockResults.values()))//
                .add(onlyStrand, onlyStrandRunState.get(), actualBlock.get(), allowedCommands);
        blockResults.entrySet().forEach(e -> builder.blockResult(e.getKey(), e.getValue()));
        blockRunStates.entrySet().forEach(e -> builder.blockRunState(e.getKey(), e.getValue()));
        stateSink.onNext(builder.build());
    }

    private Set<StrandCommand> allowedCommands(Optional<EntryState> state, boolean dead) {
        if (state.isPresent() && !dead) {
            return Collections.singleton(STEP_OVER);
        } else {
            return Collections.emptySet();
        }
    }

    private void updateBlock(Optional<Block> block) {
        /* XXX Concurrency by far not solved!!! However, this might have to be done on lower levels ... to be seen.*/
        if (block.isPresent()) {
            Block newBlock = block.get();

            Block oldBlock = actualBlock.getAndSet(newBlock);
            if (oldBlock != null) {
                blockRunStates.put(oldBlock, FINISHED);
                /* How to define success? */
                blockResults.put(oldBlock, SUCCESS);
            }
            blockRunStates.put(newBlock, PAUSED);
            onlyStrandRunState.set(PAUSED);
        } else {
            Block actual = actualBlock.get();
            if (actual != null) {
                blockRunStates.put(actual, RUNNING);
                onlyStrandRunState.set(RUNNING);
            }
        }
    }

    @Override
    public Flux<MissionState> states() {
        return stateStream;
    }


    @Override
    public void instruct(Strand strand, StrandCommand command) {
        /* This for sure will also change in the future ... multithreading etc... for the moment it has to do ;-)*/
        if (!onlyStrand.equals(strand)) {
            throw new IllegalArgumentException(strand + " is not a valid strand of this mission.");
        }
        instruct(command);
    }

    private void instruct(StrandCommand command) {
        if (STEP_OVER.equals(command)) {
            jdiController.stepForward();
        } else {
            throw new IllegalArgumentException("Command '" + command + "' is not allowed! Only '" + STEP_OVER + "' is allowed for the moment!");
        }
    }

    @Override
    public void instructRoot(StrandCommand command) {
        instructRoot(command);
    }


    @Override
    public Flux<MissionOutput> outputs() {
        /* For the moment no outputs provided ... to be seen how we handle this. (e.g. std out??) */
        return Flux.never();
    }

    @Override
    public Flux<MissionRepresentation> representations() {
        /* Representation does not change for the moment ... however, most probably it will in the future ;-)
        (e.g. step into methods ?)*/
        return Flux.just(structure.representation());
    }

}
