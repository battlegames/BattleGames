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

package dev.anhcraft.battle.premium.config;

import dev.anhcraft.battle.premium.system.PositionPair;
import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.annotations.*;
import dev.anhcraft.config.schema.ConfigSchema;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class ArenaSettings {
    @Setting
    @Path("empty_regions")
    @Consistent
    private List<PositionPair> emptyRegions;

    @Nullable
    public List<PositionPair> getEmptyRegions() {
        return emptyRegions;
    }

    @PostHandler
    private void handle(ConfigDeserializer deserializer, ConfigSchema schema, ConfigSection section){
        try {
            SimpleForm sf = section.get("empty_regions");
            if(sf != null && sf.isSection()) {
                for (String k : sf.asSection().getKeys(false)) {
                    ConfigSection v = Objects.requireNonNull(section.get(k)).asSection();
                    if (v == null) continue;
                    SimpleForm v1 = v.get("corner_1");
                    if (v1 == null) continue;
                    SimpleForm v2 = v.get("corner_2");
                    if (v2 == null) continue;
                    emptyRegions.add(new PositionPair(
                            Objects.requireNonNull(v1.asString()),
                            Objects.requireNonNull(v2.asString())
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
