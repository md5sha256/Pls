package io.github.md5sha256.pls;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatapackHandler {

    private final Plugin plugin;

    public DatapackHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    private void registerDataPack(String name, InputStream inputStream) throws IOException {
        Server server = plugin.getServer();
        File root = server.getWorldContainer();
        File datapackFolder = root.toPath().resolve("datapacks").toFile();
        if (!datapackFolder.isDirectory()) {
            datapackFolder.mkdirs();
        }
        File packFile = new File(datapackFolder, name);
        if (!packFile.exists()) {
            packFile.createNewFile();
        }
        try (OutputStream os = new FileOutputStream(packFile)) {
            inputStream.transferTo(os);
        }
        // load the datapack
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pls");
    }

}
