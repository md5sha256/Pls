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

public class RequestExecutor {

    private final String openAiToken;
    private final URI openAi;
    private final Plugin plugin;

    public RequestExecutor(Plugin plugin, String openAiToken) {
        this.plugin = plugin;
        this.openAiToken = openAiToken;
        plugin.getLogger().info("Token: " + openAiToken);
        try {
            openAi = new URI("https://api.openai.com/v1/chat/completions");
        } catch (URISyntaxException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public RequestResult formatResponse(byte[] jsonBytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(jsonBytes);
        JacksonConfigurationLoader loader = JacksonConfigurationLoader.builder().source(() -> new BufferedReader(new InputStreamReader(bis))).build();
        ConfigurationNode node;
        try {
            node = loader.load();
        } catch (ConfigurateException ex) {
            return new RequestResult(ex.getMessage(), true);
        }
        ConfigurationNode choicesNode = node.node("choices");
        List<Choice> choices;
        try {
            choices = choicesNode.getList(Choice.class);
        } catch (ConfigurateException ex) {
            return new RequestResult(ex.getMessage(), true);
        }
        if (choices == null || choices.isEmpty()) {
            this.plugin.getLogger().info(new String(jsonBytes, StandardCharsets.UTF_8));
            return new RequestResult("no choices", true);
        }
        Choice choice = choices.get(0);
        return new RequestResult(choice.message.content, false);
    }

    private HttpRequest.Builder buildHttpRequest() {
        return HttpRequest.newBuilder(openAi)
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", "Bearer " + this.openAiToken)
                .timeout(Duration.ofSeconds(15));
    }

    private String formatModel(String params) throws ConfigurateException {
        ConfigurationNode node = JacksonConfigurationLoader.builder().build().createNode();
        node.node("model").set("gpt-3.5-turbo");
        ConfigurationNode messagesNode = node.node("messages");
        messagesNode.setList(Message.class, List.of(systemMessage(), userMessage(params)));
        return JacksonConfigurationLoader.builder().buildAndSaveString(node);
    }

    public CompletableFuture<byte[]> sendRequest(String params) throws ConfigurateException{
        String model = formatModel(params);
        HttpRequest request = buildHttpRequest().POST(HttpRequest.BodyPublishers.ofString(model, StandardCharsets.UTF_8))
                .build();
        HttpClient client = HttpClient.newHttpClient();
        CompletableFuture<HttpResponse<byte[]>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray());
        return response.thenApply(HttpResponse::body);
    }


    private static Message systemMessage() {
        String content = "You are a helpful assistant. You will generate minecraft commands based on user input. Your response should contain ONLY the command and NEVER any explanation whatsoever, even when you give multiple commands. Start all your commands with a '/'.";
        return new Message("system", content);
    }

    private static Message userMessage(String params) {
        return new Message("user", params);
    }

    @ConfigSerializable
    public record Message(String role, String content) {

    }

    @ConfigSerializable
    public record Choice(Message message, String finishReason, int index) {

    }


}
