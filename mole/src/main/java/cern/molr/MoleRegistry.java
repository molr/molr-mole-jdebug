/**
 * © Copyright 2016 CERN. This software is distributed under the terms of the Apache License Version 2.0, copied
 * verbatim in the file “COPYING”. In applying this licence, CERN does not waive the privileges and immunities granted
 * to it by virtue of its status as an Intergovernmental Organization or submit itself to any jurisdiction.
 */
package cern.molr;

/**
 *
 * @author jepeders
 * @author tiagomr
 */

public class MoleRegistry {

/*    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Expected three arguments (name path host[:port]), but received " + args.length);
        }

        final String name = args[0];
        final String path = args[1];
        final String stringUri = args[2];
        try {
            final URL registerUrl = getRegisterUrl(name, stringUri);
            ClasspathAnnotatedMissionDiscoverer classpathAnnotatedTaskDiscoverer = new ClasspathAnnotatedMissionDiscoverer();
            Map<Mole, Map<Class<?>, List<Method>>> moles = classpathAnnotatedTaskDiscoverer.discover();
            MoleContainer moleContainer = getAgentContainer(name, path);
            registerContainer(registerUrl, mole);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(
                    String.format("Expected URI in form of host[:port], but received %s: %s", stringUri, e));
        }
    }

    private static MoleContainer getAgentContainer(String containerName, String containerPath) {
        List<Service> services = new ArrayList<>();
        agents.entrySet().forEach( agentEntry -> {
            agentEntry.getValue().entrySet().forEach( classEntry -> {
                List<String> methodNames = classEntry.getValue().stream().map(method -> {
                    return method.getName();
                }).collect(Collectors.toList());
                services.add(new Service(agentEntry.getKey().getClass().getName(), classEntry.getKey().getName(), methodNames));
            });
        });

        return new MoleContainer(containerName, containerPath, services);
    }


    public Map<Mole, Map<Class<?>, List<Method>>> getAgents() {
        return agents;
    }

    private static URL getRegisterUrl(String name, String controllerEndpoint) throws MalformedURLException {
        return new URL("http://" + controllerEndpoint + "/jarrace/container/registerClassInstantiation/");
    }

    private static void registerContainer(URL endpoint, MoleContainer moleContainer) {
        try {
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String response = objectWriter.writeValueAsString(moleContainer);
            System.out.println(response);
            connection.getOutputStream().write(response.getBytes());
            System.out.println(connection.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
