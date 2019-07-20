package dev.anhcraft.abm.api.impl;

import dev.anhcraft.abm.utils.info.InfoHolder;
import org.jetbrains.annotations.NotNull;

public interface Informative {
    void inform(@NotNull InfoHolder holder);
}
