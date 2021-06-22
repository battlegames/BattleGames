package dev.anhcraft.battle.utils;

import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.anhcraft.battle.utils.attribute.Attribute;
import dev.anhcraft.battle.utils.attribute.ItemModifier;
import dev.anhcraft.battle.utils.attribute.Modifier;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Advanced item manipulation by working with NBT tags.
 *
 * <b>You must call {@link #save()} on finished to get the cloned item that was applied all changes.</b>
 */
public class ItemNBTHelper extends Selector<ItemStack> {
    private NBTItem tag;

    @NotNull
    private static String getNmsEquipName(EquipmentSlot slot){
        switch (slot){
            case HAND: return "mainhand";
            case OFF_HAND: return "offhand";
            case HEAD: return "head";
            case CHEST: return "chest";
            case LEGS: return "legs";
            case FEET: return "feet";
            default: return "";
        }
    }

    private static EquipmentSlot getBukkitEquipName(String str){
        switch (str){
            case "mainhand": return EquipmentSlot.HAND;
            case "offhand": return EquipmentSlot.OFF_HAND;
            case "head": return EquipmentSlot.HEAD;
            case "chest": return EquipmentSlot.CHEST;
            case "legs": return EquipmentSlot.LEGS;
            case "feet": return EquipmentSlot.FEET;
        }
        return null;
    }

    /**
     * Constructs an {@code ItemNBTHelper} object which selects the given item stack.
     * @param itemStack the item stack
     * @return ItemNBTHelper
     */
    @NotNull
    public static ItemNBTHelper of(@NotNull ItemStack itemStack){
        ItemNBTHelper i = new ItemNBTHelper();
        i.select(itemStack);
        return i;
    }

    @Override
    protected boolean onSelected(@NotNull ItemStack target) {
        tag = new NBTItem(target);
        return true;
    }

    /**
     * Applies all changes to the stack and returns it.
     * @return the target
     */
    @NotNull
    public ItemStack save(){
        tag.applyNBT(getTarget());
        return getTarget();
    }

    /**
     * Makes the stack unbreakable or breakable.
     * @param unbreakable {@code true} to make it unbreakable, otherwise is {@code false}
     * @return this object
     */
    @Contract("_ -> this")
    public ItemNBTHelper setUnbreakable(boolean unbreakable){
        if(unbreakable) tag.setBoolean("Unbreakable", true);
        else tag.removeKey("Unbreakable");
        return this;
    }

    /**
     * Checks if the stack is unbreakable.
     * @return {@code true} if it is or {@code false} otherwise
     */
    public boolean isUnbreakable(){
        Boolean t = tag.getBoolean("Unbreakable");
        return t != null && t;
    }

    private NBTContainer toCompound(ItemModifier modifier){
        NBTContainer c = new NBTContainer();
        c.setString("AttributeName", modifier.getAttribute().getId());
        c.setString("Name", modifier.getName());
        c.setDouble("Amount", modifier.getAmount());
        c.setInteger("Operation", modifier.getOperation().getId());
        c.setLong("UUIDLeast", modifier.getUniqueId().getLeastSignificantBits());
        c.setLong("UUIDMost", modifier.getUniqueId().getMostSignificantBits());
        if(modifier.getSlot() != null) {
            c.setString("Slot", getNmsEquipName(modifier.getSlot()));
        }
        return c;
    }

    /**
     * Adds an attribute modifier.
     * @param modifier the modifier
     * @return this object
     */
    @Contract("_ -> this")
    public ItemNBTHelper addModifier(@NotNull ItemModifier modifier){
        Condition.argNotNull("modifier", modifier);

        NBTCompoundList ltag = tag.getCompoundList("AttributeModifiers");
        ltag.addCompound(toCompound(modifier));
        return this;
    }

    /**
     * Sets this item's modifiers.
     * @param modifiers the modifiers
     * @return this object
     */
    @Contract("_ -> this")
    public ItemNBTHelper setModifiers(@NotNull Collection<ItemModifier> modifiers){
        Condition.argNotNull("modifiers", modifiers);

        NBTCompoundList ltag = tag.getCompoundList("AttributeModifiers");
        modifiers.stream().map(this::toCompound).forEach(ltag::addCompound);
        return this;
    }

    /**
     * Gets all attribute modifiers on the stack.
     * @return a list of modifiers
     */
    @NotNull
    public List<ItemModifier> getModifiers(){
        NBTCompoundList ltag = tag.getCompoundList("AttributeModifiers");
        if(ltag != null) {
            return ltag.stream().map(tag -> {
                String attr = Objects.requireNonNull(tag.getString("AttributeName"));
                Long um = Objects.requireNonNull(tag.getLong("UUIDMost"));
                Long ul = Objects.requireNonNull(tag.getLong("UUIDLeast"));
                String name = Objects.requireNonNull(tag.getString("Name"));
                Double amt = Objects.requireNonNull(tag.getDouble("Amount"));
                int op = Objects.requireNonNull(tag.getInteger("Operation"));
                String slotTag = tag.getString("Slot");
                return new ItemModifier(
                        new UUID(um, ul), name, amt,
                        Objects.requireNonNull(Modifier.Operation.getById(op)),
                        Objects.requireNonNull(Attribute.getById(attr)),
                        slotTag == null ? null : getBukkitEquipName(slotTag)
                );
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
