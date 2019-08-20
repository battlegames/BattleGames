package dev.anhcraft.abm.api.storage.tags;

public class IntTag extends DataTag<Integer> {
    public IntTag(Integer value) {
        super(value);
    }

    @Override
    public int getId() {
        return 1;
    }
}
