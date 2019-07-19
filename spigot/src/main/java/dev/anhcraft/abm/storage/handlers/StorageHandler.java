package dev.anhcraft.abm.storage.handlers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import dev.anhcraft.abm.storage.DataMap;
import dev.anhcraft.abm.storage.tags.DataTag;

public abstract class StorageHandler {
    private final DataMap<String> data = new DataMap<>();

    protected abstract DataTag readTag(int type, ByteArrayDataInput input);
    protected abstract void writeTag(int type, DataTag tag, ByteArrayDataOutput output);
    public abstract boolean load();
    public abstract void save();

    public DataMap<String> getData() {
        return data;
    }
}
