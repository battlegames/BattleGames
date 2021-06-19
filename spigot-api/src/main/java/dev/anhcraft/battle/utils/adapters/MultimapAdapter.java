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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.adapters.TypeAdapter;
import dev.anhcraft.config.struct.ConfigSection;
import dev.anhcraft.config.struct.SimpleForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MultimapAdapter implements TypeAdapter<Multimap> {
    public static final MultimapAdapter INSTANCE = new MultimapAdapter();

    @Override
    public @Nullable SimpleForm simplify(@NotNull ConfigSerializer serializer, @NotNull Type sourceType, @NotNull Multimap value) throws Exception {
        Type keyType, valueType;
        if(sourceType instanceof ParameterizedType) {
            keyType = ((ParameterizedType) sourceType).getActualTypeArguments()[0];
            valueType = ((ParameterizedType) sourceType).getActualTypeArguments()[1];
        } else {
            keyType = Object.class;
            valueType = Object.class;
        }
        ConfigSection section = serializer.getConfigProvider().createSection();
        for (Object key : value.keys()) {
            if (key instanceof String) {
                section.set((String) key, serializer.transform(valueType, new ArrayList<>(value.get(key))));
            } else {
                SimpleForm sp = serializer.transform(keyType, key);
                if(sp != null) {
                    section.set(Objects.requireNonNull(sp.getObject().toString()), serializer.transform(valueType, new ArrayList<>(value.get(key))));
                }
            }
        }
        return SimpleForm.of(section);
    }

    @Override
    public @Nullable Multimap complexify(@NotNull ConfigDeserializer deserializer, @NotNull Type targetType, @NotNull SimpleForm value) throws Exception {
        if (value.isSection()) {
            Type keyType, valueType;
            if(targetType instanceof ParameterizedType) {
                keyType = ((ParameterizedType) targetType).getActualTypeArguments()[0];
                valueType = ((ParameterizedType) targetType).getActualTypeArguments()[1];
            } else {
                keyType = Object.class;
                valueType = Object.class;
            }
            ConfigSection section = Objects.requireNonNull(value.asSection());
            Multimap map = HashMultimap.create();
            for (String k : section.getKeys(false)) {
                Object o = deserializer.transform(valueType, section.get(k));
                if(o instanceof Collection) {
                    map.putAll(deserializer.transform(keyType, SimpleForm.of(k)), (Collection) o);
                } else {
                    map.put(deserializer.transform(keyType, SimpleForm.of(k)), o);
                }
            }
            return map;
        }
        return null;
    }
}
