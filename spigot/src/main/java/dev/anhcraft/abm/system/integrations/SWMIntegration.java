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

package dev.anhcraft.abm.system.integrations;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class SWMIntegration {
    private final SlimePlugin plugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
    private List<SlimeWorld> slimeWorlds;

    public SWMIntegration(){
        try {
            Class<?> slimeClazz = Class.forName("com.grinderwolf.swm.plugin.SWMPlugin");
            Field field = slimeClazz.getDeclaredField("worlds");
            field.setAccessible(true);
            slimeWorlds = (List<SlimeWorld>) field.get(plugin);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public Optional<SlimeWorld> getWorld(String world){
        return slimeWorlds.stream().filter(w -> w.getName().equals(world)).findFirst();
    }

    public int isReadOnly(String world){
        return getWorld(world).map(value -> value.isReadOnly() ? 1 : 0).orElse(-1);
    }

    public void reloadWorld(String world){
        getWorld(world).ifPresent(w -> {
            SlimeLoader loader = w.getLoader();
            SlimePropertyMap map = w.getPropertyMap();
            if(Bukkit.unloadWorld(w.getName(), false)){
                try {
                    plugin.loadWorld(loader, w.getName(), true, map);
                } catch (UnknownWorldException | CorruptedWorldException | NewerFormatException | IOException | WorldInUseException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
