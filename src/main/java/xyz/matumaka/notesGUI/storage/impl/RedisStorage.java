package xyz.matumaka.notesGUI.storage.impl;

import xyz.matumaka.notesGUI.NotesGUI;
import xyz.matumaka.notesGUI.storage.NoteData;
import xyz.matumaka.notesGUI.storage.StorageManager;
import org.bukkit.configuration.file.FileConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RedisStorage implements StorageManager {

    private final NotesGUI plugin;
    private JedisPool jedisPool;

    public RedisStorage(NotesGUI plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        FileConfiguration config = plugin.getConfig();
        String host = config.getString("redis.host", "localhost");
        int port = config.getInt("redis.port", 6379);
        String password = config.getString("redis.password", "");

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(8);

        if (password != null && !password.isEmpty()) {
            jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
        } else {
            jedisPool = new JedisPool(poolConfig, host, port, 2000);
        }
    }

    @Override
    public void close() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
    }

    // Key pattern: notesgui:notes:{uuid}:{id} -> hash of fields
    // Key pattern: notesgui:counter:{uuid} -> auto-increment counter

    @Override
    public void addNote(UUID targetUUID, String targetName, String authorName, String note) {
        try (Jedis jedis = jedisPool.getResource()) {
            String counterKey = "notesgui:counter:" + targetUUID;
            long id = jedis.incr(counterKey);

            String noteKey = "notesgui:notes:" + targetUUID + ":" + id;
            jedis.hset(noteKey, "id", String.valueOf(id));
            jedis.hset(noteKey, "target", targetName);
            jedis.hset(noteKey, "author", authorName);
            jedis.hset(noteKey, "note", note);
            jedis.hset(noteKey, "timestamp", String.valueOf(System.currentTimeMillis()));

            jedis.sadd("notesgui:index:" + targetUUID, String.valueOf(id));
        }
    }

    @Override
    public List<NoteData> getNotes(UUID targetUUID) {
        List<NoteData> notes = new ArrayList<>();
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> ids = jedis.smembers("notesgui:index:" + targetUUID);
            for (String idStr : ids) {
                String noteKey = "notesgui:notes:" + targetUUID + ":" + idStr;
                Map<String, String> fields = jedis.hgetAll(noteKey);
                if (fields.isEmpty()) continue;

                int id = Integer.parseInt(fields.getOrDefault("id", "0"));
                String target = fields.getOrDefault("target", "Unknown");
                String author = fields.getOrDefault("author", "Unknown");
                String noteText = fields.getOrDefault("note", "");
                long timestamp = Long.parseLong(fields.getOrDefault("timestamp", "0"));

                notes.add(new NoteData(id, targetUUID, target, author, noteText, timestamp));
            }
        }
        return notes;
    }

    @Override
    public boolean removeNote(UUID targetUUID, int noteId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String noteKey = "notesgui:notes:" + targetUUID + ":" + noteId;
            long deleted = jedis.del(noteKey);
            jedis.srem("notesgui:index:" + targetUUID, String.valueOf(noteId));
            return deleted > 0;
        }
    }

    @Override
    public boolean hasNotes(UUID targetUUID) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.scard("notesgui:index:" + targetUUID) > 0;
        }
    }
}
