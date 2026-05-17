package xyz.matumaka.notesGUI.config;

import xyz.matumaka.notesGUI.NotesGUI;
import xyz.matumaka.notesGUI.utils.ColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public class MessageManager {

    private final NotesGUI plugin;
    private FileConfiguration messages;
    private File messagesFile;

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    public MessageManager(NotesGUI plugin) {
        this.plugin = plugin;
        load();
    }

    public void reload() {
        load();
    }

    private void load() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);

        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            messages.setDefaults(defaults);
        }
    }

    public String getRaw(String key) {
        return messages.getString(key, key);
    }

    public Component get(String key, Map<String, String> placeholders) {
        String raw = getRaw(key);
        String prefix = getRaw("prefix");
        raw = raw.replace("%prefix%", prefix);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            raw = raw.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return ColorUtil.colorize(raw);
    }

    public Component get(String key) {
        return get(key, Collections.emptyMap());
    }

    public void send(CommandSender sender, String key, Map<String, String> placeholders) {
        sender.sendMessage(get(key, placeholders));
    }

    public void send(CommandSender sender, String key) {
        send(sender, key, Collections.emptyMap());
    }
}
