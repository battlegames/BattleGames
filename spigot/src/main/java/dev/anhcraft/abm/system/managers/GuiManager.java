package dev.anhcraft.abm.system.managers;

import dev.anhcraft.abif.ABIF;
import dev.anhcraft.abm.BattlePlugin;
import dev.anhcraft.abm.api.ext.BattleComponent;
import dev.anhcraft.abm.gui.core.*;
import dev.anhcraft.abm.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class GuiManager extends BattleComponent {
    private final Map<String, BattleGuiHandler> GUI_HANDLER = new HashMap<>();
    private final Map<String, BattleGui> GUI = new HashMap<>();
    private final Map<Player, PlayerGui> PLAYER_INV = new HashMap<>();

    public GuiManager(BattlePlugin plugin) {
        super(plugin);
    }

    public boolean callSlotHandler(Player player, PlayerGui apg, BattleSlot slot){
        if(slot.getHandler() == null) return true;
        String[] qr = slot.getHandler().split("::");
        return callSlotHandler(player, apg, qr[0], qr[1]);
    }

    public boolean callSlotHandler(InventoryClickEvent event, PlayerGui apg, BattleSlot slot){
        if(slot.getHandler() == null) return true;
        String[] qr = slot.getHandler().split("::");
        return callSlotHandler(event, apg, qr[0], qr[1]);
    }

    public boolean callSlotHandler(Player player, PlayerGui apg, String ghl, String shl){
        BattleGuiHandler gh = GUI_HANDLER.get(ghl);
        if(gh != null){
            Method[] methods = gh.getClass().getDeclaredMethods();
            try {
                for (Method m : methods) {
                    if (m.isAnnotationPresent(SlotHandler.class)){
                        SlotHandler sch = m.getDeclaredAnnotation(SlotHandler.class);
                        if(sch.value().equals(shl)) {
                            int c = m.getParameterCount();
                            Object o = null;
                            if (c == 1) o = m.invoke(gh, player);
                            else if (c == 2) o = m.invoke(gh, player, apg);
                            if(o != null) return !(o instanceof Boolean) || (boolean) o;
                        }
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public boolean callSlotHandler(InventoryClickEvent event, PlayerGui apg, String ghl, String shl){
        BattleGuiHandler gh = GUI_HANDLER.get(ghl);
        if(gh != null){
            Method[] methods = gh.getClass().getDeclaredMethods();
            try {
                for (Method m : methods) {
                    if (m.isAnnotationPresent(SlotClickHandler.class)){
                        SlotClickHandler sch = m.getDeclaredAnnotation(SlotClickHandler.class);
                        if(sch.value().equals(shl)) {
                            int c = m.getParameterCount();
                            Object o = null;
                            if (c == 1) o = m.invoke(gh, event);
                            else if (c == 2) o = m.invoke(gh, event, apg);
                            if(o != null) return !(o instanceof Boolean) || (boolean) o;
                        }
                    }
                    else if (m.isAnnotationPresent(SlotHandler.class)){
                        SlotHandler sch = m.getDeclaredAnnotation(SlotHandler.class);
                        if(sch.value().equals(shl)) {
                            int c = m.getParameterCount();
                            Player p = (Player) event.getWhoClicked();
                            Object o = null;
                            if (c == 1) o = m.invoke(gh, p);
                            else if (c == 2) o = m.invoke(gh, p, apg);
                            if(o != null) return !(o instanceof Boolean) || (boolean) o;
                        }
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void registerGuiHandler(String id, BattleGuiHandler handler){
        GUI_HANDLER.put(id, handler);
    }

    public void registerGUI(String id, BattleGui gui){
        GUI.put(id, gui);
        BattlePagination pg = gui.getPagination();
        if(pg != null){
            pg.getHeaderLore().replaceAll(s -> StringUtil.localizeString(s, plugin.getLocaleConf()));
            pg.getFooterLore().replaceAll(s -> StringUtil.localizeString(s, plugin.getLocaleConf()));
        }
    }

    @NotNull
    public PlayerGui getGui(Player player){
        PlayerGui x = PLAYER_INV.get(player);
        if(x == null){
            x = new PlayerGui();
            PLAYER_INV.put(player, x);
        }
        return x;
    }

    private ItemStack[] drawItems(Player player, PlayerGui apg, BattleGui gui){
        ItemStack[] items = new ItemStack[gui.getSize()];
        if(gui.getBackground() != null) {
            ItemStack item = ABIF.load(gui.getBackground(),
                    s -> formatString(s, player),
                    ls -> formatStrings(ls, player));
            Arrays.fill(items, item);
        }
        BattleSlot[] bs = gui.getSlots();
        for(int i = 0; i < bs.length; i++){
            BattleSlot x = bs[i];
            if(x != null && x.getItem() != null) {
                items[i] = ABIF.load(x.getItem(),
                        s -> formatString(s, player),
                        ls -> formatStrings(ls, player));
            }
        }
        BattlePagination pg = gui.getPagination();
        if(pg != null) {
            BattleGuiHandler gh = GUI_HANDLER.get(pg.getHandler());
            if (gh instanceof PaginationHandler) {
                apg.getSlot2DataIndexes().clear();
                int[] slots = pg.getSlots();
                int dataIndex = apg.getPage() * slots.length;
                List<ItemStack> pgi = new LinkedList<>();
                ((PaginationHandler) gh).getData(player, apg, pg, dataIndex, dataIndex + slots.length - 1, pgi);
                apg.setOutOfData(pgi.size() < slots.length);
                ListIterator<ItemStack> it = pgi.listIterator();
                int maxSlots = Math.min(slots.length, pgi.size());
                for (int i = 0; i < maxSlots; i++) {
                    items[slots[i]] = it.next();
                    apg.getSlot2DataIndexes().put(slots[i], dataIndex++);
                }
            }
        }
        return items;
    }

    public void setPlayerInventory(Player player, String name){
        PlayerGui gui = getGui(player);
        gui.setInternalInventory(GUI.get(name));
        setPlayerInventory(player, getGui(player));
    }

    public void setPlayerInventory(Player player, PlayerGui apg){
        BattleGui bg = apg.getInternalInventory();
        if(bg == null) return;
        ItemStack[] items = drawItems(player, apg, bg);
        for(int i = 0; i < items.length; i++) player.getInventory().setItem(i, items[i]);
    }

    public void openInventory(Player player, String name){
        PlayerGui gui = getGui(player);
        gui.setGui(GUI.get(name));
        openInventory(player, gui);
    }

    public void openInventory(Player player, PlayerGui apg){
        BattleGui gui = apg.getGui();
        if(gui == null) return;
        Inventory inv;
        if(gui.getTitle() == null) inv = Bukkit.createInventory(null, gui.getSize());
        else inv = Bukkit.createInventory(null, gui.getSize(), formatString(gui.getTitle(), player));
        ItemStack[] items = drawItems(player, apg, gui);
        inv.setContents(items);
        apg.setInventory(inv);
        player.openInventory(inv);
        if(gui.getSound() != null)
            player.playSound(player.getLocation(), gui.getSound(), 2f, 1f);
    }

    private List<String> formatStrings(List<String> ls, Player player) {
        return StringUtil.formatPlaceholders(player, StringUtil.localizeStrings(ls, plugin.getLocaleConf()));
    }

    private String formatString(String s, Player player) {
        return StringUtil.formatPlaceholders(player, StringUtil.localizeString(s, plugin.getLocaleConf()));
    }

    public void destroyGui(Player player){
        PLAYER_INV.remove(player);
    }
}
