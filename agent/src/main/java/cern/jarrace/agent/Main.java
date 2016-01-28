package cern.jarrace.agent;

import cern.jarrace.commons.domain.AgentContainer;
import cern.jarrace.commons.domain.Service;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by timartin on 28/01/2016.
 */
public class Main {

    public static void main(String args[]) throws IOException {
        List<Service> services = new ArrayList<>();
        services.add(new Service("AgentA", "ClassA", Collections.singletonList("MainEntryPoint")));
        AgentContainer agentContainer = new AgentContainer("MyContainer", "ThisPath", services);
        ObjectWriter mapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
        System.out.println(mapper.writeValueAsString(agentContainer));
    }

}
