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

public class GuiLegacyConfigGen {
    public static void main(String[] args) {
        FileConfiguration fc = YamlConfiguration.loadConfiguration(new File("resources/config/gui.yml"));
        for (String s : fc.getKeys(false)) {
            System.out.println("Visiting gui " + s);
            ConfigurationSection cs = fc.getConfigurationSection(s + ".components");
            for (String k : cs.getKeys(false)) {
                System.out.println("  | Visiting component " + k);
                String q = cs.getString(k + ".item.material");
                if (q != null) {
                    PresentPair<String, Integer> p = LegacyUtils.LEGACY.get(q.toUpperCase());
                    if (p != null) {
                        cs.set(k + ".item.material", p.getFirst().toLowerCase());
                        if (p.getSecond() != 0) cs.set(k + ".item.damage", p.getSecond());
                    }
                }
            }
        }
        try {
            fc.save(new File("resources/config/gui.legacy.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
