package xyz.matumaka.notesGUI.storage.impl;

import xyz.matumaka.notesGUI.NotesGUI;
import xyz.matumaka.notesGUI.storage.NoteData;
import xyz.matumaka.notesGUI.storage.StorageManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class YMLStorage implements StorageManager {

    private final NotesGUI plugin;
    private File dataFile;
    private FileConfiguration data;

    public YMLStorage(NotesGUI plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        dataFile = new File(plugin.getDataFolder(), "notes.yml");
        if (!dataFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create notes.yml: " + e.getMessage());
            }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
    }

    @Override
    public void close() {
        save();
    }

    private void save() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save notes.yml: " + e.getMessage());
        }
    }

    @Override
    public void addNote(UUID targetUUID, String targetName, String authorName, String note) {
        String path = "notes." + targetUUID.toString();
        List<java.util.Map<?, ?>> existing = data.getMapList(path);

        int nextId = existing.size() + 1;
        // Ensure unique ID even if entries were removed
        for (java.util.Map<?, ?> entry : existing) {
            Object idObj = entry.get("id");
            if (idObj instanceof Number) {
                int id = ((Number) idObj).intValue();
                if (id >= nextId) nextId = id + 1;
            }
        }

        java.util.Map<String, Object> entry = new java.util.LinkedHashMap<>();
        entry.put("id", nextId);
        entry.put("target", targetName);
        entry.put("author", authorName);
        entry.put("note", note);
        entry.put("timestamp", System.currentTimeMillis());

        existing.add(entry);
        data.set(path, existing);
        save();
    }

    @Override
    public List<NoteData> getNotes(UUID targetUUID) {
        String path = "notes." + targetUUID.toString();
        List<java.util.Map<?, ?>> raw = data.getMapList(path);
        List<NoteData> notes = new ArrayList<>();

        for (java.util.Map<?, ?> entry : raw) {
            int id = entry.get("id") instanceof Number ? ((Number) entry.get("id")).intValue() : 0;
            Object targetObj = entry.get("target");
            String target = targetObj != null ? String.valueOf(targetObj) : "Unknown";
            Object authorObj = entry.get("author");
            String author = authorObj != null ? String.valueOf(authorObj) : "Unknown";
            Object noteObj = entry.get("note");
            String noteText = noteObj != null ? String.valueOf(noteObj) : "";
            long timestamp = entry.get("timestamp") instanceof Number ? ((Number) entry.get("timestamp")).longValue() : 0L;
            notes.add(new NoteData(id, targetUUID, target, author, noteText, timestamp));
        }

        return notes;
    }

    @Override
    public boolean removeNote(UUID targetUUID, int noteId) {
        String path = "notes." + targetUUID.toString();
        List<java.util.Map<?, ?>> raw = data.getMapList(path);
        boolean removed = raw.removeIf(entry -> {
            Object idObj = entry.get("id");
            return idObj instanceof Number && ((Number) idObj).intValue() == noteId;
        });
        if (removed) {
            data.set(path, raw);
            save();
        }
        return removed;
    }

    @Override
    public boolean hasNotes(UUID targetUUID) {
        String path = "notes." + targetUUID.toString();
        List<java.util.Map<?, ?>> raw = data.getMapList(path);
        return !raw.isEmpty();
    }
}
