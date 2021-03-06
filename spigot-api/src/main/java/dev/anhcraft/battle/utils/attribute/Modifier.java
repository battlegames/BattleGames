package dev.anhcraft.battle.utils.attribute;

import dev.anhcraft.config.annotations.Configurable;
import dev.anhcraft.config.annotations.Description;
import dev.anhcraft.config.annotations.Setting;
import dev.anhcraft.config.annotations.Validation;
import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an attribute modifier.
 */
@Configurable
public class Modifier implements Serializable {
    private static final long serialVersionUID = -4638305656316584438L;

    /**
     * Types of operation.
     */
    public enum Operation{
        ADD_NUMBER(0),
        ADD_SCALAR(0),
        MULTIPLY_SCALAR_1(2);

        private final int id;

        Operation(int id) {
            this.id = id;
        }

        public int getId(){
            return this.id;
        }

        /**
         * Gets an operation type by its id.
         * @param id the id
         * @return the operation
         */
        public static Operation getById(int id){
            for(Operation o : values()){
                if(o.getId() == id) return o;
            }
            return null;
        }
    }

    @Setting
    @Description("The name of this modifier")
    @Validation(notNull = true)
    protected String name;

    @Setting
    protected double amount;

    @Setting
    @Validation(notNull = true)
    @Description("The operation this modifier will apply")
    protected Operation operation;

    protected UUID uuid = UUID.randomUUID();

    /**
     * Constructs an instance of {@code Modifier} with the given information.
     * @param name the modifier's name
     * @param amount the modifier's amount
     * @param operation type of operation
     */
    public Modifier(@NotNull String name, double amount, @NotNull Operation operation){
        Condition.argNotNull("name", name);
        Condition.argNotNull("operation", operation);
        this.name = name;
        this.amount = amount;
        this.operation = operation;
    }

    /**
     * Constructs an instance of {@code Modifier} with the given information.
     * @param uuid the modifier's unique id
     * @param name the modifier's name
     * @param amount the modifier's amount
     * @param operation type of operation
     */
    public Modifier(@NotNull UUID uuid, @NotNull String name, double amount, @NotNull Operation operation){
        Condition.argNotNull("uuid", uuid);
        Condition.argNotNull("name", name);
        Condition.argNotNull("operation", operation);
        this.name = name;
        this.amount = amount;
        this.operation = operation;
        this.uuid = uuid;
    }

    /**
     * Returns the modifier's unique id.
     * @return the id
     */
    @NotNull
    public UUID getUniqueId() {
        return this.uuid;
    }

    /**
     * Returns the modifier's name.
     * @return the name
     */
    @NotNull
    public String getName(){
        return this.name;
    }

    /**
     * Returns the modifier's amount.
     * @return the amount
     */
    public double getAmount(){
        return this.amount;
    }

    /**
     * Returns the modifier's operation.
     * @return type of operation
     */
    @NotNull
    public Operation getOperation() {
        return this.operation;
    }

    /**
     * Makes a new clone of this object.
     * @return {@link Modifier}
     */
    @NotNull
    public Modifier duplicate(){
        return new Modifier(uuid, name, amount, operation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Modifier that = (Modifier) o;
        return Double.compare(that.amount, amount) == 0 &&
                name.equals(that.name) &&
                operation == that.operation &&
                uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, amount, operation, uuid);
    }
}
