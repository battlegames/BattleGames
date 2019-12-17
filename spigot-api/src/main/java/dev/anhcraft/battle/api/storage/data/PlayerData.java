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
package dev.anhcraft.battle.api.storage.data;

import dev.anhcraft.battle.api.inventory.ItemStorage;
import dev.anhcraft.battle.api.inventory.PlayerInventory;
import dev.anhcraft.battle.api.inventory.item.ItemType;
import dev.anhcraft.battle.api.market.Transaction;
import dev.anhcraft.battle.api.stats.IntCounter;
import dev.anhcraft.battle.api.stats.LongCounter;
import dev.anhcraft.battle.api.stats.NativeStats;
import dev.anhcraft.battle.api.stats.Statistic;
import dev.anhcraft.battle.api.storage.tags.StringTag;
import dev.anhcraft.battle.impl.Resettable;
import dev.anhcraft.battle.impl.Serializable;
import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData implements Resettable, Serializable {
    private PlayerInventory inventory = new PlayerInventory();
    private Map<String, Statistic> stats = new ConcurrentHashMap<>();
    private Map<String, Long> kits = new ConcurrentHashMap<>();
    private List<String> receivedFirstJoinKits = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    private Map<String, Long> boosters = new ConcurrentHashMap<>();
    private String activeBooster;

    public PlayerData(){
        for(NativeStats ns : NativeStats.values()) {
            stats.put(ns.getId(), ns.newInstance());
        }
    }

    @NotNull
    public IntCounter getHeadshotCounter() {
        return (IntCounter) stats.get(NativeStats.HEADSHOT.getId());
    }

    @NotNull
    public IntCounter getAssistCounter() {
        return (IntCounter) stats.get(NativeStats.ASSIST.getId());
    }

    @NotNull
    public IntCounter getFirstKillCounter() {
        return (IntCounter) stats.get(NativeStats.FIRST_KILL.getId());
    }

    @NotNull
    public IntCounter getKillCounter() {
        return (IntCounter) stats.get(NativeStats.KILL.getId());
    }

    @NotNull
    public IntCounter getDeathCounter() {
        return (IntCounter) stats.get(NativeStats.DEATH.getId());
    }

    @NotNull
    public IntCounter getWinCounter() {
        return (IntCounter) stats.get(NativeStats.WIN.getId());
    }

    @NotNull
    public IntCounter getLoseCounter() {
        return (IntCounter) stats.get(NativeStats.LOSE.getId());
    }

    @NotNull
    public LongCounter getExp() {
        return (LongCounter) stats.get(NativeStats.EXP.getId());
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

    @NotNull
    public List<Transaction> getTransactions() {
        return transactions;
    }

    @NotNull
    public Map<String, Long> getBoosters() {
        return boosters;
    }

    @Nullable
    public String getActiveBooster() {
        return activeBooster;
    }

    public void setActiveBooster(@NotNull String id) {
        Condition.argNotNull("id", id);
        activeBooster = id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(DataMap<String> map) {
        for(Map.Entry<String, Statistic> x : stats.entrySet()){
            Object t = map.readTag(x.getKey());
            if(t != null) {
                x.getValue().setData(t);
            }
        }
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
        int mkts = map.readTag("mkts", Integer.class, 0);
        for (int i = 0; i < mkts; i++){
            String pre = "mkts."+i;
            UUID buyer = new UUID(
                    map.readRequiredTag(pre+".buyer.ms", Long.class),
                    map.readRequiredTag(pre+".buyer.ls", Long.class)
            );
            long date = map.readRequiredTag(pre+".date", Long.class);
            double price = map.readRequiredTag(pre+".price", Double.class);
            String currency = map.readTag(pre+".currency", String.class);
            String product = map.readRequiredTag(pre+".product", String.class);
            transactions.add(new Transaction(buyer, product, price, currency == null ? "VAULT" : currency, date));
        }
        List boosterList = map.readTag("bst", List.class);
        if(boosterList != null){
            boosterList.forEach(o -> {
                String k = ((StringTag) o).getValue();
                boosters.put(k, map.readTag("bst."+k, Long.class));
            });
        }
        String atvBooster = map.readTag("abst", String.class);
        if(atvBooster != null && boosters.containsKey(atvBooster)){
            activeBooster = atvBooster;
        }
    }

    @Override
    public void write(DataMap<String> map) {
        for(Map.Entry<String, Statistic> x : stats.entrySet()) {
            map.writeTag(x.getKey(), x.getValue().getData());
        }
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
        map.writeTag("mkts", transactions.size());
        int tsi = 0;
        for(Transaction ts : transactions){
            String pre = "mkts."+tsi;
            map.writeTag(pre+".buyer.ms", ts.getBuyer().getMostSignificantBits());
            map.writeTag(pre+".buyer.ls", ts.getBuyer().getLeastSignificantBits());
            map.writeTag(pre+".date", ts.getDate());
            map.writeTag(pre+".product", ts.getProduct());
            map.writeTag(pre+".price", ts.getPrice());
            tsi++;
        }
        List<StringTag> bst = new ArrayList<>();
        boosters.forEach((key, value) -> {
            bst.add(new StringTag(key));
            map.writeTag("bst."+key, value);
        });
        map.writeTag("bst", bst);
        if(activeBooster != null){
            map.writeTag("abst", activeBooster);
        }
    }

    @Override
    public void reset() {
        for(Statistic x : stats.values()) {
            x.reset();
        }
        inventory.clearInventory();
        kits.clear();
        receivedFirstJoinKits.clear();
        transactions.clear();
    }
}
