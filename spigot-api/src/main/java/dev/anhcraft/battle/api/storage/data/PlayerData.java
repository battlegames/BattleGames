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
import dev.anhcraft.battle.api.stats.Statistic;
import dev.anhcraft.battle.api.stats.StatisticMap;
import dev.anhcraft.battle.api.storage.tags.StringTag;
import dev.anhcraft.battle.impl.Resettable;
import dev.anhcraft.battle.impl.Serializable;
import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData implements Resettable, Serializable {
    private PlayerInventory inventory = new PlayerInventory();
    private Map<String, Object> storedStats = new HashMap<>();
    private StatisticMap stats = new StatisticMap(statistic -> {
        Object v = storedStats.get(statistic.getId());
        if(v != null) {
            statistic.setData(v);
        }
    });
    private Map<String, Long> kits = new ConcurrentHashMap<>();
    private List<String> receivedFirstJoinKits = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    private Map<String, Long> boosters = new ConcurrentHashMap<>();
    private String activeBooster;

    @NotNull
    public StatisticMap getStats(){
        return stats;
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
        for(String x : map.filterKeys(s -> s.startsWith("stats."))){
            String t = x.substring("stats.".length());
            Object f = map.readTag(x);
            if (f != null) {
                storedStats.put(t, f);
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
        for(Statistic x : stats.all()) {
            map.writeTag("stats."+x.getId(), x.getData());
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
        stats.clear();
        inventory.clearInventory();
        kits.clear();
        receivedFirstJoinKits.clear();
        transactions.clear();
    }
}
