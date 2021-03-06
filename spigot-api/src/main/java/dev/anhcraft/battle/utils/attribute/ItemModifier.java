package dev.anhcraft.battle.utils.attribute;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Description;
import dev.anhcraft.config.annotations.Setting;
import dev.anhcraft.config.annotations.Validation;
import dev.anhcraft.config.schema.ConfigSchema;
import dev.anhcraft.config.schema.SchemaScanner;
import dev.anhcraft.jvmkit.utils.Condition;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an attribute modifier for items.
 */
@Configurable
public class ItemModifier extends Modifier implements Serializable {
    public static final ConfigSchema SCHEMA = SchemaScanner.scanConfig(ItemModifier.class);
    private static final long serialVersionUID = 3443790587999457033L;

    @Setting
    @Description("The attribute type of this modifier")
    @Validation(notNull = true)
    private Attribute attr;

    @Setting
    @Description("The slot where this modifier will take effect")
    private EquipmentSlot slot;

    /**
     * Constructs an instance of {@code ItemModifier} with the given information.
     * @param name the modifier's name
     * @param amount the modifier's amount
     * @param operation type of operation
     * @param attr type of attribute
     */
    public ItemModifier(@NotNull String name, double amount, @NotNull Operation operation, @NotNull Attribute attr){
        super(name, amount, operation);
        Condition.argNotNull("attr", attr);
        this.attr = attr;
    }

    /**
     * Constructs an instance of {@code ItemModifier} with the given information.
     * @param name the modifier's name
     * @param amount the modifier's amount
     * @param operation type of operation
     * @param attr type of attribute
     * @param slot the equipment slot which this modifier will put on (or null means 'all slots')
     */
    public ItemModifier(@NotNull String name, double amount, @NotNull Operation operation, @NotNull Attribute attr, @Nullable EquipmentSlot slot){
        super(name, amount, operation);
        Condition.argNotNull("attr", attr);
        this.attr = attr;
        this.slot = slot;
    }

    /**
     * Constructs an instance of {@code ItemModifier} with the given information.
     * @param uuid the modifier's unique id
     * @param name the modifier's name
     * @param amount the modifier's amount
     * @param operation type of operation
     * @param attr type of attribute
     * @param slot the equipment slot which this modifier will put on (or null means 'all slots')
     */
    public ItemModifier(@NotNull UUID uuid, @NotNull String name, double amount, @NotNull Operation operation, @NotNull Attribute attr, @Nullable EquipmentSlot slot){
        super(uuid, name, amount, operation);
        Condition.argNotNull("attr", attr);
        this.attr = attr;
        this.slot = slot;
    }

    /**
     * Returns the slot which this modifier will put on.
     * @return the slot
     */
    @Nullable
    public EquipmentSlot getSlot() {
        return slot;
    }

    /**
     * Returns the attribute type.
     * @return attribute
     */
    @NotNull
    public Attribute getAttribute() {
        return attr;
    }

    /**
     * Makes a new clone of this object.
     * @return {@link ItemModifier}
     */
    @Override
    @NotNull
    public ItemModifier duplicate(){
        return new ItemModifier(uuid, name, amount, operation, attr, slot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ItemModifier that = (ItemModifier) o;
        return attr == that.attr && Objects.equals(slot, that.slot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), attr, slot);
    }
}
