package org.bigtheta.droplet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bigtheta.droplet.entities.Droplet;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";

    public static final String BASE_URL = "https://api.digitalocean.com/v2";
    public static final String DROPLETS = "/droplets";
    public static final String DROPLETS_URL = BASE_URL + DROPLETS;
    public static final String DELETE_URL = DROPLETS_URL + "/";

    private CloseableHttpClient client;
    private ObjectMapper mapper;
    private final String token;
    private final String sshKey;

    public static void main(String[] args) {
        System.out.println(new Client().list());
    }

    public Client() {
        client = HttpClients.createDefault();
        mapper = new ObjectMapper();
        token = System.getenv().get("DROPLET_TOKEN");
        sshKey = System.getenv().get("DROPLET_SSH");
    }


    public List<Droplet> list() {
        HttpGet httpGet = new HttpGet(DROPLETS_URL);

        httpGet.setHeader(new BasicHeader(CONTENT_TYPE, APPLICATION_JSON));
        httpGet.setHeader(new BasicHeader(AUTHORIZATION, token));

        try (CloseableHttpResponse response = client.execute(httpGet)) {
            HttpEntity entity = response.getEntity();

            StringWriter writer = new StringWriter();
            IOUtils.copy(entity.getContent(), writer, "UTF-8");
            String content = writer.toString();

            EntityUtils.consume(entity);
            JsonNode actualObj = mapper.readTree(content);

            List<Droplet> droplets = new ArrayList<>();
            actualObj.get("droplets")
                    .forEach(x -> droplets.add(new Droplet(x)));
            return droplets;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(long id) {
        HttpDelete httpDelete = new HttpDelete(DELETE_URL + id);

        httpDelete.setHeader(new BasicHeader(CONTENT_TYPE, APPLICATION_JSON));
        httpDelete.setHeader(new BasicHeader(AUTHORIZATION, token));
        try (CloseableHttpResponse response = client.execute(httpDelete)) {
            return response.getStatusLine().getStatusCode() == 204;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean create(String name) {
        String content = "{" +
                "\"name\":\"" + name + "\"," +
                "\"region\":\"ams3\"," +
                "\"size\":\"s-1vcpu-1gb\"," +
                "\"image\":\"ubuntu-16-04-x64\"," +
                "\"ssh_keys\":[" + sshKey + "]," +
                "\"backups\":false," +
                "\"ipv6\":true," +
                "\"user_data\":null," +
                "\"private_networking\":null," +
                "\"volumes\":null," +
                "\"tags\":[\"pet\"]" +
                "}";
        HttpPost request = new HttpPost(DROPLETS_URL);

        request.setHeader(new BasicHeader(CONTENT_TYPE, APPLICATION_JSON));
        request.setHeader(new BasicHeader(AUTHORIZATION, token));

        StringEntity entity = null;

        try {
            entity = new StringEntity(content);
        } catch (UnsupportedEncodingException e) {
            return false;
        }

        request.setEntity(entity);

        try (CloseableHttpResponse response = client.execute(request)) {
            return response.getStatusLine().getStatusCode() == 202;
        } catch (IOException e) {
            return false;
        }
    }
}
