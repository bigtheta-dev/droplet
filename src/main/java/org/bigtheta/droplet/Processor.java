package org.bigtheta.droplet;

import static java.util.stream.Collectors.joining;

public class Processor {
    public String process(String[] arg) {
        Client client = new Client();
        if (arg.length == 1 && arg[0].equals("list")) {
            return client.list()
                    .stream()
                    .map(Object::toString)
                    .collect(joining("\n"));
        } else if (arg.length == 2 && arg[0].equals("create")) {
            return client.create(arg[1]) ? "ok" : "fail";
        } else if (arg.length == 2 && arg[0].equals("delete")) {
            try {
                return client.delete(Long.parseLong(arg[1])) ? "ok" : "fail";
            } catch (NumberFormatException e) {
                return "Can't parse argument";
            }
        }
        return "Unknown command";
    }
}
