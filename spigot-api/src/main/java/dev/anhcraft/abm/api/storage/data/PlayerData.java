/*
 *
 *     Battle Minigame.
 *     Copyright (c) 2019 by anhcraft.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */
package dev.anhcraft.abm.api.storage.data;

import dev.anhcraft.abm.api.inventory.PlayerInventory;
import dev.anhcraft.abm.api.inventory.ItemStorage;
import dev.anhcraft.abm.api.storage.Serializable;
import dev.anhcraft.abm.api.storage.tags.StringTag;
import dev.anhcraft.abm.api.inventory.items.ItemType;
import dev.anhcraft.abm.api.misc.Resettable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PlayerData implements Resettable, Serializable {
    private AtomicInteger headshotCounter = new AtomicInteger();
    private AtomicInteger killCounter = new AtomicInteger();
    private AtomicInteger deathCounter = new AtomicInteger();
    private AtomicInteger assistCounter = new AtomicInteger();
    private AtomicInteger winCounter = new AtomicInteger();
    private AtomicInteger loseCounter = new AtomicInteger();
    private AtomicLong exp = new AtomicLong();
    private PlayerInventory inventory = new PlayerInventory();
    private Map<String, Long> kits = new ConcurrentHashMap<>();

    @NotNull
    public AtomicInteger getHeadshotCounter() {
        return headshotCounter;
    }

    @NotNull
    public AtomicInteger getAssistCounter() {
        return assistCounter;
    }

    @NotNull
    public AtomicInteger getKillCounter() {
        return killCounter;
    }

    @NotNull
    public AtomicInteger getDeathCounter() {
        return deathCounter;
    }

    @NotNull
    public AtomicInteger getWinCounter() {
        return winCounter;
    }

    @NotNull
    public AtomicInteger getLoseCounter() {
        return loseCounter;
    }

    @NotNull
    public AtomicLong getExp() {
        return exp;
    }

    @NotNull
    public PlayerInventory getInventory() {
        return inventory;
    }

    @NotNull
    public Map<String, Long> getKits() {
        return kits;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(DataMap<String> map) {
        headshotCounter.set(map.readTag("hs", Integer.class));
        assistCounter.set(map.readTag("ast", Integer.class));
        killCounter.set(map.readTag("kill", Integer.class));
        deathCounter.set(map.readTag("death", Integer.class));
        winCounter.set(map.readTag("win", Integer.class));
        loseCounter.set(map.readTag("lose", Integer.class));
        exp.set(map.readTag("exp", Long.class));
        map.readTag("inv", List.class).forEach(o -> {
            String q = ((StringTag) o).getValue();
            ItemStorage storage = inventory.getStorage(ItemType.valueOf(q));
            map.readTag("inv."+q, List.class).forEach(o1 -> {
                String v = ((StringTag) o1).getValue();
                long t = map.readTag("inv."+q+"."+v, Long.class);
                storage.put(v, t);
            });
        });
        map.readTag("kits", List.class).forEach(o -> {
            String k = ((StringTag) o).getValue();
            kits.put(k, map.readTag("kit."+k, Long.class));
        });
    }

    @Override
    public void write(DataMap<String> map) {
        map.writeTag("hs", headshotCounter.get());
        map.writeTag("ast", assistCounter.get());
        map.writeTag("kill", killCounter.get());
        map.writeTag("death", deathCounter.get());
        map.writeTag("win", winCounter.get());
        map.writeTag("lose", loseCounter.get());
        map.writeTag("exp", exp.get());
        List<StringTag> inv = new ArrayList<>();
        inventory.getAllStorage().forEach(e -> {
            String s = e.getKey().name();
            inv.add(new StringTag(s));
            List<StringTag> items = new ArrayList<>();
            e.getValue().list().forEach(i -> {
                items.add(new StringTag(i.getKey()));
                map.writeTag("inv."+s+"."+i.getKey(), i.getValue());
            });
            map.writeTag("inv."+s, items);
        });
        map.writeTag("inv", inv);
        List<StringTag> kts = new ArrayList<>();
        kits.forEach((key, value) -> {
            kts.add(new StringTag(key));
            map.writeTag("kit."+key, value);
        });
        map.writeTag("kits", kts);
    }

    @Override
    public void reset() {
        headshotCounter.set(0);
        killCounter.set(0);
        deathCounter.set(0);
        winCounter.set(0);
        loseCounter.set(0);
        exp.set(0);
        inventory.clear();
    }
}
