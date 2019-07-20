package dev.anhcraft.abm.utils.info;

public abstract class InfoData<T> {
    private T value;

    protected InfoData(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
