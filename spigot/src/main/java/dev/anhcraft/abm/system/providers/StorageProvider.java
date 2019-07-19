package dev.anhcraft.abm.system.providers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import dev.anhcraft.abm.storage.DataMap;
import dev.anhcraft.abm.storage.handlers.StorageHandler;
import dev.anhcraft.abm.storage.tags.DataTag;

public class StorageProvider extends StorageHandler {
    private StorageHandler handler;

    public StorageProvider(StorageHandler handler) {
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
