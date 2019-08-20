package dev.anhcraft.abm.api.storage;

import dev.anhcraft.abm.api.storage.data.DataMap;

public interface Serializable {
    void read(DataMap<String> map);
    void write(DataMap<String> map);
}
