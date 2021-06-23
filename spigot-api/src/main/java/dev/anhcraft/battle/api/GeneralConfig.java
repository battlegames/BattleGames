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

import dev.anhcraft.battle.api.chat.BattleChat;
import dev.anhcraft.battle.api.storage.StorageType;
import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.annotations.*;
import dev.anhcraft.config.schema.ConfigSchema;
import dev.anhcraft.config.struct.ConfigSection;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

@SuppressWarnings("FieldMayBeFinal")
@Configurable
public class GeneralConfig {
    @Setting
    @Path("locale")
    @Description({
            "The locale file used for messages and texts",
            "Available: en_us.yml, vi.yml"
    })
    @Validation(notNull = true)
    private String localeFile;

    @Setting
    @Path("date_format.long_form")
    @Description({
            "The format of date in long-form (contains day, month, year, etc)",
            "Read more: <a href='https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html'>Java Date Format</a>"
    })
    @Validation(notNull = true)
    private String dateFormatLong;

    @Setting
    @Path("date_format.short_form.hours")
    @Description({
            "The format of date in short-form. The maximum time unit is <b>hour</b>.",
            "Read more: <a href='https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html'>Java Date Format</a>"
    })
    @Validation(notNull = true)
    private String dateFormatShortHours;

    @Setting
    @Path("date_format.short_form.minutes")
    @Description({
            "The format of date in short-form. The maximum time unit is <b>minute</b>.",
            "Read more: <a href='https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html'>Java Date Format</a>"
    })
    @Validation(notNull = true)
    private String dateFormatShortMinutes;

    @Setting
    @Path("date_format.short_form.seconds")
    @Description({
            "The format of date in short-form. The maximum time unit is <b>second</b>.",
            "Read more: <a href='https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html'>Java Date Format</a>"
    })
    @Validation(notNull = true)
    private String dateFormatShortSeconds;

    @Setting
    @Path("level_system.exp_to_level_formula")
    @Description("The formula for converting X exp to Y level")
    @Validation(notNull = true)
    private String exp2LvFormula;

    @Setting
    @Path("level_system.level_to_exp_formula")
    @Description("The formula for converting X level to Y exp")
    @Validation(notNull = true)
    private String lv2ExpFormula;

    @Setting
    @Path("default_scoreboard")
    @Description("The default scoreboard settings")
    private BattleScoreboard defaultScoreboard;

    @Setting
    @Path("default_chat")
    @Description("The default chat settings")
    private BattleChat defaultChat;

    @Setting
    @Path("storage.type")
    @Description({
            "The type of storage",
            "Available: file, mysql"
    })
    @Validation(notNull = true)
    private StorageType storageType;

    @Setting
    @Path("storage.file.data_path")
    @Description("The path to the directory where we should store data")
    @Validation(notNull = true, silent = true)
    private String storageFilePath = "battle";

    @Setting
    @Path("storage.mysql.hostname")
    @Description("The IP (or host name) of MySQL server")
    @Validation(notNull = true, silent = true)
    private String storageMySQLHost = "localhost";

    @Setting
    @Path("storage.mysql.port")
    @Description("The port MySQL server listens to")
    private int storageMySQLPort = 3306;

    @Setting
    @Path("storage.mysql.database")
    @Description("The MySQL database")
    private String storageMySQLDatabase = "minecraft";

    @Setting
    @Path("storage.mysql.username")
    @Description("The MySQL user's name")
    private String storageMySQLUser = "root";

    @Setting
    @Path("storage.mysql.password")
    @Description("The MySQL user's password")
    private String storageMySQLPass = "";

    @Setting
    @Path("storage.mysql.datasource_properties")
    @Description("The external configuration (for HikariCP's data source)")
    private ConfigurationSection storageMySQLProperties;

    @Setting
    @Path("misc.anti_death_drops")
    @Description("This option prevents player items and exp from dropping")
    private boolean antiDeathDrops = true;

    @Setting
    @Path("misc.resource_pack.enabled")
    @Description("Enabled force resource pack on join")
    private boolean resourcePackEnabled;

    @Setting
    @Path("misc.resource_pack.custom_url")
    @Description("Custom resource pack URL")
    private String resourcePackCustomUrl;

    @Setting
    @Path("misc.heal_on_game_start")
    @Description("Enable this option to heal all players when game started")
    private boolean healOnGameStart = true;

    @Setting
    @Path("misc.heal_on_game_end")
    @Description("Enable this option to heal all players after end game")
    private boolean healOnGameEnd = true;

    @Setting
    @Path("misc.default_speed.walk")
    @Description({
            "The walking speed",
            "-1 <= speed <= 1"
    })
    private double walkSpeed = 0.2;

