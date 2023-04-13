package io.github.md5sha256.pls;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatapackInstaller implements DatapackHandler {

    private final Plugin plugin;

    public DatapackInstaller(Plugin plugin) {
        this.plugin = plugin;
    }

    public void acceptDatapack(String fileName, InputStream inputStream) throws IOException {
        Server server = this.plugin.getServer();
        File root = server.getWorldContainer();
        File datapackFolder = root.toPath().resolve("datapacks").toFile();
        if (!datapackFolder.isDirectory()) {
            datapackFolder.mkdirs();
        }
        File packFile = new File(datapackFolder, fileName);
        if (!packFile.exists()) {
            packFile.createNewFile();
        }
        try (OutputStream os = new FileOutputStream(packFile)) {
            inputStream.transferTo(os);
        }
    }

}
