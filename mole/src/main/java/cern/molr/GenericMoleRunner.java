package cern.molr;

import cern.molr.commons.mole.Mole;

/**
 * Created by jepeders on 1/22/16.
 */
public class GenericMoleRunner {

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("whatever");
        }

        final String agentName = args[0];
        final String entry = args[1];

        try {
            Mole mole = createAgent(agentName);
            mole.run(entry);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Done running");
    }

    private static Mole createAgent(String agentName) throws Exception {
        System.out.println("Running " + agentName);
        Class<Mole> clazz = (Class<Mole>) Class.forName(agentName);
        return (Mole) clazz.getConstructor().newInstance();
    }
}
