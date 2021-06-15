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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.anhcraft.battle.utils.EnumEntity;
import dev.anhcraft.jvmkit.utils.HttpUtil;
import org.bukkit.entity.EntityType;
import org.junit.Test;

import java.io.IOException;

public class EnumEntityValidator {
    @Test
    public void a() {
        try {
            //String str = HttpUtil.fetchString("https://assets.mcasset.cloud/1.12/assets/minecraft/lang/en_us.lang");
            String str = HttpUtil.fetchString("https://assets.mcasset.cloud/1.16/assets/minecraft/lang/en_us.json");
            /*Properties p = new Properties();
            p.load(new StringReader(str));
            JsonObject jo = new JsonObject();
            for(Map.Entry<Object, Object> e : p.entrySet()){
                jo.addProperty(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
            }*/
            JsonObject jo = new Gson().fromJson(str, JsonObject.class);
            for (EntityType et : EntityType.values()) {
                EnumEntity ee = EnumEntity.of(et);
                if (ee == null) {
                    // System.out.println("[!!!] EnumEntity for " + et.name() + " not found!");
                    System.out.println(et.name() + "(null, null)");
                } else {
                    // System.out.println(et.name() + ": " + ObjectUtil.optional(ee.getPath(), "null") + " / " + ObjectUtil.optional(ee.getLegacyPath(), "null"));
                    if (!jo.has(ee.getLocalePath())) {
                        System.out.println(">> Locale path for " + ee.name() + " not found");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
