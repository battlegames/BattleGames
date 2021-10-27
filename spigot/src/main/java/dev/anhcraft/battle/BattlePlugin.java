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
package dev.anhcraft.battle;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import dev.anhcraft.battle.api.*;
import dev.anhcraft.battle.api.advancement.AdvancementManager;
import dev.anhcraft.battle.api.arena.Arena;
import dev.anhcraft.battle.api.arena.ArenaManager;
import dev.anhcraft.battle.api.chat.ChatManager;
import dev.anhcraft.battle.api.economy.NativeCurrencies;
import dev.anhcraft.battle.api.effect.BattleEffect;
import dev.anhcraft.battle.api.effect.EffectOption;
import dev.anhcraft.battle.api.events.ConfigReloadEvent;
import dev.anhcraft.battle.api.gui.Gui;
import dev.anhcraft.battle.api.gui.GuiManager;
import dev.anhcraft.battle.api.inventory.item.*;
import dev.anhcraft.battle.api.market.Market;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.api.storage.data.ServerData;
import dev.anhcraft.battle.cmd.CommandInitializer;
import dev.anhcraft.battle.gui.BattleFunction;
import dev.anhcraft.battle.gui.menu.Advancements;
import dev.anhcraft.battle.gui.menu.ArenaChooser;
import dev.anhcraft.battle.gui.menu.BoosterMenu;
import dev.anhcraft.battle.gui.menu.KitMenu;
import dev.anhcraft.battle.gui.menu.backpack.*;
import dev.anhcraft.battle.gui.menu.market.*;
import dev.anhcraft.battle.system.BattleRegionRollback;
import dev.anhcraft.battle.system.BattleWorldRollback;
import dev.anhcraft.battle.system.integrations.ISWMIntegration;
import dev.anhcraft.battle.system.integrations.PapiExpansion;
import dev.anhcraft.battle.system.integrations.SWMIntegration;
import dev.anhcraft.battle.system.integrations.VaultApi;
import dev.anhcraft.battle.system.listeners.BlockListener;
import dev.anhcraft.battle.system.listeners.GameListener;
import dev.anhcraft.battle.system.listeners.PlayerListener;
import dev.anhcraft.battle.system.listeners.WorldListener;
import dev.anhcraft.battle.system.managers.*;
import dev.anhcraft.battle.system.managers.config.*;
import dev.anhcraft.battle.system.managers.item.BattleGrenadeManager;
import dev.anhcraft.battle.system.managers.item.BattleGunManager;
import dev.anhcraft.battle.system.managers.item.BattleItemManager;
import dev.anhcraft.battle.system.messengers.BungeeMessenger;
import dev.anhcraft.battle.system.renderers.bossbar.BossbarRenderer;
import dev.anhcraft.battle.system.renderers.scoreboard.PlayerScoreboard;
import dev.anhcraft.battle.system.renderers.scoreboard.ScoreboardRenderer;
import dev.anhcraft.battle.tasks.*;
import dev.anhcraft.battle.update.Updater;
import dev.anhcraft.battle.utils.ConfigHelper;
import dev.anhcraft.battle.utils.MaterialUtil;
import dev.anhcraft.battle.utils.State;
import dev.anhcraft.battle.utils.VersionUtil;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.jvmkit.utils.Condition;
import dev.anhcraft.jvmkit.utils.ReflectionUtil;
import net.objecthunter.exp4j.Expression;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BattlePlugin extends JavaPlugin implements BattleApi {
    public static final int BOSSBAR_UPDATE_INTERVAL = 1;
    public static final int SCOREBOARD_UPDATE_INTERVAL = 10;

    public final Map<OfflinePlayer, PlayerData> playerData = new ConcurrentHashMap<>();
    public final SystemConfig systemConf = new SystemConfig();
    public final GeneralConfig generalConf = new GeneralConfig();
    private final ServerData serverData = new ServerData();
    private final Market market = new Market();
    public JsonObject minecraftLocale;
    public File configFolder;
    public boolean syncDataTaskNeed;
    public boolean supportBungee;
    public boolean spigotBungeeEnabled;
    public boolean slimeWorldManagerSupport;
    public ISWMIntegration SWMIntegration;
    public Expression toExpConverter;
    public Expression toLevelConverter;
    public SimpleDateFormat longFormDate;
    public SimpleDateFormat shortFormDate1;
    public SimpleDateFormat shortFormDate2;
    public SimpleDateFormat shortFormDate3;
    public BattleWorldRollback battleWorldRollback;
    public BattleRegionRollback battleRegionRollback;
    // CONFIG
    public SystemConfigManager systemConfigManager;
    public GeneralConfigManager generalConfigManager;
    public LocaleConfigManager localeConfigManager;
    public ItemConfigManager itemConfigManager;
    public AmmoConfigManager ammoConfigManager;
    public MagazineConfigManager magazineConfigManager;
    public ScopeConfigManager scopeConfigManager;
    public GunConfigManager gunConfigManager;
    public GrenadeConfigManager grenadeConfigManager;
    public ModeConfigManager modeConfigManager;
    public ArenaConfigManager arenaConfigManager;
    public KitConfigManager kitConfigManager;
    public PerkConfigManager perkConfigManager;
    public BoosterConfigManager boosterConfigManager;
    public MarketConfigManager marketConfigManager;
    public AdvancementConfigManager advancementConfigManager;
    public GUIConfigManager guiConfigManager;
    public WorldConfigManager worldConfigManager;
    // SYSTEM
    public BattleChatManager chatManager;
    public BattleArenaManager arenaManager;
    public BattleDataManager dataManager;
    public BattleItemManager itemManager;
    public BattleGunManager gunManager;
    public BattleGrenadeManager grenadeManager;
    public BattleGuiManager guiManager;
    public BattleAdvancementManager advancementManager;
    public BungeeMessenger bungeeMessenger;
    // RENDERER
    public ScoreboardRenderer scoreboardRenderer;
    public BossbarRenderer bossbarRenderer;
    // TASKS
    public GameTask gameTask;
    public QueueTitleTask queueTitleTask;
    public QueueServerTask queueServerTask;
    public EntityTrackingTask entityTrackingTask;
    private PapiExpansion papiExpansion;
    // LISTENER
    public PlayerListener playerListener;

    @Override
    public void onEnable() {
        try {
            Class<?> sc = Class.forName("org.spigotmc.SpigotConfig");
            //noinspection ConstantConditions
            spigotBungeeEnabled = (boolean) ReflectionUtil.getStaticField(sc, "bungee");
        } catch (ClassNotFoundException e) {
            exit("BattleGames can only work on Spigot-based servers.");
            return;
        }
        getLogger().info("Consider to donate me if you think BattleGames is awesome <3");
        injectApiProvider();
        loadLegacyMaterial();

        configFolder = getDataFolder();
        configFolder.mkdir();
        if (getServer().getPluginManager().isPluginEnabled("SlimeWorldManager")) {
            slimeWorldManagerSupport = true;
            SWMIntegration = new SWMIntegration(this);
            getLogger().info("Hooked to SlimeWorldManager");
        }
        papiExpansion = new PapiExpansion(this);
        papiExpansion.register();

        systemConfigManager = new SystemConfigManager();
        generalConfigManager = new GeneralConfigManager();
        localeConfigManager = new LocaleConfigManager();
        itemConfigManager = new ItemConfigManager();
        ammoConfigManager = new AmmoConfigManager();
        magazineConfigManager = new MagazineConfigManager();
        scopeConfigManager = new ScopeConfigManager();
        gunConfigManager = new GunConfigManager();
        grenadeConfigManager = new GrenadeConfigManager();
        modeConfigManager = new ModeConfigManager();
        arenaConfigManager = new ArenaConfigManager();
        kitConfigManager = new KitConfigManager();
        perkConfigManager = new PerkConfigManager();
        boosterConfigManager = new BoosterConfigManager();
        marketConfigManager = new MarketConfigManager();
        advancementConfigManager = new AdvancementConfigManager();
        guiConfigManager = new GUIConfigManager();
        worldConfigManager = new WorldConfigManager();
        itemManager = new BattleItemManager(this);
        gunManager = new BattleGunManager(this);
        grenadeManager = new BattleGrenadeManager(this);
        arenaManager = new BattleArenaManager(this);
        chatManager = new BattleChatManager(this);
        advancementManager = new BattleAdvancementManager(this);
        guiManager = new BattleGuiManager(this);
        battleWorldRollback = new BattleWorldRollback(this);
        battleRegionRollback = new BattleRegionRollback(this);

        guiManager.registerGuiHandler(BattleFunction.class);
        guiManager.registerPagination("player_gun", new GunCompartment());
        guiManager.registerPagination("player_magazine", new MagazineCompartment());
        guiManager.registerPagination("player_ammo", new AmmoCompartment());
        guiManager.registerPagination("player_scope", new ScopeCompartment());
        guiManager.registerPagination("player_grenade", new GrenadeCompartment());
        guiManager.registerPagination("kits", new KitMenu());
        guiManager.registerPagination("arena_chooser", new ArenaChooser());
        guiManager.registerPagination("market_category", new CategoryMenu());
        guiManager.registerPagination("market_product", new ProductMenu());
        guiManager.registerPagination("market_transaction", new TransactionMenu());
        guiManager.registerPagination("boosters", new BoosterMenu());
        guiManager.registerPagination("editor_market_category", new CategoryMenuEditor());
        guiManager.registerPagination("editor_market_product", new ProductMenuEditor());
        guiManager.registerPagination("advancements", new Advancements());

        reloadConfigs();

        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
        playerListener = new PlayerListener(this);
        getServer().getPluginManager().registerEvents(playerListener, this);
        getServer().getOnlinePlayers().forEach(playerListener::handleJoin);

        getServer().getScheduler().runTaskTimerAsynchronously(this, scoreboardRenderer = new ScoreboardRenderer(), 0, SCOREBOARD_UPDATE_INTERVAL);
        getServer().getScheduler().runTaskTimerAsynchronously(this, bossbarRenderer = new BossbarRenderer(), 0, BOSSBAR_UPDATE_INTERVAL);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new DataSavingTask(this), 0, 60);
        if (syncDataTaskNeed) {
            getServer().getScheduler().runTaskTimerAsynchronously(this, new DataLoadingTask(this), 0, 60);
        }
        if (generalConf.getJoinSignUpdateTime() >= 20) {
            getServer().getScheduler().runTaskTimerAsynchronously(this, new JoinSignUpdateTask(this), 0, generalConf.getJoinSignUpdateTime());
        }
        getServer().getScheduler().runTaskTimerAsynchronously(this, queueTitleTask = new QueueTitleTask(), 0, 20);
        getServer().getScheduler().runTaskTimer(this, gameTask = new GameTask(this), 0, 1);
        getServer().getScheduler().runTaskTimerAsynchronously(this, entityTrackingTask = new EntityTrackingTask(this), 0, 10);
        if (supportBungee) {
            getServer().getScheduler().runTaskTimerAsynchronously(this, queueServerTask = new QueueServerTask(this), 0, 20);
            getServer().getMessenger().registerIncomingPluginChannel(this, BungeeMessenger.BATTLE_CHANNEL, bungeeMessenger = new BungeeMessenger(this));
            getServer().getMessenger().registerOutgoingPluginChannel(this, BungeeMessenger.BATTLE_CHANNEL);
        }
        getServer().getScheduler().runTaskTimer(this, new WorldTask(this), 0, 100);

        new CommandInitializer(this);

        Metrics metrics = new Metrics(this, 6080);
        metrics.addCustomChart(new SimplePie("license_type", () -> "premium"));

        getServer().getScheduler().runTaskLater(this, () -> {
            if (VaultApi.init()) {
                NativeCurrencies.VAULT.setEconomy(VaultApi.getEconomyApi());
            } else {
                exit("Failed to hook to Vault");
            }
        }, 20);
    }

    private void loadLegacyMaterial() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/material.txt")));
            while(reader.ready()) {
                String line = reader.readLine();
                String[] p = line.split(" ");
                if(p.length == 3){
                    MaterialUtil.registerLegacyMaterial(p[0], Integer.parseInt(p[1]), p[2]);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void injectApiProvider() {
        ApiProvider ap = new ApiProvider();
        ap.set(this);
        try {
            Class<?> clazz = ApiProvider.class;
            Field pf = clazz.getDeclaredField("provider");
            pf.setAccessible(true);
            pf.set(null, ap);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        arenaManager.listGames(game -> {/*
            if(game instanceof LocalGame) {
                ((LocalGame) game).getPlayers().values().forEach(player -> {
                    if (player.getBackupInventory() != null)
                        player.toBukkit().getInventory().setContents(player.getBackupInventory());
                });
            }*/ // the code below will handle this action
            arenaManager.destroy(game);
        });
        dataManager.saveServerData();
        playerData.keySet().forEach(dataManager::savePlayerData);
        getServer().getWorlds().stream()
                .flatMap((Function<World, Stream<Entity>>) world -> world.getEntities().stream())
                .forEach(entity -> {
            if (entity instanceof HumanEntity) return;
            if (entity.hasMetadata("abm_temp_entity")) entity.remove();
        });
        dataManager.destroy();
    }

    @Override
    public void reloadConfig() {

    }

    public synchronized void reloadConfigs() {
        ConfigReloadEvent event = new ConfigReloadEvent();
        getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        systemConfigManager.reloadConfig();
        generalConfigManager.reloadConfig();
        localeConfigManager.reloadConfig();
        itemConfigManager.reloadConfig();
        ammoConfigManager.reloadConfig();
        magazineConfigManager.reloadConfig();
        scopeConfigManager.reloadConfig();
        gunConfigManager.reloadConfig();
        grenadeConfigManager.reloadConfig();
        modeConfigManager.reloadConfig();
        arenaConfigManager.reloadConfig();
        kitConfigManager.reloadConfig();
        perkConfigManager.reloadConfig();
        boosterConfigManager.reloadConfig();
        advancementConfigManager.reloadConfig();
        worldConfigManager.reloadConfig();
        if (VersionUtil.compareVersion(Objects.requireNonNull(systemConfigManager.getSettings().getString("last_config_version")), "2") < 0) {
            getLogger().info("Looks like the current config system has been outdated.");
            getLogger().info("The plugin will try to update it for you!");
            getLogger().info("Update details: v1 -> v2");
            getLogger().info("- " + marketConfigManager.getFilePath());
            getLogger().info("- " + guiConfigManager.getFilePath());
            ///////////////
            marketConfigManager.reloadConfig(true);
            guiConfigManager.reloadConfig(true);
            ///////////////
            if (!systemConf.isRemoteConfigEnabled()) {
                getLogger().info("All done! Saving changes...");
                systemConfigManager.getSettings().set("last_config_version", 2);
                systemConfigManager.saveConfig();
                ConfigHelper.load(SystemConfig.class, systemConfigManager.getSettings(), systemConf);
            }
        } else {
            marketConfigManager.reloadConfig();
            guiConfigManager.reloadConfig();
        }
    }

    @NotNull
    public File getEditorFolder() {
        return new File(configFolder, "editor");
    }

    public void resetScoreboard(Player player) {
        BattleScoreboard sb = generalConf.getDefaultScoreboard();
        if (sb == null || !sb.isEnabled()) {
            scoreboardRenderer.removeScoreboard(player);
            return;
        }
        PlayerScoreboard ps = new PlayerScoreboard(player, sb.getTitle(), sb.getContent(), sb.getFixedLength());
        scoreboardRenderer.setScoreboard(ps);
    }

    @Override
    public @NotNull SystemConfig getSystemConfig() {
        return systemConf;
    }

    @Override
    public @NotNull GeneralConfig getGeneralConfig() {
        return generalConf;
    }

    @Override
    public @NotNull JsonObject getMinecraftLocale() {
        return minecraftLocale;
    }

    @Override
    public @Nullable String getLocalizedMessage(@NotNull String path) {
        Object o = localeConfigManager.getSettings().get(path);
        if (o == null) return null;
        return o instanceof Collection ? ((Collection<?>) o).stream().map(Object::toString).collect(Collectors.joining(", ")) : o.toString();
    }

    @Override
    public @NotNull String getLocalizedMessage(@NotNull String path, @NotNull String def) {
        Object o = localeConfigManager.getSettings().get(path);
        if (o == null) return def;
        return o instanceof Collection ? ((Collection<?>) o).stream().map(Object::toString).collect(Collectors.joining(", ")) : o.toString();
    }

    @Override
    public @Nullable List<String> getLocalizedMessages(@NotNull String path) {
        Object o = localeConfigManager.getSettings().get(path);
        if (o == null) return null;
        return o instanceof Collection ? ((Collection<?>) o).stream().map(Object::toString).collect(Collectors.toList()) : Collections.singletonList(o.toString());
    }

    @Override
    public @NotNull List<String> getLocalizedMessages(@NotNull String path, @NotNull String def) {
        Object o = localeConfigManager.getSettings().get(path);
        if (o == null) return Collections.singletonList(def);
        return o instanceof Collection ? ((Collection<?>) o).stream().map(Object::toString).collect(Collectors.toList()) : Collections.singletonList(o.toString());
    }

    @Override
    public @NotNull List<String> getLocalizedMessages(@NotNull String path, @NotNull List<String> def) {
        Object o = localeConfigManager.getSettings().get(path);
        if (o == null) return def;
        return o instanceof Collection ? ((Collection<?>) o).stream().map(Object::toString).collect(Collectors.toList()) : Collections.singletonList(o.toString());
    }

    @Override
    @NotNull
    public Map<String, String> mapInfo(@NotNull InfoHolder holder) {
        Condition.argNotNull("holder", holder);
        return holder.getMap().entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    Object data = entry.getValue();
                    if (data instanceof State) {
                        String s = localeConfigManager.getSettings().getString(((State) data).getLocalePath());
                        return s == null ? "" : s;
                    } else if (data instanceof Double) {
                        double v = (double) data;
                        if (Double.isNaN(v) || Double.isInfinite(v)) {
                            return String.valueOf(v);
                        } else {
                            char[] arr = String.format("%f", v).toCharArray();
                            StringBuilder stringBuilder = new StringBuilder();
                            boolean b = false;
                            for (int j = arr.length - 1; j >= 0; j--) {
                                if (arr[j] == '0' && !b) continue;
                                if (arr[j] != '0') {
                                    b = true;
                                    if (arr[j] == '.' && stringBuilder.length() == 0) {
                                        stringBuilder.append('0');
                                    }
                                }
                                stringBuilder.insert(0, arr[j]);
                            }
                            return stringBuilder.toString();
                        }
                    } else if (data instanceof Float) {
                        float v = (float) data;
                        if (Float.isNaN(v) || Float.isInfinite(v)) {
                            return String.valueOf(v);
                        } else {
                            char[] arr = String.format("%f", v).toCharArray();
                            StringBuilder stringBuilder = new StringBuilder();
                            boolean b = false;
                            for (int j = arr.length - 1; j >= 0; j--) {
                                if (arr[j] == '0' && !b) continue;
                                if (arr[j] != '0') {
                                    b = true;
                                    if (arr[j] == '.' && stringBuilder.length() == 0) {
                                        stringBuilder.append('0');
                                    }
                                }
                                stringBuilder.insert(0, arr[j]);
                            }
                            return stringBuilder.toString();
                        }
                    } else if (data instanceof Integer)
                        return Integer.toString((Integer) data);
                    else if (data instanceof Long)
                        return Long.toString((Long) data);
                    else if (data instanceof String)
                        return data.toString();
                    return "Error! (data class=" + data.getClass().getSimpleName() + ")";
                }
        ));
    }

    public PapiExpansion getPapiExpansion() {
        return papiExpansion;
    }

    @NotNull
    @Override
    public String formatLongFormDate(@NotNull Date date) {
        Condition.argNotNull("date", date);
        return longFormDate.format(date);
    }

    @NotNull
    @Override
    public String formatShortFormTime(long time) {
        final long x = 1000;
        if (time < 60 * x) return formatShortFormDateSeconds(new Date(time));
        else if (time < 60 * 60 * x) return formatShortFormDateMinutes(new Date(time));
        else return formatShortFormDateHours(new Date(time));
    }

    @NotNull
    @Override
    public String formatShortFormDateHours(@NotNull Date date) {
        Condition.argNotNull("date", date);
        return shortFormDate1.format(date);
    }

    @NotNull
    @Override
    public String formatShortFormDateMinutes(@NotNull Date date) {
        Condition.argNotNull("date", date);
        return shortFormDate2.format(date);
    }

    @NotNull
    @Override
    public String formatShortFormDateSeconds(@NotNull Date date) {
        Condition.argNotNull("date", date);
        return shortFormDate3.format(date);
    }

    @Override
    public float getDefaultWalkingSpeed() {
        return (float) generalConf.getWalkSpeed();
    }

    @Override
    public float getDefaultFlyingSpeed() {
        return (float) generalConf.getFlySpeed();
    }

    @Override
    public long calculateExp(int level) {
        return (long) toExpConverter.setVariable("x", level).evaluate();
    }

    @Override
    public int calculateLevel(long exp) {
        return (int) toLevelConverter.setVariable("x", exp).evaluate();
    }

    @Override
    @Nullable
    public PlayerData getPlayerData(@Nullable OfflinePlayer player) {
        return playerData.get(player);
    }

    @NotNull
    @Override
    public ServerData getServerData() {
        return serverData;
    }

    @Override
    public @Nullable WorldSettings getWorldSettings(String world) {
        return worldConfigManager.getWorldSettings(world);
    }

    @Override
    public Arena getArena(@Nullable String id) {
        return arenaConfigManager.ARENA_MAP.get(id);
    }

    @Override
    public @Nullable Gui getGui(@Nullable String id) {
        return guiManager.GUI.get(id);
    }

    @Override
    public @Nullable BattleItemModel getItemModel(@NotNull ItemType itemType, @NotNull String id) {
        switch (itemType) {
            case AMMO:
                return getAmmoModel(id);
            case GUN:
                return getGunModel(id);
            case MAGAZINE:
                return getMagazineModel(id);
            case SCOPE:
                return getScopeModel(id);
            case GRENADE:
                return getGrenadeModel(id);
        }
        return null;
    }

    @Override
    public AmmoModel getAmmoModel(@Nullable String id) {
        return ammoConfigManager.AMMO_MAP.get(id);
    }

    @Override
    public GunModel getGunModel(@Nullable String id) {
        return gunConfigManager.GUN_MAP.get(id);
    }

    @Override
    public MagazineModel getMagazineModel(@Nullable String id) {
        return magazineConfigManager.MAGAZINE_MAP.get(id);
    }

    @Override
    public ScopeModel getScopeModel(@Nullable String id) {
        return scopeConfigManager.SCOPE_MAP.get(id);
    }

    @Override
    public GrenadeModel getGrenadeModel(@Nullable String id) {
        return grenadeConfigManager.GRENADE_MAP.get(id);
    }

    @Override
    public Kit getKit(@Nullable String id) {
        return kitConfigManager.KIT_MAP.get(id);
    }

    @Override
    public @Nullable Perk getPerk(@Nullable String id) {
        return perkConfigManager.PERK_MAP.get(id);
    }

    @Override
    public @Nullable Booster getBooster(@Nullable String id) {
        return boosterConfigManager.BOOSTER_MAP.get(id);
    }

    @NotNull
    @Override
    public List<Arena> listArenas() {
        return ImmutableList.copyOf(arenaConfigManager.ARENA_MAP.values());
    }

    @Override
    public void listArenas(@NotNull Consumer<Arena> consumer) {
        Condition.argNotNull("consumer", consumer);
        arenaConfigManager.ARENA_MAP.values().forEach(consumer);
    }

    @Override
    public @NotNull List<Gui> listGui() {
        return ImmutableList.copyOf(guiManager.GUI.values());
    }

    @Override
    public void listGui(@NotNull Consumer<Gui> consumer) {
        Condition.argNotNull("consumer", consumer);
        guiManager.GUI.values().forEach(consumer);
    }

    @NotNull
    @Override
    public List<AmmoModel> listAmmoModels() {
        return ImmutableList.copyOf(ammoConfigManager.AMMO_MAP.values());
    }

    @Override
    public void listAmmoModels(@NotNull Consumer<AmmoModel> consumer) {
        Condition.argNotNull("consumer", consumer);
        ammoConfigManager.AMMO_MAP.values().forEach(consumer);
    }

    @NotNull
    @Override
    public List<GunModel> listGunModels() {
        return ImmutableList.copyOf(gunConfigManager.GUN_MAP.values());
    }

    @Override
    public void listGunModels(@NotNull Consumer<GunModel> consumer) {
        Condition.argNotNull("consumer", consumer);
        gunConfigManager.GUN_MAP.values().forEach(consumer);
    }

    @NotNull
    @Override
    public List<MagazineModel> listMagazineModels() {
        return ImmutableList.copyOf(magazineConfigManager.MAGAZINE_MAP.values());
    }

    @Override
    public void listMagazineModels(@NotNull Consumer<MagazineModel> consumer) {
        Condition.argNotNull("consumer", consumer);
        magazineConfigManager.MAGAZINE_MAP.values().forEach(consumer);
    }

    @Override
    public @NotNull List<ScopeModel> listScopes() {
        return ImmutableList.copyOf(scopeConfigManager.SCOPE_MAP.values());
    }

    @Override
    public void listScopes(@NotNull Consumer<ScopeModel> consumer) {
        Condition.argNotNull("consumer", consumer);
        scopeConfigManager.SCOPE_MAP.values().forEach(consumer);
    }

    @Override
    @NotNull
    public List<GrenadeModel> listGrenades() {
        return ImmutableList.copyOf(grenadeConfigManager.GRENADE_MAP.values());
    }

    @Override
    public void listGrenades(@NotNull Consumer<GrenadeModel> consumer) {
        Condition.argNotNull("consumer", consumer);
        grenadeConfigManager.GRENADE_MAP.values().forEach(consumer);
    }

    @NotNull
    @Override
    public List<Kit> listKits() {
        return ImmutableList.copyOf(kitConfigManager.KIT_MAP.values());
    }

    @Override
    public void listKits(@NotNull Consumer<Kit> consumer) {
        Condition.argNotNull("consumer", consumer);
        kitConfigManager.KIT_MAP.values().forEach(consumer);
    }

    @Override
    @NotNull
    public List<Perk> listPerks() {
        return ImmutableList.copyOf(perkConfigManager.PERK_MAP.values());
    }

    @Override
    public void listPerks(@NotNull Consumer<Perk> consumer) {
        Condition.argNotNull("consumer", consumer);
        perkConfigManager.PERK_MAP.values().forEach(consumer);
    }

    @Override
    public @NotNull List<Booster> listBoosters() {
        return ImmutableList.copyOf(boosterConfigManager.BOOSTER_MAP.values());
    }

    @Override
    public void listBoosters(@NotNull Consumer<Booster> consumer) {
        Condition.argNotNull("consumer", consumer);
        boosterConfigManager.BOOSTER_MAP.values().forEach(consumer);
    }

    @Override
    public @NotNull ArenaManager getArenaManager() {
        return arenaManager;
    }

    @Override
    public @NotNull ItemManager getItemManager() {
        return itemManager;
    }

    @Override
    public @NotNull GuiManager getGuiManager() {
        return guiManager;
    }

    @Override
    public @NotNull ChatManager getChatManager() {
        return chatManager;
    }

    @Override
    public @NotNull AdvancementManager getAdvancementManager() {
        return advancementManager;
    }

    @Override
    public boolean hasBungeecordSupport() {
        return supportBungee;
    }

    @Override
    public boolean hasSlimeWorldManagerSupport() {
        return slimeWorldManagerSupport;
    }

    @Override
    public void playEffect(@NotNull Location location, @NotNull BattleEffect effect) {
        Number delayTime = ((Number) effect.getOptions().getOrDefault(EffectOption.REPEAT_DELAY, 0));
        int maxRepeat = (int) effect.getOptions().getOrDefault(EffectOption.REPEAT_TIMES, 0);
        BiConsumer<Location, BattleEffect> consumer = effect.getType().getEffectConsumer();
        AtomicInteger id = new AtomicInteger();
        id.set(getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            private int counter;

            @Override
            public void run() {
                consumer.accept(location, effect);
                if (counter++ >= maxRepeat) {
                    getServer().getScheduler().cancelTask(id.get());
                }
            }
        }, 0, delayTime.longValue()).getTaskId());
    }

    @Override
    public @NotNull Market getMarket() {
        return market;
    }

    private void exit(String msg) {
        getLogger().warning("Plugin is now shutting down...");
        getLogger().info("Reason: " + msg);
        getServer().getPluginManager().disablePlugin(this);
    }
}
