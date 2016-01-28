package cern.jarrace.agent;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AgentContainer {

    private static final Map<Agent, Map<Class<?>, List<Method>>> agents = new HashMap<>();

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Expected three arguments (name path host[:port]), but received " + args.length);
        }

        final String name = args[0];
        final String path = args[1];
        final String stringUri = args[2];
        try {
            final URL registerUrl = getRegisterUrl(name, stringUri);
            ContainerDiscoverer.discover(agents);
            System.out.println(agents.toString());
            registerServices(registerUrl, name, path);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(
                    String.format("Expected URI in form of host[:port], but received %s: %s", stringUri, e));
        }
    }


    public Map<Agent, Map<Class<?>, List<Method>>> getAgents() {
        return agents;
    }

    private static URL getRegisterUrl(String name, String controllerEndpoint) throws MalformedURLException {
        return new URL("http://" + controllerEndpoint + "/jarrace/container/register/");
    }

    private static void registerServices(URL endpoint, String name, String path) {
        agents.entrySet().stream().forEach(entry -> {
            entry.getValue().forEach((clazz, list) -> {
                registerService(endpoint, entry.getKey(), clazz, list);
            });
        });
    }

    private static void registerService(URL endpoint, String name, String path, Agent agent, Class<?> clazz, List<Method> methods) {
        try {
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            String methodsToSend = methods.stream().map(Method::getName).collect(Collectors.joining("\",\""));

            String resp = "{\"containerName\":\" + "


            String response = "{\"containerName\":\"" + agent.getClass().getName() + "\",\"clazz\":\"" + clazz.getName()
                    + "\",\"entryPoints\":[\"" + methodsToSend + "\"]}";
            System.out.println(response);
            connection.getOutputStream().write(response.getBytes());
            System.out.println(connection.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    containerName = null
    agentContainerPath = null
    services = {ArrayList@6040}  size = 0
}
