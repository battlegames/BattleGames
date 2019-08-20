package dev.anhcraft.abm.api.storage.tags;

public abstract class DataTag<T> {
    private T value;

    public DataTag(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public abstract int getId();
}
