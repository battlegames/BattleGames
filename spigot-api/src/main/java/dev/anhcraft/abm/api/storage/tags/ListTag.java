package dev.anhcraft.abm.api.storage.tags;

import java.util.List;

public class ListTag<T extends DataTag> extends DataTag<List<T>> {
    public ListTag(List<T> value) {
        super(value);
    }

    @Override
    public int getId() {
        return 7;
    }
}
