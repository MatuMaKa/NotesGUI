package xyz.matumaka.notesGUI.storage;

import java.util.List;
import java.util.UUID;

public interface StorageManager {
    void init();
    void close();

    void addNote(UUID targetUUID, String targetName, String authorName, String note);
    List<NoteData> getNotes(UUID targetUUID);
    boolean removeNote(UUID targetUUID, int noteId);
    boolean hasNotes(UUID targetUUID);
}
