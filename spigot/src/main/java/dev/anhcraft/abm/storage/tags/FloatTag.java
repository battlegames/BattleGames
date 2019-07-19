package dev.anhcraft.abm.storage.tags;

public class FloatTag extends DataTag<Float> {
    public FloatTag(Float value) {
        super(value);
    }

    @Override
    public int getId() {
        return 6;
    }
}
