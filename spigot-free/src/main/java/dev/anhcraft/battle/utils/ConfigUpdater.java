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

package dev.anhcraft.battle.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ConfigUpdater {
    public static class PathRelocating{
        private String[] oldPath;
        private String[] newPath;
        private Class<?> type;

        public PathRelocating oldPath(String oldPath) {
            this.oldPath = oldPath.split("\\.");
            return this;
        }

        public PathRelocating newPath(String newPath) {
            this.newPath = newPath.split("\\.");
            return this;
        }

        public PathRelocating type(Class<?> type) {
            this.type = type;
            return this;
        }
    }

    private final List<PathRelocating> pathRelocating = new ArrayList<>();
    private final Logger logger;

    public ConfigUpdater(Logger logger) {
        this.logger = logger;
    }

    @NotNull
    public List<PathRelocating> getPathRelocating() {
        return pathRelocating;
    }

    public void update(ConfigurationSection conf){
        List<String> holders = new ArrayList<>();
        for(String k : conf.getKeys(true)){
            String[] f = k.split("\\.");
            Object val = conf.get(k);

            pr:
            for (PathRelocating p : pathRelocating){
                if(f.length != p.oldPath.length || val == null || !p.type.isAssignableFrom(val.getClass())) continue;
                holders.clear();

                for(int i = 0; i < f.length; i++){
                    if(p.oldPath[i].equals("*")){
                        holders.add(f[i]);
                    } else if(!p.oldPath[i].equals(f[i])){
                        continue pr;
                    }
                }

                String path = Arrays.stream(p.newPath).map(s -> {
                    if(s.startsWith("#")){
                        return holders.get(Integer.parseInt(s.substring(1)));
                    }
                    return s;
                }).collect(Collectors.joining("."));
                conf.set(k, null); // must remove the old before setting the new
                conf.set(path, val);
                logger.info("[ConfigUpdater] Relocated entry at `"+k+"` to `"+path+"`");
                break;
            }
        }
    }
}
