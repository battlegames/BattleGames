package dev.anhcraft.abm.api.storage.tags;

public class StringTag extends DataTag<String> {
    public StringTag(String value) {
        super(value);
    }

    @Override
    public int getId() {
        return 4;
    }
}
