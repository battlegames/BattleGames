package dev.anhcraft.abm.storage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import dev.anhcraft.abm.api.storage.data.DataMap;
import dev.anhcraft.abm.storage.handlers.StorageProvider;
import dev.anhcraft.abm.api.storage.tags.DataTag;

public class Storage extends StorageProvider {
    private StorageProvider handler;

    public Storage(StorageProvider handler) {
        this.handler = handler;
    }

    @Override
    protected DataTag readTag(int type, ByteArrayDataInput input) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeTag(int type, DataTag tag, ByteArrayDataOutput output) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean load() {
        return handler.load();
    }

    @Override
    public void save() {
        handler.save();
    }

    @Override
    public DataMap<String> getData() {
        return handler.getData();
    }
}
