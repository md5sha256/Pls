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
        this.endpointCommandWE = endpoint.resolve("worldedit");
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
     * @param params The errorMessage to sent to openAi
     * @return Returns a json formatted string which should be the value of the prompt key
     */
    private static String formatPayload(String key, String params) {
        ConfigurationNode node = JacksonConfigurationLoader.builder().build().createNode();
        try {
            node.node(key).set(params);
            return JacksonConfigurationLoader.builder().buildAndSaveString(node);
        } catch (ConfigurateException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public CompletableFuture<RequestResult<String>> requestCommand(String prompt) {
        HttpRequest request = buildHttpRequest(endpointCommand).POST(HttpRequest.BodyPublishers.ofString(formatPayload("prompt", prompt))).build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray()).thenApply(httpResponse -> {
            if (httpResponse.statusCode() != 200) {
                return new RequestResult<>(null, new ErrorData(new String(httpResponse.body(), StandardCharsets.UTF_8), true));
            }
            return parseCommands(httpResponse.body());
        });
    }

    public CompletableFuture<RequestResult<String>> requestCommandWE(String prompt) {
        HttpRequest request = buildHttpRequest(endpointCommandWE).POST(HttpRequest.BodyPublishers.ofString(formatPayload("prompt", prompt))).build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray()).thenApply(httpResponse -> {
            if (httpResponse.statusCode() != 200) {
                return new RequestResult<>(null, new ErrorData(new String(httpResponse.body(), StandardCharsets.UTF_8), true));
            }
            return parseCommands(httpResponse.body());
        });
    }

    public CompletableFuture<RequestResult<ModerationClassification>> requestModeration(String message) {
        HttpRequest request = buildHttpRequest(endpointModeration).POST(HttpRequest.BodyPublishers.ofString(formatPayload("errorMessage", message))).build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray()).thenApply(httpResponse -> {
            if (httpResponse.statusCode() != 200) {
                final String errMsg = new String(httpResponse.body(), StandardCharsets.UTF_8);
                return new RequestResult<ModerationClassification>(null, new ErrorData(errMsg, false));
            }
            return parseModerationClassification(httpResponse.body());
        });
    }

    public CompletableFuture<RequestResult<String>> requestDatapack(String prompt, DatapackHandler datapackHandler) {
        HttpRequest request = buildHttpRequest(endpointDatapack).POST(HttpRequest.BodyPublishers.ofString(formatPayload("prompt", prompt))).build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream()).thenApply(httpResponse -> {
            if (httpResponse.statusCode() != 200) {
                this.logger.warning("Failed to save datapack. Status code: " + httpResponse.statusCode());
                return new RequestResult<>(new ErrorData("Failed to save datapack. Status code: " + httpResponse.statusCode(), true));
            }

            Optional<String> content = httpResponse.headers().firstValue("Content-Disposition");
            if (content.isEmpty()) {
                this.logger.warning("Failed to save datapack, missing content-disposition header.");
                return new RequestResult<>(new ErrorData("Failed to save datapack, missing content-disposition header.", true));
            }
            String fileName = content.get().substring("attachment; filename=".length());
            try (InputStream inputStream = httpResponse.body()) {
                datapackHandler.acceptDatapack(fileName, inputStream);
            } catch (IOException ex) {
                this.logger.log(Level.SEVERE, "Failed to save datapack: fileName=" + fileName, ex);
                return new RequestResult<>(new ErrorData("Failed to save datapack: fileName=" + fileName, true));
            }
            return new RequestResult<>(fileName);
        });
    }

    /**
     * Convert the bytes from a web request to a {@link RequestResult}
     *
     * @param jsonBytes The json bytes of the result
     * @return A never-null web request
     */
    public RequestResult<String> parseCommands(byte[] jsonBytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(jsonBytes);
        JacksonConfigurationLoader loader = JacksonConfigurationLoader.builder().source(() -> new BufferedReader(new InputStreamReader(bis))).build();
        ConfigurationNode node;
        try {
            node = loader.load();
        } catch (ConfigurateException ex) {
            return new RequestResult<>(new ErrorData(ex.getMessage(), false));
        }
        ConfigurationNode commandNode = node.node("command");
        if (commandNode == null) {
            this.logger.info(new String(jsonBytes, StandardCharsets.UTF_8));
            return new RequestResult<>(new ErrorData("no command", false));
        }
        String command = commandNode.getString();
        return new RequestResult<>(command);
    }

    public RequestResult<ModerationClassification> parseModerationClassification(byte[] jsonBytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(jsonBytes);
        JacksonConfigurationLoader loader = JacksonConfigurationLoader.builder()
                .source(() -> new BufferedReader(new InputStreamReader(bis)))
                .defaultOptions(options -> options.serializers(serializers -> serializers.register(ModerationClassification.class, new ModerationClassificationSerializer())))
                .build();
        ModerationClassification classification;
        try {
            ConfigurationNode node = loader.load();
            classification = node.get(ModerationClassification.class);
        } catch (ConfigurateException ex) {
            return new RequestResult<>(new ErrorData(ex.getMessage(), false));
        }
        return new RequestResult<>(classification);
    }
}
