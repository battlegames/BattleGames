package dev.anhcraft.abm.storage.tags;

public class DoubleTag extends DataTag<Double> {
    public DoubleTag(Double value) {
        super(value);
    }

    @Override
    public int getId() {
        return 3;
    }
}
