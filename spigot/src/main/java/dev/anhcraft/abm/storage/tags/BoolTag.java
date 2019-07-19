package dev.anhcraft.abm.storage.tags;

public class BoolTag extends DataTag<Boolean> {
    public BoolTag(Boolean value) {
        super(value);
    }

    @Override
    public int getId() {
        return 2;
    }
}
