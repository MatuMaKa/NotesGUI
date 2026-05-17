package xyz.matumaka.notesGUI.listeners;

import xyz.matumaka.notesGUI.NotesGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;

public class JoinListener implements Listener {

    private final NotesGUI plugin;

    public JoinListener(NotesGUI plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (plugin.getNotesManager().hasNotes(player.getUniqueId())) {
            int count = plugin.getNotesManager().getNotes(player.getUniqueId()).size();

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.hasPermission("notesgui.notify")) {
                    plugin.getMessageManager().send(online, "notify-join", Map.of(
                            "player", player.getName(),
                            "count", String.valueOf(count)
                    ));
                }
            }
        }
    }
}