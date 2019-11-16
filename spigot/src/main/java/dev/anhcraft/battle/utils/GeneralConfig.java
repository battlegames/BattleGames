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

package dev.anhcraft.battle.utils;

import dev.anhcraft.battle.api.misc.BattleChat;
import dev.anhcraft.battle.api.misc.BattleScoreboard;
import dev.anhcraft.battle.api.misc.ConfigurableObject;
import dev.anhcraft.battle.api.misc.MouseClick;
import dev.anhcraft.battle.api.storage.StorageType;
import dev.anhcraft.confighelper.ConfigSchema;
import dev.anhcraft.confighelper.annotation.*;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Schema
public class GeneralConfig extends ConfigurableObject {
    public static final ConfigSchema<GeneralConfig> SCHEMA = ConfigSchema.of(GeneralConfig.class);

    @Key("locale")
    @Explanation({
            "The locale file used for messages and texts",
            "Available: en_us.yml, vi.yml"
    })
    @Validation(notNull = true)
    private String localeFile;

    @Key("date_format.long_form")
    @Explanation({
            "The format of date in long-form (contains day, month, year, etc)",
            "Read more: <a href='https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html'>Java Date Format</a>"
    })
    @Validation(notNull = true)
    private String dateFormatLong;

    @Key("date_format.short_form.hours")
    @Explanation({
            "The format of date in short-form. The maximum time unit is <b>hour</b>.",
            "Read more: <a href='https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html'>Java Date Format</a>"
    })
    @Validation(notNull = true)
    private String dateFormatShortHours;

    @Key("date_format.short_form.minutes")
    @Explanation({
            "The format of date in short-form. The maximum time unit is <b>minute</b>.",
            "Read more: <a href='https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html'>Java Date Format</a>"
    })
    @Validation(notNull = true)
    private String dateFormatShortMinutes;

    @Key("date_format.short_form.seconds")
    @Explanation({
            "The format of date in short-form. The maximum time unit is <b>second</b>.",
            "Read more: <a href='https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html'>Java Date Format</a>"
    })
    @Validation(notNull = true)
    private String dateFormatShortSeconds;

    @Key("level_system.exp_to_level_formula")
    @Explanation("The formula for converting X exp to Y level")
    @Validation(notNull = true)
    private String exp2LvFormula;

    @Key("level_system.level_to_exp_formula")
    @Explanation("The formula for converting X level to Y exp")
    @Validation(notNull = true)
    private String lv2ExpFormula;

    @Key("default_scoreboard")
    @Explanation("The default scoreboard settings")
    private BattleScoreboard defaultScoreboard;

    @Key("default_chat")
    @Explanation("The default chat settings")
    private BattleChat defaultChat;

    @Key("storage.type")
    @Explanation({
            "The type of storage",
            "Available: file, mysql"
    })
    @Validation(notNull = true)
    @PrettyEnum
    private StorageType storageType;

    @Key("storage.file.data_path")
    @Explanation("The path to the directory where we should store data")
    @IgnoreValue(ifNull = true)
    private String storageFilePath = ".battle";

    @Key("storage.mysql.hostname")
    @Explanation("The IP (or host name) of MySQL server")
    @IgnoreValue(ifNull = true)
    private String storageMySQLHost = "localhost";

    @Key("storage.mysql.port")
    @Explanation("The port MySQL server listens to")
    private int storageMySQLPort = 3306;

    @Key("storage.mysql.database")
    @Explanation("The MySQL database")
    private String storageMySQLDatabase = "minecraft";

    @Key("storage.mysql.username")
    @Explanation("The MySQL user's name")
    private String storageMySQLUser = "root";

    @Key("storage.mysql.password")
    @Explanation("The MySQL user's password")
    private String storageMySQLPass = "";

    @Key("storage.mysql.datasource_properties")
    @Explanation("The external configuration (for HikariCP's data source)")
    private ConfigurationSection storageMySQLProperties;

    @Key("misc.anti_death_drops")
    @Explanation("This option prevents player items and exp from dropping")
    private boolean antiDeathDrops = true;

    @Key("misc.default_speed.walk")
    @Explanation({
            "The walking speed",
            "-1 <= speed <= 1"
    })
    private double walkSpeed = 0.2;

    @Key("misc.default_speed.fly")
    @Explanation({
            "The flying speed",
            "-1 <= speed <= 1"
    })
    private double flySpeed = 0.2;

    @Key("bungeecord.enabled")
    @Explanation("Should we enable the Bungeecord support?")
    private boolean bungeeEnabled;

