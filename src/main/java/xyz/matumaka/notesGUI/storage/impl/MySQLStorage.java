package xyz.matumaka.notesGUI.storage.impl;

import xyz.matumaka.notesGUI.NotesGUI;
import xyz.matumaka.notesGUI.storage.NoteData;
import xyz.matumaka.notesGUI.storage.StorageManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySQLStorage implements StorageManager {

    private final NotesGUI plugin;
    private HikariDataSource dataSource;

    public MySQLStorage(NotesGUI plugin) {
        this.plugin = plugin;
    }

    @Override
    public void init() {
        FileConfiguration config = plugin.getConfig();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + config.getString("mysql.host", "localhost")
                + ":" + config.getInt("mysql.port", 3306)
                + "/" + config.getString("mysql.database", "notesgui")
                + "?useSSL=false&autoReconnect=true");
        hikariConfig.setUsername(config.getString("mysql.username", "root"));
        hikariConfig.setPassword(config.getString("mysql.password", ""));
        hikariConfig.setMaximumPoolSize(config.getInt("mysql.pool-size", 5));
        hikariConfig.setPoolName("NotesGUI-MySQL");

        dataSource = new HikariDataSource(hikariConfig);
        createTable();
    }

    private void createTable() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS notes (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "target_uuid VARCHAR(36) NOT NULL," +
                            "target_name VARCHAR(16) NOT NULL," +
                            "author_name VARCHAR(16) NOT NULL," +
                            "note TEXT NOT NULL," +
                            "timestamp BIGINT NOT NULL" +
                            ")"
            );
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create notes table: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public void addNote(UUID targetUUID, String targetName, String authorName, String note) {
        String sql = "INSERT INTO notes (target_uuid, target_name, author_name, note, timestamp) VALUES (?,?,?,?,?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, targetUUID.toString());
            ps.setString(2, targetName);
            ps.setString(3, authorName);
            ps.setString(4, note);
            ps.setLong(5, System.currentTimeMillis());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to add note: " + e.getMessage());
        }
    }

    @Override
    public List<NoteData> getNotes(UUID targetUUID) {
        List<NoteData> notes = new ArrayList<>();
        String sql = "SELECT id, target_name, author_name, note, timestamp FROM notes WHERE target_uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, targetUUID.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                notes.add(new NoteData(
                        rs.getInt("id"),
                        targetUUID,
                        rs.getString("target_name"),
                        rs.getString("author_name"),
                        rs.getString("note"),
                        rs.getLong("timestamp")
                ));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get notes: " + e.getMessage());
        }
        return notes;
    }

    @Override
    public boolean removeNote(UUID targetUUID, int noteId) {
        String sql = "DELETE FROM notes WHERE id = ? AND target_uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, noteId);
            ps.setString(2, targetUUID.toString());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to remove note: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean hasNotes(UUID targetUUID) {
        String sql = "SELECT COUNT(*) FROM notes WHERE target_uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, targetUUID.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to check notes: " + e.getMessage());
        }
        return false;
    }
}