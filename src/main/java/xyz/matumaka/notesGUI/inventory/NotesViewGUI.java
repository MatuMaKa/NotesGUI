package xyz.matumaka.notesGUI.inventory;

import xyz.matumaka.notesGUI.NotesGUI;
import xyz.matumaka.notesGUI.inventory.gui.InventoryGUI;
import xyz.matumaka.notesGUI.storage.NoteData;
import xyz.matumaka.notesGUI.utils.ColorUtil;
import com.cryptomorin.xseries.XMaterial;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class NotesViewGUI extends InventoryGUI {

    private final NotesGUI plugin;
    private final String targetName;
    private final UUID targetUUID;
    private int page = 0;

    private static final int[] NOTE_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private static final int PREV_SLOT = 48;
    private static final int PAGE_SLOT = 49;
    private static final int NEXT_SLOT = 50;

    public NotesViewGUI(NotesGUI plugin, String targetName, UUID targetUUID) {
        this.plugin = plugin;
        this.targetName = targetName;
        this.targetUUID = targetUUID;
    }

    @Override
    public Inventory createInventory(Player player) {
        String title = plugin.getConfigManager().getConfig().getString("gui.title", "&8Notes - %player%");
        title = title.replace("%player%", targetName);

        Inventory inv = Bukkit.createInventory(null, 54, ColorUtil.colorize(title));
        fillBorder(inv);
        fillNotes(inv, plugin.getNotesManager().getNotes(targetUUID));
        return inv;
    }

    private void fillBorder(Inventory inv) {
        String cornerMat = plugin.getConfigManager().getConfig().getString("gui.border.corner-material", "YELLOW_STAINED_GLASS_PANE");
        String borderMat = plugin.getConfigManager().getConfig().getString("gui.border.material", "GRAY_STAINED_GLASS_PANE");
        String cornerName = plugin.getConfigManager().getConfig().getString("gui.border.corner-name", " ");
        String borderName = plugin.getConfigManager().getConfig().getString("gui.border.name", " ");
        boolean hideTooltip = plugin.getConfigManager().getConfig().getBoolean("gui.border.hide-tooltip", true);

        ItemStack corner = createGlassItem(cornerMat, cornerName, hideTooltip);
        ItemStack border = createGlassItem(borderMat, borderName, hideTooltip);

        for (int slot : new int[]{0, 8, 45, 53}) inv.setItem(slot, corner);
        for (int i = 1; i <= 7; i++) inv.setItem(i, border);
        for (int i = 46; i <= 52; i++) inv.setItem(i, border);
        for (int row = 1; row <= 4; row++) {
            inv.setItem(row * 9, border);
            inv.setItem(row * 9 + 8, border);
        }
    }

    private void fillNotes(Inventory inv, List<NoteData> notes) {
        int perPage = NOTE_SLOTS.length;
        int totalPages = Math.max(1, (int) Math.ceil((double) notes.size() / perPage));
        int start = page * perPage;

        boolean fillerEnabled = plugin.getConfigManager().getConfig().getBoolean("gui.filler.enabled", true);
        String fillerMat = plugin.getConfigManager().getConfig().getString("gui.filler.material", "GRAY_STAINED_GLASS_PANE");
        String fillerName = plugin.getConfigManager().getConfig().getString("gui.filler.name", " ");
        boolean fillerHideTooltip = plugin.getConfigManager().getConfig().getBoolean("gui.filler.hide-tooltip", true);
        boolean fillerIsAir = fillerMat.equalsIgnoreCase("AIR");

        for (int i = 0; i < NOTE_SLOTS.length; i++) {
            int noteIndex = start + i;
            if (noteIndex < notes.size()) {
                inv.setItem(NOTE_SLOTS[i], createNoteItem(notes.get(noteIndex)));
            } else {
                if (!fillerEnabled || fillerIsAir) {
                    inv.setItem(NOTE_SLOTS[i], null);
                } else {
                    inv.setItem(NOTE_SLOTS[i], createGlassItem(fillerMat, fillerName, fillerHideTooltip));
                }
            }
        }

        inv.setItem(PREV_SLOT, null);
        inv.setItem(NEXT_SLOT, null);

        if (page > 0) {
            inv.setItem(PREV_SLOT, createNavItem(
                    plugin.getConfigManager().getConfig().getString("gui.navigation.prev-material", "ARROW"),
                    plugin.getConfigManager().getConfig().getString("gui.navigation.prev-name", "&ePrevious Page")
            ));
        }

        String indicatorName = plugin.getConfigManager().getConfig().getString("gui.navigation.page-indicator-name", "&7Page &f%page% &7/ &f%total%");
        indicatorName = indicatorName.replace("%page%", String.valueOf(page + 1)).replace("%total%", String.valueOf(totalPages));
        inv.setItem(PAGE_SLOT, createNavItem(
                plugin.getConfigManager().getConfig().getString("gui.navigation.page-indicator-material", "PAPER"),
                indicatorName
        ));

        if (page < totalPages - 1) {
            inv.setItem(NEXT_SLOT, createNavItem(
                    plugin.getConfigManager().getConfig().getString("gui.navigation.next-material", "ARROW"),
                    plugin.getConfigManager().getConfig().getString("gui.navigation.next-name", "&eNext Page")
            ));
        }
    }

    private ItemStack createGlassItem(String materialName, String name, boolean hideTooltip) {
        ItemStack item = XMaterial.matchXMaterial(materialName).map(XMaterial::parseItem).orElse(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ColorUtil.colorize(name));
            meta.setHideTooltip(hideTooltip);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createNavItem(String materialName, String name) {
        ItemStack item = XMaterial.matchXMaterial(materialName).map(XMaterial::parseItem).orElse(new ItemStack(Material.PAPER));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ColorUtil.colorize(name));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createNoteItem(NoteData note) {
        String materialName = plugin.getConfigManager().getConfig().getString("gui.note-item.material", "PAPER");
        ItemStack item = XMaterial.matchXMaterial(materialName).map(XMaterial::parseItem).orElse(new ItemStack(Material.PAPER));

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = plugin.getConfigManager().getConfig().getString("gui.note-item.name", "&eNote #%id%");
            meta.displayName(ColorUtil.colorize(name.replace("%id%", String.valueOf(note.getId()))));

            List<String> loreTemplate = plugin.getConfigManager().getConfig().getStringList("gui.note-item.lore");
            List<Component> lore = new ArrayList<>();
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(note.getTimestamp()));

            for (String line : loreTemplate) {
                line = line.replace("%id%", String.valueOf(note.getId()));
                line = line.replace("%author%", note.getAuthorName());
                line = line.replace("%date%", date);
                line = line.replace("%note%", note.getNote());
                lore.add(ColorUtil.colorize(line));
            }

            meta.lore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;

        int slot = event.getSlot();

        if (slot == PREV_SLOT && page > 0) {
            page--;
            fillNotes(event.getInventory(), plugin.getNotesManager().getNotes(targetUUID));
            return;
        }

        if (slot == NEXT_SLOT) {
            List<NoteData> notes = plugin.getNotesManager().getNotes(targetUUID);
            int totalPages = Math.max(1, (int) Math.ceil((double) notes.size() / NOTE_SLOTS.length));
            if (page < totalPages - 1) {
                page++;
                fillNotes(event.getInventory(), notes);
            }
            return;
        }

        for (int i = 0; i < NOTE_SLOTS.length; i++) {
            if (slot == NOTE_SLOTS[i]) {
                List<NoteData> notes = plugin.getNotesManager().getNotes(targetUUID);
                int noteIndex = page * NOTE_SLOTS.length + i;
                if (noteIndex < notes.size()) {
                    NoteData note = notes.get(noteIndex);
                    if (player.hasPermission("notesgui.remove")) {
                        plugin.getNotesManager().removeNote(targetUUID, note.getId());
                        fillNotes(event.getInventory(), plugin.getNotesManager().getNotes(targetUUID));
                        plugin.getMessageManager().send(player, "note-removed", java.util.Map.of(
                                "id", String.valueOf(note.getId()),
                                "player", targetName
                        ));
                    }
                }
                return;
            }
        }
    }
}