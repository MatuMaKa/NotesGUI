package xyz.matumaka.notesGUI.inventory.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class GUIListener implements Listener {

    private final GUIManager guiManager;

    public GUIListener(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        InventoryGUI gui = guiManager.getOpenGUI(player);
        if (gui != null) {
            event.setCancelled(true);
            gui.onClick(event);
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        InventoryGUI gui = guiManager.getOpenGUI(player);
        if (gui != null) {
            gui.onOpen(event);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        InventoryGUI gui = guiManager.getOpenGUI(player);
        if (gui != null) {
            gui.onClose(event);
            guiManager.closeGUI(player);
        }
    }
}