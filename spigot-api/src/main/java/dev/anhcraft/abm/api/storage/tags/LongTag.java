package dev.anhcraft.abm.api.storage.tags;

public class LongTag extends DataTag<Long> {
    public LongTag(Long value) {
        super(value);
    }

    @Override
    public int getId() {
        return 5;
    }
}
