package dev.anhcraft.abm.api.misc.info;

import org.jetbrains.annotations.NotNull;

public interface Informative {
    void inform(@NotNull InfoHolder holder);
}
