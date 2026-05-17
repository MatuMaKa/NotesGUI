package xyz.matumaka.notesGUI.config;

import xyz.matumaka.notesGUI.NotesGUI;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final NotesGUI plugin;
    private String storageType;

    public ConfigManager(NotesGUI plugin) {
        this.plugin = plugin;
        load();
    }

    public String getStorageType() { return storageType; }

    public void reload() {
        load();
    }

    private void load() {
        FileConfiguration config = plugin.getConfig();
        storageType = config.getString("storage", "yml");
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }
}
