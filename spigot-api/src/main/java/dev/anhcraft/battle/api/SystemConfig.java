/*
 *
 *     Battle Minigame.
 *     Copyright (c) 2019 by anhcraft.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package dev.anhcraft.battle.api;

import dev.anhcraft.config.annotations.*;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class SystemConfig {
    @Setting
    @Path("config_folder")
    @Description({
            "Here you can specify the path to the configuration folder which contains",
            "general.yml, arenas.yml, locale files, etc (except system.yml).",
            "- If you are going to host multiple Battle servers, this option should be",
            "used to share the same configuration together.",
            "- Leave blank to use the default path",
            "(*) If you are hosting files on the internet or on a private web server",
            "then it's better to try the remote config feature (see below)"
    })
    @Validation(notNull = true, silent = true)
    private String configFolder = "";

    @Setting
    @Path("remote_config.enabled")
    @Description({
            "Enable remote configuration feature or not?",
            "(*) Remote configuration: Allow you to host config files (except system.yml)",
            "on the internet, or on your private web server. For examples:",
            "- Hosting locally/privately: http://localhost/battleConfig/",
            "- Using a file hosting: https://filehosting.com/battleConfig/"
    })
    private boolean remoteConfigEnabled;

    @Setting
    @Path("remote_config.url")
    @Description({
            "The URL to the directory contain the config files",
            "Placeholder: %s (to be replaced with a file name)",
            "For example, with the default URL (http://localhost/battle/config/%s), if the plugin",
            "wants to get general.yml, it will request to http://localhost/battle/config/general.yml"
    })
    @Validation(notNull = true, silent = true)
    private String remoteConfigLink = "http://localhost/battle/config/%s";

    @Setting
    @Path("last_config_version")
    @Description({
            "Last configuration version",
            "This value is used to validate or upgrade the config",
            "DO NOT CHANGE MANUALLY!!!"
    })
    @Validation(notNull = true)
    private int lastConfigVersion;

    @Setting
    @Path("last_storage_version")
    @Description({
            "Last storage version",
            "This value is used to validate or upgrade the storage",
            "DO NOT CHANGE MANUALLY!!!"
    })
    @Validation(notNull = true)
    private int lastStorageVersion;

    @Setting
    @Path("last_plugin_version")
    @Description({
            "Last plugin version",
            "DO NOT CHANGE MANUALLY!!!"
    })
    @Validation(notNull = true)
    private String lastPluginVersion;

    @NotNull
    public String getConfigFolder() {
        return configFolder;
    }

    public boolean isRemoteConfigEnabled() {
        return remoteConfigEnabled;
    }

    @NotNull
    public String getRemoteConfigLink() {
        return remoteConfigLink;
    }

    public int getLastConfigVersion() {
        return lastConfigVersion;
    }

    public int getLastStorageVersion() {
        return lastStorageVersion;
    }

    @NotNull
    public String getLastPluginVersion() {
        return lastPluginVersion;
    }
}
