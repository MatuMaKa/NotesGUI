package xyz.matumaka.notesGUI.inventory.gui;

import xyz.matumaka.notesGUI.inventory.InventoryHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public abstract class InventoryGUI implements InventoryHandler {

    public abstract Inventory createInventory(Player player);

    @Override
    public void onClick(InventoryClickEvent event) {
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }
}
