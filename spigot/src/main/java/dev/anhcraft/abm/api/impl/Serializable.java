package dev.anhcraft.abm.api.impl;

import dev.anhcraft.abm.storage.DataMap;

public interface Serializable {
    void read(DataMap<String> map);
    void write(DataMap<String> map);
}
