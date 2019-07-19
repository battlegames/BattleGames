package dev.anhcraft.abm.api.impl;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public interface Informative {
    void writeInfo(Map<String, String> map, ConfigurationSection localeConf);
}
