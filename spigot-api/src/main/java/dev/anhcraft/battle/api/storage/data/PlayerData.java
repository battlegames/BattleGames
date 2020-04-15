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

import dev.anhcraft.battle.api.advancement.PlayerProgression;
import dev.anhcraft.battle.api.inventory.Backpack;
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
    private final Backpack backpack = new Backpack();
    private final Map<String, Object> storedStats = new HashMap<>();
    private final StatisticMap stats = new StatisticMap(statistic -> {
        Object v = storedStats.get(statistic.getId());
        if(v != null) {
            statistic.setData(v);
        }
    });
    private final Map<String, Long> kits = new ConcurrentHashMap<>();
    private final List<String> receivedFirstJoinKits = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();
    private final Map<String, Long> boosters = new ConcurrentHashMap<>();
    private final Map<String, PlayerProgression> advancements = new HashMap<>();
    private String activeBooster;

    public PlayerData(){
        stats.setAdvancementSupport(true);
    }

    @NotNull
    public StatisticMap getStats(){
        return stats;
    }

    @NotNull
    public Backpack getBackpack() {
        return backpack;
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

    @Nullable
    public PlayerProgression getProgression(String type) {
        return advancements.get(type);
    }

    @NotNull
    public PlayerProgression getProgressionOrCreate(String type) {
        return advancements.compute(type, (s, p) -> p == null ? new PlayerProgression() : p);
    }

    public void clearProgression() {
        advancements.clear();
        stats.clear();
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
        List adv = map.readTag("adv", List.class);
        if(adv != null) {
            adv.forEach(o -> {
                String type = ((StringTag) o).getValue();
                List finished = map.readTag("adv.fns."+type, List.class);
                String active = map.readTag("adv.act."+type, String.class);
                double amount = map.readTag("adv.amt."+type, Double.class, 0d);
                double tgtAmount = map.readTag("adv.tamt."+type, Double.class, 0d);
                int cpg = map.readTag("adv.cpg."+type, Integer.class, 0);
                PlayerProgression pp = new PlayerProgression();
                pp.setCurrentLevel(cpg);
                pp.setCurrentAmount(amount);
                pp.setActiveAdvancement(active);
                pp.setTargetAmount(tgtAmount);
                if(finished != null) {
                    for (Object obj : finished) {
                        String v = ((StringTag) obj).getValue();
                        pp.getFinishedAdvancements().add(v);
                    }
                }
                advancements.put(type, pp);
            });
        }
        List inv = map.readTag("inv", List.class);
        if(inv != null) {
            inv.forEach(o -> {
                String q = ((StringTag) o).getValue();
                Backpack.Compartment storage = backpack.getStorage(ItemType.valueOf(q));
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
        backpack.listStorage((itemType, compartment) -> {
            String s = itemType.name();
            inv.add(new StringTag(s));
            List<StringTag> items = new ArrayList<>();
            compartment.list((i, v) -> {
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
        List<StringTag> adv = new ArrayList<>();
        advancements.forEach((key, value) -> {
            adv.add(new StringTag(key));
            if(!value.getFinishedAdvancements().isEmpty()) {
                List<StringTag> finished = new ArrayList<>();
                for (String s : value.getFinishedAdvancements()) {
                    finished.add(new StringTag(s));
                }
                map.writeTag("adv.fns." + key, finished);
            }
            if(value.getActiveAdvancement() != null) {
                map.writeTag("adv.act." + key, value.getActiveAdvancement());
            } else {
                map.removeTag("adv.act." + key);
            }
            map.writeTag("adv.amt." + key, value.getCurrentAmount());
            map.writeTag("adv.cpg." + key, value.getCurrentLevel());
            map.writeTag("adv.tamt." + key, value.getTargetAmount());
        });
        map.writeTag("adv", adv);
    }

    @Override
    public void reset() {
        stats.clear();
        backpack.clear();
        kits.clear();
        receivedFirstJoinKits.clear();
        transactions.clear();
    }
}
