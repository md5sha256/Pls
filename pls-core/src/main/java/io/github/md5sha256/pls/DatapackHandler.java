package io.github.md5sha256.pls;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface DatapackHandler {

    void acceptDatapack(String fileName, InputStream inputStream) throws IOException;

}
