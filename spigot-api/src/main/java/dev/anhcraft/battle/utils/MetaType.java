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

import dev.anhcraft.jvmkit.utils.ObjectUtil;
import org.bukkit.Color;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public enum MetaType {
    POTION((i, im) -> {
        PotionMeta m = (PotionMeta) im;
        PotionData pd = m.getBasePotionData();
        i.potionType(pd.getType());
        i.potionExtended(pd.isExtended());
        i.potionUpgraded(pd.isUpgraded());
    }, (i, im) -> {
        PotionMeta m = (PotionMeta) im;
        PotionType pt = i.potionType();
        if (pt != null) {
            m.setBasePotionData(new PotionData(
                    pt,
                    pt.isExtendable() && i.potionExtended(),

                    pt.isUpgradeable() && i.potionUpgraded()
            ));
        }
    }),
    LEADER((i, im) -> {
        LeatherArmorMeta m = (LeatherArmorMeta) im;
        Color c = m.getColor();
        i.leatherColorRed(c.getRed());
        i.leatherColorGreen(c.getGreen());
        i.leatherColorBlue(c.getBlue());
    }, (i, im) -> {
        LeatherArmorMeta m = (LeatherArmorMeta) im;
        m.setColor(Color.fromRGB(
                i.leatherColorRed(),
                i.leatherColorGreen(),
                i.leatherColorBlue()
        ));
    }),
    SKULL((i, im) -> {
        SkullMeta m = (SkullMeta) im;
        i.skullOwner(m.getOwner());
    }, (i, im) -> {
        SkullMeta m = (SkullMeta) im;
        m.setOwner(i.skullOwner());
    }),
    BOOK((i, im) -> {
        BookMeta m = (BookMeta) im;
        i.bookTitle(m.getTitle());
        i.bookAuthor(m.getAuthor());
        i.bookGeneration(m.getGeneration());
        i.bookPages(m.getPages());
    }, (i, im) -> {
        BookMeta m = (BookMeta) im;
        m.setTitle(i.bookTitle());
        m.setAuthor(i.bookAuthor());
        m.setGeneration(i.bookGeneration());
        m.setPages(ObjectUtil.optional(i.bookPages(), new ArrayList<>()));
    });

    private final BiConsumer<PreparedItem, ItemMeta> onLoad;
    private final BiConsumer<PreparedItem, ItemMeta> onSave;

    MetaType(BiConsumer<PreparedItem, ItemMeta> onLoad, BiConsumer<PreparedItem, ItemMeta> onSave) {
        this.onLoad = onLoad;
        this.onSave = onSave;
    }

    public BiConsumer<PreparedItem, ItemMeta> getOnLoad() {
        return onLoad;
    }

    public BiConsumer<PreparedItem, ItemMeta> getOnSave() {
        return onSave;
    }
}
