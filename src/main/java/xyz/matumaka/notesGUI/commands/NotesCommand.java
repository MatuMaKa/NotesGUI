package xyz.matumaka.notesGUI.commands;

import xyz.matumaka.notesGUI.NotesGUI;
import xyz.matumaka.notesGUI.storage.NoteData;
import xyz.matumaka.notesGUI.inventory.NotesViewGUI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class NotesCommand implements CommandExecutor, TabCompleter {

    private final NotesGUI plugin;

    public NotesCommand(NotesGUI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("notesgui.command")) {
            plugin.getMessageManager().send(sender, "no-permission");
            return true;
        }

        if (args.length == 0) {
            plugin.getMessageManager().send(sender, "usage");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add" -> handleAdd(sender, args);
            case "view" -> handleView(sender, args);
            case "remove" -> handleRemove(sender, args);
            case "reload" -> handleReload(sender);
            default -> plugin.getMessageManager().send(sender, "usage");
        }
        return true;
    }

    private void handleAdd(CommandSender sender, String[] args) {
        if (!sender.hasPermission("notesgui.command.add")) {
            plugin.getMessageManager().send(sender, "no-permission");
            return;
        }
        if (args.length < 3) {
            plugin.getMessageManager().send(sender, "usage-add");
            return;
        }

        String targetName = args[1];
        String rawNote = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        String note = stripColorCodes(rawNote);

        OfflinePlayer target = resolveOfflinePlayer(targetName);
        if (target == null) {
            plugin.getMessageManager().send(sender, "player-not-found", Map.of("player", targetName));
            return;
        }

        String authorName = sender instanceof Player p ? p.getName() : "Console";
        plugin.getNotesManager().addNote(target.getUniqueId(), target.getName() != null ? target.getName() : targetName, authorName, note);
        plugin.getMessageManager().send(sender, "note-added", Map.of("player", target.getName() != null ? target.getName() : targetName));
    }

    private void handleView(CommandSender sender, String[] args) {
        if (!sender.hasPermission("notesgui.command.view")) {
            plugin.getMessageManager().send(sender, "no-permission");
            return;
        }
        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "usage-view");
            return;
        }
        if (args.length < 2) {
            plugin.getMessageManager().send(sender, "usage-view");
            return;
        }

        String targetName = args[1];
        OfflinePlayer target = resolveOfflinePlayer(targetName);
        if (target == null) {
            plugin.getMessageManager().send(sender, "player-not-found", Map.of("player", targetName));
            return;
        }

        String resolvedName = target.getName() != null ? target.getName() : targetName;
        NotesViewGUI gui = new NotesViewGUI(plugin, resolvedName, target.getUniqueId());
        plugin.getGuiManager().openGUI(player, gui);
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (!sender.hasPermission("notesgui.command.remove")) {
            plugin.getMessageManager().send(sender, "no-permission");
            return;
        }
        if (args.length < 3) {
            plugin.getMessageManager().send(sender, "usage-remove");
            return;
        }

        String targetName = args[1];
        String idStr = args[2];
        int noteId;
        try {
            noteId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            plugin.getMessageManager().send(sender, "invalid-id", Map.of("id", idStr));
            return;
        }

        OfflinePlayer target = resolveOfflinePlayer(targetName);
        if (target == null) {
            plugin.getMessageManager().send(sender, "player-not-found", Map.of("player", targetName));
            return;
        }

        boolean removed = plugin.getNotesManager().removeNote(target.getUniqueId(), noteId);
        if (removed) {
            plugin.getMessageManager().send(sender, "note-removed", Map.of(
                    "id", String.valueOf(noteId),
                    "player", target.getName() != null ? target.getName() : targetName
            ));
        } else {
            plugin.getMessageManager().send(sender, "note-not-found", Map.of("id", String.valueOf(noteId)));
        }
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("notesgui.command.reload")) {
            plugin.getMessageManager().send(sender, "no-permission");
            return;
        }
        plugin.reload();
        plugin.getMessageManager().send(sender, "reloaded");
    }

    private String stripColorCodes(String text) {
        return text.replaceAll("(?i)[&§][0-9a-fk-or]", "");
    }

    @SuppressWarnings("deprecation")
    private OfflinePlayer resolveOfflinePlayer(String name) {
        Player online = Bukkit.getPlayerExact(name);
        if (online != null) return online;

        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if (op.getName() != null && op.getName().equalsIgnoreCase(name)) {
                return op;
            }
        }
        return null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subs = new ArrayList<>();
            if (sender.hasPermission("notesgui.command.add")) subs.add("add");
            if (sender.hasPermission("notesgui.command.view")) subs.add("view");
            if (sender.hasPermission("notesgui.command.remove")) subs.add("remove");
            if (sender.hasPermission("notesgui.command.reload")) subs.add("reload");
            return subs.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("add") || sub.equals("view") || sub.equals("remove")) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
            OfflinePlayer target = Bukkit.getPlayerExact(args[1]);
            if (target != null) {
                return plugin.getNotesManager().getNotes(target.getUniqueId()).stream()
                        .map(n -> String.valueOf(n.getId()))
                        .filter(id -> id.startsWith(args[2]))
                        .collect(Collectors.toList());
            }
        }

        return new ArrayList<>();
    }
}