package io.github.md5sha256.pls;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

public final class PlsPlugin extends JavaPlugin {

    private Settings settings;
    private RequestExecutor requestExecutor;

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
        this.requestExecutor = new RequestExecutor(this, settings.openAiToken());
        getCommand("pls").setExecutor(new PlsCommand(this.requestExecutor));
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
        if (!file.exists()) {
            file.mkdirs();
            try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("settings.yml");
                 OutputStream os = new FileOutputStream(file)) {
                if (in == null) {
                    this.getLogger().log(Level.SEVERE, () -> "Failed to load default settings.yml");
                    return null;
                }
                in.transferTo(os);
                this.getLogger().log(Level.SEVERE, () -> "Missing open-ai token. Please add your token in the settings.yml");
                return null;
            } catch (IOException ex) {
                this.getLogger().log(Level.SEVERE, "Failed to load default settings.yml", ex);
                return null;
            }
        }
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        String rawToken = yamlConfiguration.getString("open-ai-token");
        if (rawToken == null) {
            this.getLogger().log(Level.SEVERE, () -> "Missing key open-ai-token");
            return null;
        }
        return new Settings(rawToken);
    }
}
