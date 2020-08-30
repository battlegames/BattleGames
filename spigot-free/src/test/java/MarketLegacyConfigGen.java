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

import dev.anhcraft.jvmkit.utils.PresentPair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MarketLegacyConfigGen {
    public static void main(String[] args) {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(new File("spigot-free/src/main/resources/config/market.yml"));
        for (String s : fc.getConfigurationSection("categories").getKeys(false)) {
            System.out.println("Visiting category `" + s + "`");
            String q = fc.getString("categories." + s + ".icon.material");
            if (q != null) {
                PresentPair<String, Integer> p = LegacyUtils.LEGACY.get(q.toUpperCase());
                if (p != null) {
                    System.out.println(">> Upgrading category icon...");
                    fc.set("categories." + s + ".icon.material", p.getFirst().toLowerCase());
                    if (p.getSecond() != 0) fc.set("categories." + s + ".icon.damage", p.getSecond());
                }
            }

            ConfigurationSection cs = fc.getConfigurationSection("categories." + s + ".products");
            for (String k : cs.getKeys(false)) {
                System.out.println("  | Visiting product `" + k + "`");
                String q1 = cs.getString(k + ".icon.material");
                if (q1 != null) {
                    PresentPair<String, Integer> p1 = LegacyUtils.LEGACY.get(q1.toUpperCase());
                    if (p1 != null) {
                        System.out.println("  >> Upgrading product icon...");
                        cs.set(k + ".icon.material", p1.getFirst().toLowerCase());
                        if (p1.getSecond() != 0) cs.set(k + ".icon.damage", p1.getSecond());
                    }
                }
                ConfigurationSection gi = cs.getConfigurationSection(k + ".executions.give_items.vanilla");
                if (gi != null) {
                    for (String t : gi.getKeys(false)) {
                        String q2 = gi.getString(t + ".material");
                        if (q2 != null) {
                            PresentPair<String, Integer> p2 = LegacyUtils.LEGACY.get(q2.toUpperCase());
                            if (p2 != null) {
                                System.out.println("  >> Upgrading product item giving...");
                                gi.set(t + ".material", p2.getFirst().toLowerCase());
                                if (p2.getSecond() != 0) gi.set(t + ".damage", p2.getSecond());
                            }
                        }
                    }
                }
            }
        }
        try {
            fc.save(new File("spigot-free/src/main/resources/config/market.legacy.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
