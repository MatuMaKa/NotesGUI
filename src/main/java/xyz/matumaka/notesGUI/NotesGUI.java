package xyz.matumaka.notesGUI;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.matumaka.notesGUI.config.ConfigManager;
import xyz.matumaka.notesGUI.inventory.gui.GUIListener;
import xyz.matumaka.notesGUI.listeners.JoinListener;
import xyz.matumaka.notesGUI.storage.NotesManager;
import xyz.matumaka.notesGUI.storage.StorageManager;
import xyz.matumaka.notesGUI.config.MessageManager;
import xyz.matumaka.notesGUI.inventory.gui.GUIManager;
import xyz.matumaka.notesGUI.commands.NotesCommand;
import xyz.matumaka.notesGUI.storage.impl.MySQLStorage;
import xyz.matumaka.notesGUI.storage.impl.RedisStorage;
import xyz.matumaka.notesGUI.storage.impl.YMLStorage;

public final class NotesGUI extends JavaPlugin {
    private static NotesGUI instance;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private GUIManager guiManager;
    private NotesManager notesManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);

        StorageManager storage = buildStorage();
        storage.init();
        notesManager = new NotesManager(storage);

        guiManager = new GUIManager();

        getServer().getPluginManager().registerEvents(new GUIListener(guiManager), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);

        NotesCommand notesCommand = new NotesCommand(this);
        getCommand("notes").setExecutor(notesCommand);
        getCommand("notes").setTabCompleter(notesCommand);

        getLogger().info("NotesGUI enabled.");
    }

    @Override
    public void onDisable() {
        if (notesManager != null) {
            notesManager.getStorage().close();
        }
        getLogger().info("NotesGUI disabled.");
    }

    public void reload() {
        reloadConfig();
        configManager.reload();
        messageManager.reload();

        if (notesManager != null) {
            StorageManager oldStorage = notesManager.getStorage();
            StorageManager newStorage = buildStorage();
            newStorage.init();
            notesManager.setStorage(newStorage);
            oldStorage.close();
        }
    }

    private StorageManager buildStorage() {
        return switch (configManager.getStorageType().toLowerCase()) {
            case "mysql" -> new MySQLStorage(this);
            case "redis" -> new RedisStorage(this);
            default -> new YMLStorage(this);
        };
    }

    public static NotesGUI getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public NotesManager getNotesManager() {
        return notesManager;
    }
}
