package org.bigtheta.droplet.entities;

import com.fasterxml.jackson.databind.JsonNode;

public class Droplet {
    private JsonNode object;

    public Droplet(JsonNode object) {
        this.object = object;
    }

    public String getName() {
        return object.get("name").asText();
    }

    public long getId() {
        return object.get("id").asLong();
    }

    public String getIp() {
        return object.get("networks").get("v4").get(0).get("ip_address").asText();
    }

    @Override
    public String toString() {
        return "ip:" + getIp() + "\tid:" + getId() + "\tname:" + getName();
    }
}