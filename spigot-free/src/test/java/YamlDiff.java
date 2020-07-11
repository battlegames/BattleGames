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

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class YamlDiff {
    @Test
    public void a(){
        FileConfiguration c1 = YamlConfiguration.loadConfiguration(new File("src/main/resources/config/locale/en_us.yml"));
        FileConfiguration c2 = YamlConfiguration.loadConfiguration(new File("src/main/resources/config/locale/vi_vn.yml"));
        for(String s : c1.getKeys(true)){
            Object a = c1.get(s);
            Object b = c2.get(s);
            if(a != null && b == null){
                System.out.println("Missing: "+s);
                c2.set(s, a);
            } else if(a != null && !a.getClass().equals(b.getClass())){
                System.out.println("Wrong data type: "+s);
                c2.set(s, a);
            }
        }
        for(String s : c2.getKeys(true)){
            Object a = c1.get(s);
            Object b = c2.get(s);
            if(a == null && b != null){
                System.out.println("Redundant entry: "+s);
                c2.set(s, null);
            }
        }
        try {
            File out = new File("src/main/resources/config/locale/vi_vn.temp.yml");
            out.createNewFile();
            c2.save(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
