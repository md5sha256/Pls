package io.github.md5sha256.pls;


/**
 * The settings which are defined in the settings.yml
 *
 * @param openAiToken The {@link String} OpenAi token which is in the form of "sk-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
 */
public record Settings(String openAiToken) {

}
