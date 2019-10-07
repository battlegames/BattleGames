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

import dev.anhcraft.abm.api.inventory.ItemStorage;
import dev.anhcraft.abm.api.inventory.PlayerInventory;
import dev.anhcraft.abm.api.inventory.items.ItemType;
import dev.anhcraft.abm.api.misc.Resettable;
import dev.anhcraft.abm.api.storage.Serializable;
import dev.anhcraft.abm.api.storage.tags.StringTag;
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
    private List<String> receivedFirstJoinKits = new ArrayList<>();

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

    @NotNull
    public List<String> getReceivedFirstJoinKits() {
        return receivedFirstJoinKits;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(DataMap<String> map) {
        headshotCounter.set(map.readTag("hs", Integer.class, 0));
        assistCounter.set(map.readTag("ast", Integer.class, 0));
        killCounter.set(map.readTag("kill", Integer.class, 0));
        deathCounter.set(map.readTag("death", Integer.class, 0));
        winCounter.set(map.readTag("win", Integer.class, 0));
        loseCounter.set(map.readTag("lose", Integer.class, 0));
        exp.set(map.readTag("exp", Long.class, 0L));
        List inv = map.readTag("inv", List.class);
        if(inv != null) {
            inv.forEach(o -> {
                String q = ((StringTag) o).getValue();
                ItemStorage storage = inventory.getStorage(ItemType.valueOf(q));
                List is = map.readTag("inv." + q, List.class);
                if(is != null) {
                    is.forEach(o1 -> {
                        String v = ((StringTag) o1).getValue();
                        long t = map.readTag("inv." + q + "." + v, Long.class, 0L);
                        storage.put(v, t);
                    });
                }
            });
        }
        List kl = map.readTag("kits", List.class);
        if(kl != null){
            kl.forEach(o -> {
                String k = ((StringTag) o).getValue();
                kits.put(k, map.readTag("kit."+k, Long.class));
            });
        }
        List fjkl = map.readTag("first_join_kits", List.class);
        if(fjkl != null) {
            fjkl.forEach(o -> {
                String k = ((StringTag) o).getValue();
                receivedFirstJoinKits.add(k);
            });
        }
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
        inventory.listStorage((itemType, itemStorage) -> {
            String s = itemType.name();
            inv.add(new StringTag(s));
            List<StringTag> items = new ArrayList<>();
            itemStorage.list((i, v) -> {
                items.add(new StringTag(i));
                map.writeTag("inv."+s+"."+i, v);
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
        List<StringTag> rfjk = new ArrayList<>();
        receivedFirstJoinKits.forEach(v -> rfjk.add(new StringTag(v)));
        map.writeTag("first_join_kits", rfjk);
    }

    @Override
    public void reset() {
        headshotCounter.set(0);
        killCounter.set(0);
        deathCounter.set(0);
        winCounter.set(0);
        loseCounter.set(0);
        exp.set(0);
        inventory.clearInventory();
        receivedFirstJoinKits.clear();
    }
}
