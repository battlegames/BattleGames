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

package dev.anhcraft.battle.utils.adapters;

import dev.anhcraft.battle.utils.MaterialUtil;
import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.struct.SimpleForm;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class MaterialAdapter implements TypeAdapter<Material> {
    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer configSerializer, @NotNull Type type, @NotNull Material material) throws Exception {
        return configSerializer.transform(Enum.class, material);
    }

    @Override
    public @Nullable Material complexify(@NotNull ConfigDeserializer configDeserializer, @NotNull Type type, @NotNull SimpleForm simpleForm) throws Exception {
        if(simpleForm.isString()) {
            return MaterialUtil.parse(simpleForm.asString()).orElse(null);
        }
        return null;
    }
}