    @Key("bungeecord.lobby_servers")
    @Explanation({
            "The lobby servers.",
            "If the player can't connect to the first one, the second will be chosen, and so on",
            "until there is no server left"
    })
    @IgnoreValue(ifNull = true)
    private List<String> bungeeLobbies = new ArrayList<>();

    @Key("bungeecord.reconnect_tries_per_server")
    @Explanation({
            "Here you can specify how many times does the player can try to reconnect. If exceed,",
            "the next server in the given list will be chosen."
    })
    private int bungeeReconnectTries;

    @Key("bungeecord.connection_delay")
    @Explanation("The delayed time between two connections per player")
    private long bungeeConnectDelay = 100;

    @Key("control.gun_shoot")
    @Explanation("The type of mouse click for shooting gun")
    @IgnoreValue(ifNull = true)
    @PrettyEnum
    private MouseClick gunShootClick = MouseClick.LEFT_CLICK;

    @Key("control.gun_zoom")
    @Explanation("The type of mouse click for zooming in (with scope attached to gun)")
    @IgnoreValue(ifNull = true)
    @PrettyEnum
    private MouseClick gunZoomClick = MouseClick.RIGHT_CLICK;

    @Key("control.grenade_throw")
    @Explanation("The type of mouse click for throwing grenade")
    @IgnoreValue(ifNull = true)
    @PrettyEnum
    private MouseClick grenadeThrowClick = MouseClick.LEFT_CLICK;

    @Key("in_game_economy.currency.name")
    @Explanation("The name of the currency that used in-game")
    @IgnoreValue(ifNull = true)
    private String igEcoCurrencyName = "&aCoins";

    @Key("in_game_economy.currency.format")
    @Explanation("The price format (contains the symbol and the cost)")
    @IgnoreValue(ifNull = true)
    private String igEcoCurrencyFormat = "%.2f coins";

    @Key("in_game_economy.init_balance")
    @Explanation("The initial balance (given on starting a game)")
    private double igEcoInitBalance;

    @NotNull
    public String getLocaleFile() {
        return localeFile;
    }

    @NotNull
    public String getDateFormatLong() {
        return dateFormatLong;
    }

    @NotNull
    public String getDateFormatShortHours() {
        return dateFormatShortHours;
    }

    @NotNull
    public String getDateFormatShortMinutes() {
        return dateFormatShortMinutes;
    }

    @NotNull
    public String getDateFormatShortSeconds() {
        return dateFormatShortSeconds;
    }

    @NotNull
    public String getExp2LvFormula() {
        return exp2LvFormula;
    }

    @NotNull
    public String getLv2ExpFormula() {
        return lv2ExpFormula;
    }

    @Nullable
    public BattleScoreboard getDefaultScoreboard() {
        return defaultScoreboard;
    }

    @Nullable
    public BattleChat getDefaultChat() {
        return defaultChat;
    }

    @NotNull
    public StorageType getStorageType() {
        return storageType;
    }

    @NotNull
    public String getStorageFilePath() {
        return storageFilePath;
    }

    @NotNull
    public String getStorageMySQLHost() {
        return storageMySQLHost;
    }

    public int getStorageMySQLPort() {
        return storageMySQLPort;
    }

    @NotNull
    public String getStorageMySQLDatabase() {
        return storageMySQLDatabase;
    }

    @NotNull
    public String getStorageMySQLUser() {
        return storageMySQLUser;
    }

    @NotNull
    public String getStorageMySQLPass() {
        return storageMySQLPass;
    }

    @Nullable
    public ConfigurationSection getStorageMySQLProperties() {
        return storageMySQLProperties;
    }

    public boolean shouldAntiDeathDrops() {
        return antiDeathDrops;
    }

    public double getWalkSpeed() {
        return walkSpeed;
    }

    public double getFlySpeed() {
        return flySpeed;
    }

    public boolean isBungeeEnabled() {
        return bungeeEnabled;
    }

    @NotNull
    public List<String> getBungeeLobbies() {
        return bungeeLobbies;
    }

    public int getBungeeReconnectTries() {
        return bungeeReconnectTries;
    }

    public long getBungeeConnectDelay() {
        return bungeeConnectDelay;
    }

    @NotNull
    public MouseClick getGrenadeThrowClick() {
        return grenadeThrowClick;
    }

    @NotNull
    public MouseClick getGunShootClick() {
        return gunShootClick;
    }

    @NotNull
    public MouseClick getGunZoomClick() {
        return gunZoomClick;
    }

    @NotNull
    public String getIgEcoCurrencyName() {
        return igEcoCurrencyName;
    }

    @NotNull
    public String getIgEcoCurrencyFormat() {
        return igEcoCurrencyFormat;
    }

    public double getIgEcoInitBalance() {
        return igEcoInitBalance;
    }
}