    @Setting
    @Path("misc.default_speed.fly")
    @Description({
            "The flying speed",
            "-1 <= speed <= 1"
    })
    private double flySpeed = 0.2;

    @Setting
    @Path("misc.region_partition_size")
    @Description("The maximum size for each region partition")
    private int regionPartitionSize = 125000;

    @Setting
    @Path("misc.entity_track_min_distance")
    @Description("The minimum distance to ensure a tracked entity has moved")
    private double entityTrackMinDistance = 1.5;

    @Setting
    @Path("misc.blood_effect.enabled")
    @Description("Should we show the blood effects")
    private boolean bloodEffectEnabled = true;

    @Setting
    @Path("misc.blood_effect.particles_ratio")
    @Description("The amount of blood particles will be spawned per each health point")
    private double bloodEffectParticleRatio = 1;

    @Setting
    @Path("misc.material_hardness")
    @Description("The material of blocks")
    @Consistent
    private Map<Material, Integer> materialHardness;

    @Setting
    @Path("misc.entity_hardness")
    @Description("The base hardness of entities (without equipment)")
    @Consistent
    private Map<EntityType, Integer> entityHardness;

    @Setting
    @Path("bungeecord.enabled")
    @Description("Should we enable the Bungeecord support?")
    private boolean bungeeEnabled;

    @Setting
    @Path("bungeecord.lobby_servers")
    @Description({
            "The lobby servers.",
            "If the player can't connect to the first one, the second will be chosen, and so on",
            "until there is no server left"
    })
    @Validation(notNull = true, silent = true)
    private List<String> bungeeLobbies = new ArrayList<>();

    @Setting
    @Path("bungeecord.reconnect_tries_per_server")
    @Description({
            "Here you can specify how many times does the player can try to reconnect. If exceed,",
            "the next server in the given list will be chosen."
    })
    private int bungeeReconnectTries;

    @Setting
    @Path("bungeecord.connection_delay")
    @Description("The delayed time between two connections per player")
    private long bungeeConnectDelay = 100;

    @Setting
    @Path("control.gun_shoot")
    @Description("The type of mouse click for shooting gun")
    @Validation(notNull = true, silent = true)
    private MouseClick gunShootClick = MouseClick.LEFT_CLICK;

    @Setting
    @Path("control.gun_zoom")
    @Description("The type of mouse click for zooming in (with scope attached to gun)")
    @Validation(notNull = true, silent = true)
    private MouseClick gunZoomClick = MouseClick.RIGHT_CLICK;

    @Setting
    @Path("control.grenade_throw")
    @Description("The type of mouse click for throwing grenade")
    @Validation(notNull = true, silent = true)
    private MouseClick grenadeThrowClick = MouseClick.LEFT_CLICK;

    @Setting
    @Path("control.auto_reload_gun")
    @Description("Automatically reload guns ammunition when needed")
    private boolean autoReloadGun = false;

    @Setting
    @Path("in_game_economy.currency.name")
    @Description("The name of the currency that used in-game")
    @Validation(notNull = true, silent = true)
    private String igEcoCurrencyName = "&aCoins";

    @Setting
    @Path("in_game_economy.currency.format")
    @Description("The price format (contains the symbol and the cost)")
    @Validation(notNull = true, silent = true)
    private String igEcoCurrencyFormat = "%.2f coins";

    @Setting
    @Path("in_game_economy.init_balance")
    @Description("The initial balance (given on starting a game)")
    private double igEcoInitBalance;

    @Setting
    @Path("join_sign.dynamic_lines")
    @Description({
            "The text on join signs.",
            "It will be updated frequently (based on the option `update_time`)",
            "You can use placeholders for arena and game.",
    })
    private List<String> joinSignDynamicLines;

    @Setting
    @Path("join_sign.update_time")
    @Description({
            "Time for each update to the text on join signs (in ticks)",
            "Minimum are 20 ticks; set to lower will disable the task",
            "thus make the text always fixed."
    })
    private long joinSignUpdateTime;

    @Setting
    @Path("backpack.sounds.on_add_success")
    @Description("Sound to be played when an item is put into the backpack successfully")
    private BattleSound backpackSoundAddItemSuccess;

    @Setting
    @Path("backpack.sounds.on_add_failure")
    @Description("Sound to be played when a <b>wrong</b> item is put into the backpack")
    private BattleSound backpackSoundAddItemFailure;

    @Setting
    @Path("backpack.sounds.on_remove")
    @Description("Sound to be played when the player takes out item from backpack")
    private BattleSound backpackSoundRemoveItem;

