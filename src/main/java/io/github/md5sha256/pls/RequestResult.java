package io.github.md5sha256.pls;


/**
 * Represents the result of a request to OpenAi
 *
 * @param response The string response from the web request, or an error message
 * @param error    If there was an error in parsing the request
 */
public record RequestResult(String response, boolean error) {
}
