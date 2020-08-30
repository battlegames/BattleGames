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

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

public class YamlSort {
    @Test
    public void a() {
        FileConfiguration c = YamlConfiguration.loadConfiguration(new File("src/main/resources/config/general.yml"));
        Multimap<Integer, String> map = MultimapBuilder.treeKeys().treeSetValues().build();
        for (String s : c.getConfigurationSection("misc.block_hardness").getKeys(false)) {
            map.putAll(c.getInt("misc.block_hardness." + s + ".value"), c.getStringList("misc.block_hardness." + s + ".material"));
        }
        YamlConfiguration configuration = new YamlConfiguration();
        int i = 0;
        for (Integer s : map.keySet()) {
            String k = Integer.toString(i);
            configuration.set(k + ".material", new ArrayList<>(map.get(s)));
            configuration.set(k + ".value", s);
            i++;
        }
        System.out.println(configuration.saveToString());
    }
}