    @Setting
    @Path("radio.message_format")
    @Description("Radio message format")
    @Validation(notNull = true, silent = true)
    private String radioMessageFormat = "&6[RADIO] &8%player_displayname%&f: <message>";

    @Setting
    @Path("custom_items.medical_kit.health_bonus")
    @Description("Additional health give to the player")
    private int medicalKitBonusHealth;

    @Setting
    @Path("custom_items.medical_kit.use_sound")
    @Description("Sound to be played when using the medical kit")
    private BattleSound medicalKitUseSound;

    @Setting
    @Path("custom_items.adrenaline_shot.use_sound")
    @Description("Sound to be played when using the adrenaline shot")
    private BattleSound adrenalineShotUseSound;

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

    public boolean shouldAutoReloadGun() {
        return autoReloadGun;
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

    public boolean isBloodEffectEnabled() {
        return bloodEffectEnabled;
    }

    public double getBloodEffectParticleRatio() {
        return bloodEffectParticleRatio;
    }

    public double getEntityTrackMinDistance() {
        return entityTrackMinDistance;
    }

    public int getMaterialHardness(@NotNull Material material) {
        return materialHardness.getOrDefault(material, 0);
    }

    public int getEntityHardness(@NotNull EntityType entityType) {
        return entityHardness.getOrDefault(entityType, 0);
    }

    public boolean shouldHealOnGameStart() {
        return healOnGameStart;
    }

    public boolean shouldHealOnGameEnd() {
        return healOnGameEnd;
    }

    public int getRegionPartitionSize() {
        return regionPartitionSize;
    }

    public boolean isResourcePackEnabled() {
        return resourcePackEnabled;
    }

    public String getResourcePackCustomUrl() {
        return resourcePackCustomUrl;
    }

    @Nullable
    public List<String> getJoinSignDynamicLines() {
        return joinSignDynamicLines;
    }

    public long getJoinSignUpdateTime() {
        return joinSignUpdateTime;
    }

    @Nullable
    public BattleSound getBackpackSoundAddItemSuccess() {
        return backpackSoundAddItemSuccess;
    }

    @Nullable
    public BattleSound getBackpackSoundAddItemFailure() {
        return backpackSoundAddItemFailure;
    }

    @Nullable
    public BattleSound getBackpackSoundRemoveItem() {
        return backpackSoundRemoveItem;
    }

    @NotNull
    public String getRadioMessageFormat() {
        return radioMessageFormat;
    }

    public int getMedicalKitBonusHealth() {
        return medicalKitBonusHealth;
    }

    @Nullable
    public BattleSound getMedicalKitUseSound() {
        return medicalKitUseSound;
    }

    @Nullable
    public BattleSound getAdrenalineShotUseSound() {
        return adrenalineShotUseSound;
    }

    private void loadMaterialHardness(ConfigSection cs) throws Exception {
        materialHardness = new EnumMap<>(Material.class);
        for (String s : cs.getKeys(false)) {
            if (s.equals("_default_")) {
                int v = cs.get(s).asInt();
                for (Material mt : Material.values()) {
                    materialHardness.put(mt, v);
                }
            } else {
                List<?> k = cs.get(s + ".material").asList();
                int v = cs.get(s + ".value").asInt();
                for (Object pattern : k) {
                    Pattern p = Pattern.compile(pattern.toString().toUpperCase());
                    for (Material mt : Material.values()) {
                        if (p.matcher(mt.name()).matches()) {
                            materialHardness.put(mt, v);
                        }
                    }
                }
            }
        }
    }

    private void loadEntityHardness(ConfigSection cs) throws Exception {
        entityHardness = new EnumMap<>(EntityType.class);
        for (String s : cs.getKeys(false)) {
            if (s.equals("_default_")) {
                int v = cs.get(s).asInt();
                for (EntityType et : EntityType.values()) {
                    entityHardness.put(et, v);
                }
            } else {
                List<?> k = cs.get(s + ".types").asList();
                int v = cs.get(s + ".value").asInt();
                for (Object pattern : k) {
                    Pattern p = Pattern.compile(pattern.toString().toUpperCase());
                    for (EntityType et : EntityType.values()) {
                        if (p.matcher(et.name()).matches()) {
                            entityHardness.put(et, v);
                        }
                    }
                }
            }
        }
    }

    @PostHandler
    private void handle(ConfigDeserializer deserializer, ConfigSchema schema, ConfigSection section){
        try {
            loadMaterialHardness(Objects.requireNonNull(section.get("misc.material_hardness").asSection()));
            loadEntityHardness(Objects.requireNonNull(section.get("misc.entity_hardness").asSection()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
