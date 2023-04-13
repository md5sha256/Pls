package io.github.md5sha256.pls;

import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.jackson.JacksonConfigurationLoader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Endpoint {

    private final Logger logger;
    private final URI endpoint;
    private final URI endpointDatapack;
    private final URI endpointCommand;
    private final URI endpointModeration;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public Endpoint(Logger logger, URI uri) {
        this.logger = logger;
        this.endpoint = uri;
        this.endpointCommand = endpoint.resolve("command");
        this.endpointDatapack = endpoint.resolve("datapack");
        this.endpointModeration = endpoint.resolve("moderation");
    }

    /**
     * Set up a new HttpRequest with the timeout, and content-type and authorization headers filled in
     *
     * @return Returns a {@link HttpRequest.Builder}
     */
    private static HttpRequest.Builder buildHttpRequest(URI targetUri) {
        return HttpRequest.newBuilder(targetUri)
                .setHeader("Content-Type", "application/json")
                .setHeader("Accept", "application/json")
                .timeout(Duration.ofSeconds(15));
    }

    /**
     * Format the "model" key in the web request
     *
     * @param params The message to sent to openAi
     * @return Returns a json formatted string which should be the value of the prompt key
     */
    private static String formatModel(String params) {
        ConfigurationNode node = JacksonConfigurationLoader.builder().build().createNode();
        try {
            node.node("prompt").set(params);
            return JacksonConfigurationLoader.builder().buildAndSaveString(node);
        } catch (ConfigurateException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public CompletableFuture<RequestResult> requestCommand(String prompt) {
        HttpRequest request = buildHttpRequest(endpointCommand).POST(HttpRequest.BodyPublishers.ofString(formatModel(prompt))).build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray()).thenApply(httpResponse -> {
            if (httpResponse.statusCode() != 200) {
                return new RequestResult(new String(httpResponse.body(), StandardCharsets.UTF_8), true);
            }
            return formatResponse(httpResponse.body());
        });
    }

    public CompletableFuture<RequestResult> requestModeration(String prompt) {
        HttpRequest request = buildHttpRequest(endpointModeration).POST(HttpRequest.BodyPublishers.ofString(formatModel(prompt))).build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray()).thenApply(httpResponse -> {
            if (httpResponse.statusCode() != 200) {
                return new RequestResult(new String(httpResponse.body(), StandardCharsets.UTF_8), true);
            }
            return formatResponse(httpResponse.body());
        });
    }

    public CompletableFuture<RequestResult> requestDatapack(String prompt, DatapackHandler datapackHandler) {
        HttpRequest request = buildHttpRequest(endpointDatapack).POST(HttpRequest.BodyPublishers.ofString(formatModel(prompt))).build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream()).thenApply(httpResponse -> {
            if (httpResponse.statusCode() != 200) {
                this.logger.warning("Failed to save datapack. Status code: " + httpResponse.statusCode());
                return new RequestResult("Failed to save datapack. Status code: " + httpResponse.statusCode(), true);
            }

            Optional<String> content = httpResponse.headers().firstValue("Content-Disposition");
            if (content.isEmpty()) {
                this.logger.warning("Failed to save datapack, missing content-disposition header.");
                return new RequestResult("Failed to save datapack, missing content-disposition header.", true);
            }
            String fileName = content.get().substring("attachment; filename=".length());
            try (InputStream inputStream = httpResponse.body()) {
                datapackHandler.acceptDatapack(fileName, inputStream);
            } catch (IOException ex) {
                this.logger.log(Level.SEVERE, "Failed to save datapack: fileName=" + fileName, ex);
                return null;
            }
            return new RequestResult(fileName, false);
        });
    }

    /**
     * Convert the bytes from a web request to a {@link RequestResult}
     *
     * @param jsonBytes The json bytes of the response
     * @return A never-null web request
     */
    public RequestResult formatResponse(byte[] jsonBytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(jsonBytes);
        JacksonConfigurationLoader loader = JacksonConfigurationLoader.builder().source(() -> new BufferedReader(new InputStreamReader(bis))).build();
        ConfigurationNode node;
        try {
            node = loader.load();
        } catch (ConfigurateException ex) {
            return new RequestResult(ex.getMessage(), true);
        }
        ConfigurationNode commandNode = node.node("command");
        if (commandNode == null) {
            this.logger.info(new String(jsonBytes, StandardCharsets.UTF_8));
            return new RequestResult("no command", true);
        }
        String command = commandNode.getString();
        return new RequestResult(command, false);
    }


}
