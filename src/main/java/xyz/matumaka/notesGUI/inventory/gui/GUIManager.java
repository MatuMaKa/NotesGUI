package xyz.matumaka.notesGUI.inventory.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIManager {

    private final Map<UUID, InventoryGUI> openGUIs = new HashMap<>();

    public void openGUI(Player player, InventoryGUI gui) {
        openGUIs.put(player.getUniqueId(), gui);
        Inventory inv = gui.createInventory(player);
        player.openInventory(inv);
    }

    public InventoryGUI getOpenGUI(Player player) {
        return openGUIs.get(player.getUniqueId());
    }

    public void closeGUI(Player player) {
        openGUIs.remove(player.getUniqueId());
    }
}
