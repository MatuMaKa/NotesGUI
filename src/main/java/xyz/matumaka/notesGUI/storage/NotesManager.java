package xyz.matumaka.notesGUI.storage;

import java.util.List;
import java.util.UUID;

public class NotesManager {

    private StorageManager storage;

    public NotesManager(StorageManager storage) {
        this.storage = storage;
    }

    public StorageManager getStorage() { return storage; }
    public void setStorage(StorageManager storage) { this.storage = storage; }

    public void addNote(UUID targetUUID, String targetName, String authorName, String note) {
        storage.addNote(targetUUID, targetName, authorName, note);
    }

    public List<NoteData> getNotes(UUID targetUUID) {
        return storage.getNotes(targetUUID);
    }

    public boolean removeNote(UUID targetUUID, int noteId) {
        return storage.removeNote(targetUUID, noteId);
    }

    public boolean hasNotes(UUID targetUUID) {
        return storage.hasNotes(targetUUID);
    }
}
