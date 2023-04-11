package io.github.md5sha256.pls;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;


public final class PlsPlugin extends JavaPlugin {

    private Settings settings;
    private Endpoint endpoint;

    @Override
    public void onLoad() {
        super.onLoad();
        // Plugin startup logic
        settings = loadSettings();
        if (settings == null) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        // Make a new request executor
        try {
            this.endpoint = new Endpoint(this.getLogger(), new URI(this.settings.endpointUri()));
        } catch (URISyntaxException ex) {
            this.getLogger().severe("Invalid endpoint URI");
            ex.printStackTrace();
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // Set up the command executor for the pls command
        getCommand("pls").setExecutor(new PlsCommand(this.endpoint, this));
        this.getLogger().info("Pls Plugin enabled successfully");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // Plugin shutdown logic
        this.getLogger().info("Pls plugin disabled");
    }

    private Settings loadSettings() {
        File file = getDataFolder().toPath().resolve("settings.yml").toFile();
        // Create default settings file if one doesn't exist
        if (!file.exists()) {
            getDataFolder().mkdirs();
            // Load the default settings file from jar
            try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("settings.yml");
                 OutputStream os = new FileOutputStream(file)) {
                if (in == null) {
                    this.getLogger().log(Level.SEVERE, () -> "Failed to load default settings.yml");
                    return null;
                }
                file.createNewFile();
                in.transferTo(os);
                this.getLogger().log(Level.SEVERE, () -> "Missing open-ai token. Please add your token in the settings.yml");
                return null;
            } catch (IOException ex) {
                this.getLogger().log(Level.SEVERE, "Failed to load default settings.yml", ex);
                return null;
            }
        }
        // Load the settings into memory
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        String rawToken = yamlConfiguration.getString("endpoint");
        if (rawToken == null) {
            this.getLogger().log(Level.SEVERE, () -> "Missing endpoint");
            return null;
        }
        // create the settings object
        return new Settings(rawToken);
    }
}
