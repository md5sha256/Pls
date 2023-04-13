package io.github.md5sha256.pls;


import java.net.URI;

/**
 * The settings which are defined in the settings.yml
 *
 * @param endpointUri The {@link URI} Endpoint which should used to connect and interface with open-ai
 */
public record Settings(URI endpointUri) {

}
