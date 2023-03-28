package io.github.md5sha256.pls;

import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.jackson.JacksonConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Sends and parses web requests to OpenAi
 */
public class RequestExecutor {

    private final URI openAi;
    private final Plugin plugin;

    /**
     * Create a new request executor
     * @param plugin The owning plugin
     * @param openAiToken The open ai token to use
     */
    public RequestExecutor(Plugin plugin) {
        this.plugin = plugin;
        try {
            openAi = new URI("http://127.0.0.1:5000/api/plsmc");
        } catch (URISyntaxException ex) {
            // This should never happen
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Convert the bytes from a web request to a {@link RequestResult}
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
        ConfigurationNode choicesNode = node.node("command");
        String command = choicesNode.getString();
        system.out.println(command);
        return new RequestResult(command , false);
    }

    /**
     * Set up a new HttpRequest with the timeout, and content-type and authorization headers filled in
     * @return Returns a {@link HttpRequest.Builder}
     */
    private HttpRequest.Builder buildHttpRequest() {
        return HttpRequest.newBuilder(openAi)
                .setHeader("Content-Type", "application/json")
                .setHeader("Accept", "application/json")
                .timeout(Duration.ofSeconds(15));
    }

    /**
     * Format the "model" key in the web request
     * @param params The message to sent to openAi
     * @return Returns a json formatted string which should be the value of the "model" key
     * @throws ConfigurateException Thrown if there is a serialization error
     */
    private String formatModel(String params) throws ConfigurateException {
        ConfigurationNode node = JacksonConfigurationLoader.builder().build().createNode();
        node.node("message").set(userMessage(params));
        return JacksonConfigurationLoader.builder().buildAndSaveString(node);
    }

    /**
     * Send a request to OpenAi. This future does not have an exception handler,
     * it may be wise to add one. The request is sent asynchronously using the
     * java thread pool.
     * @param params The message to send to open ai
     * @return A {@link CompletableFuture} of bytes to.
     * @throws ConfigurateException Thrown if the request could not be built due to serialization errors
     */
    public CompletableFuture<byte[]> sendRequest(String params) throws ConfigurateException{
        String model = formatModel(params);
        HttpRequest request = buildHttpRequest().POST(HttpRequest.BodyPublishers.ofString(model, StandardCharsets.UTF_8))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        CompletableFuture<HttpResponse<byte[]>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray());
        return response.thenApply(HttpResponse::body);
    }

    /**
     * Represents the user message in a web request
     * @param params The content of the message
     * @return Returns a message object
     */
    private static Message userMessage(String params) {
        return new Message("user", params);
    }

    /**
     * A data object which represents a user or system message.
     * Useful for serialization via configurate
     * @param role The role (either user or system)
     * @param content The content of the emssage
     */
    @ConfigSerializable
    public record Message(String role, String content) {

    }

    /**
     * A data object which a choice in the web response
     *  Useful for serialization via configurate
     * @param message the message response
     * @param finishReason the finish reason
     * @param index the index of the choice in the response array
     */
    @ConfigSerializable
    public record Choice(Message message, String finishReason, int index) {

    }


}
