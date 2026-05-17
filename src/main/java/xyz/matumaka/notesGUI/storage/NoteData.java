package xyz.matumaka.notesGUI.storage;

import java.util.UUID;

public class NoteData {
    private int id;
    private UUID targetUUID;
    private String targetName;
    private String authorName;
    private String note;
    private long timestamp;

    public NoteData(int id, UUID targetUUID, String targetName, String authorName, String note, long timestamp) {
        this.id = id;
        this.targetUUID = targetUUID;
        this.targetName = targetName;
        this.authorName = authorName;
        this.note = note;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public UUID getTargetUUID() { return targetUUID; }
    public String getTargetName() { return targetName; }
    public String getAuthorName() { return authorName; }
    public String getNote() { return note; }
    public long getTimestamp() { return timestamp; }

    public void setId(int id) { this.id = id; }
    public void setTargetUUID(UUID targetUUID) { this.targetUUID = targetUUID; }
    public void setTargetName(String targetName) { this.targetName = targetName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setNote(String note) { this.note = note; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
