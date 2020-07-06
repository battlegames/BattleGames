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
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.api.GeneralConfig;
import dev.anhcraft.battle.api.SystemConfig;
import dev.anhcraft.battle.api.advancement.AdvancementManager;
import dev.anhcraft.battle.api.arena.Arena;
import dev.anhcraft.battle.api.arena.ArenaManager;
import dev.anhcraft.battle.api.chat.ChatManager;
import dev.anhcraft.battle.api.effect.BattleEffect;
import dev.anhcraft.battle.api.effect.EffectOption;
import dev.anhcraft.battle.api.events.ConfigReloadEvent;
import dev.anhcraft.battle.api.gui.Gui;
import dev.anhcraft.battle.api.gui.GuiManager;
import dev.anhcraft.battle.api.inventory.item.*;
import dev.anhcraft.battle.api.market.Market;
import dev.anhcraft.battle.api.misc.BattleScoreboard;
import dev.anhcraft.battle.api.misc.Booster;
import dev.anhcraft.battle.api.misc.Kit;
import dev.anhcraft.battle.api.misc.Perk;
import dev.anhcraft.battle.api.storage.data.PlayerData;
import dev.anhcraft.battle.api.storage.data.ServerData;
import dev.anhcraft.battle.cmd.CommandInitializer;
import dev.anhcraft.battle.gui.inst.CommonFunctions;
import dev.anhcraft.battle.gui.inst.ItemFunctions;
import dev.anhcraft.battle.gui.inst.MarketFunctions;
import dev.anhcraft.battle.gui.menu.Advancements;
import dev.anhcraft.battle.gui.menu.ArenaChooser;
import dev.anhcraft.battle.gui.menu.BoosterMenu;
import dev.anhcraft.battle.gui.menu.KitMenu;
import dev.anhcraft.battle.gui.menu.backpack.*;
import dev.anhcraft.battle.gui.menu.market.*;
import dev.anhcraft.battle.system.BattleRegionRollback;
import dev.anhcraft.battle.system.BattleWorldRollback;
import dev.anhcraft.battle.system.PremiumConnector;
import dev.anhcraft.battle.system.integrations.ISWMIntegration;
import dev.anhcraft.battle.system.integrations.PapiExpansion;
import dev.anhcraft.battle.system.integrations.SWMIntegration;
import dev.anhcraft.battle.system.integrations.VaultApi;
import dev.anhcraft.battle.system.listeners.BlockListener;
import dev.anhcraft.battle.system.listeners.GameListener;
import dev.anhcraft.battle.system.listeners.PlayerListener;
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
import dev.anhcraft.battle.utils.CraftStats;
import dev.anhcraft.battle.utils.info.InfoHolder;
import dev.anhcraft.battle.utils.info.State;
import dev.anhcraft.craftkit.CraftExtension;
import dev.anhcraft.craftkit.helpers.TaskHelper;
import dev.anhcraft.craftkit.utils.ServerUtil;
import dev.anhcraft.jvmkit.utils.Condition;
import dev.anhcraft.jvmkit.utils.MathUtil;
import dev.anhcraft.jvmkit.utils.ReflectionUtil;
import net.objecthunter.exp4j.Expression;
import org.bstats.bukkit.Metrics;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BattlePlugin extends JavaPlugin implements BattleApi {
    public static final int BOSSBAR_UPDATE_INTERVAL = 1;
    public static final int SCOREBOARD_UPDATE_INTERVAL = 10;

    public final Map<OfflinePlayer, PlayerData> playerData = new ConcurrentHashMap<>();
    private final ServerData serverData = new ServerData();
    private final Market market = new Market();
    public final SystemConfig systemConf = new SystemConfig();
    public final GeneralConfig generalConf = new GeneralConfig();
    public PremiumConnector premiumConnector;
    public CraftExtension extension;
    public File configFolder;
    public boolean syncDataTaskNeed;
    public boolean supportBungee;
    public boolean spigotBungeeEnabled;
    public boolean slimeWorldManagerSupport;
    private PapiExpansion papiExpansion;
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
        if (!VaultApi.init()) exit("Failed to hook to Vault");
        getLogger().info("Consider to donate me if you think BattleGames is awesome <3");
        injectApiProvider();

        configFolder = getDataFolder();
        configFolder.mkdir();
        extension = CraftExtension.of(BattlePlugin.class);
        extension.requireAtLeastVersion("1.1.9");
        premiumConnector = new PremiumConnector(this);
        premiumConnector.onIntegrate();
        if(getServer().getPluginManager().isPluginEnabled("SlimeWorldManager")){
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
        itemManager = new BattleItemManager(this);
        gunManager = new BattleGunManager(this);
        grenadeManager = new BattleGrenadeManager(this);
        arenaManager = new BattleArenaManager(this);
        chatManager = new BattleChatManager(this);
        advancementManager = new BattleAdvancementManager(this);
        guiManager = new BattleGuiManager(this);
        battleWorldRollback = new BattleWorldRollback(this);
        battleRegionRollback = new BattleRegionRollback(this);
        premiumConnector.onInitSystem();

        guiManager.registerGuiHandler(CommonFunctions.class);
        guiManager.registerGuiHandler(MarketFunctions.class);
        guiManager.registerGuiHandler(ItemFunctions.class);
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
        PlayerListener pl = new PlayerListener(this);
        getServer().getPluginManager().registerEvents(pl, this);
        getServer().getOnlinePlayers().forEach(pl::handleJoin);
        premiumConnector.onRegisterEvents();

        TaskHelper taskHelper = extension.getTaskHelper();
        taskHelper.newAsyncTimerTask(() -> CraftStats.sendData(this), 100, 576000);
        taskHelper.newAsyncTimerTask(scoreboardRenderer = new ScoreboardRenderer(), 0, SCOREBOARD_UPDATE_INTERVAL);
        taskHelper.newAsyncTimerTask(bossbarRenderer = new BossbarRenderer(), 0, BOSSBAR_UPDATE_INTERVAL);
        taskHelper.newAsyncTimerTask(new DataSavingTask(this), 0, 60);
        if(syncDataTaskNeed) {
            taskHelper.newAsyncTimerTask(new DataLoadingTask(this), 0, 60);
        }
        if(generalConf.getJoinSignUpdateTime() >= 20) {
            taskHelper.newTimerTask(new JoinSignUpdateTask(this), 0, generalConf.getJoinSignUpdateTime());
        }
        taskHelper.newAsyncTimerTask(queueTitleTask = new QueueTitleTask(), 0, 20);
        taskHelper.newTimerTask(gameTask = new GameTask(this), 0, 1);
        taskHelper.newAsyncTimerTask(entityTrackingTask = new EntityTrackingTask(this), 0, 10);
        if(supportBungee){
            taskHelper.newAsyncTimerTask(queueServerTask = new QueueServerTask(this), 0, 20);
            getServer().getMessenger().registerIncomingPluginChannel(this, BungeeMessenger.BATTLE_CHANNEL, bungeeMessenger = new BungeeMessenger(this));
            getServer().getMessenger().registerOutgoingPluginChannel(this, BungeeMessenger.BATTLE_CHANNEL);
        }
        premiumConnector.onRegisterTasks();

        new CommandInitializer(this);

        Metrics metrics = new Metrics(this, 6080);
        metrics.addCustomChart(new Metrics.SimplePie("license_type", () -> premiumConnector.isSuccess() ? "premium" : "free"));
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
    public void onDisable(){
        premiumConnector.onDisable();
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
        ServerUtil.getAllEntities(entity -> {
            if(entity instanceof HumanEntity) return;
            if(entity.hasMetadata("abm_temp_entity")) entity.remove();
        });
        dataManager.destroy();
        CraftExtension.unregister(BattlePlugin.class);
    }

    @Override
    public void reloadConfig(){

    }

    public synchronized void reloadConfigs(){
        ConfigReloadEvent event = new ConfigReloadEvent();
        getServer().getPluginManager().callEvent(event);
        if(event.isCancelled()) return;
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
        marketConfigManager.reloadConfig();
        advancementConfigManager.reloadConfig();
        guiConfigManager.reloadConfig();
        premiumConnector.onReloadConfig();
    }

    @NotNull
    public File getEditorFolder(){
        return new File(configFolder, "editor");
    }

    public <T> Collection<T> limit(String k, Set<T> strings, int max){
        if(premiumConnector.isSuccess() || strings.size() <= max){
            return strings;
        } else {
            getLogger().warning(k+" is limited in free version! ("+strings.size()+"/"+max+")");
            return strings.stream().limit(max).collect(Collectors.toSet());
        }
    }

    public void resetScoreboard(Player player) {
        BattleScoreboard sb = generalConf.getDefaultScoreboard();
        if(sb == null || !sb.isEnabled()) {
            scoreboardRenderer.removeScoreboard(player);
            return;
        }
        PlayerScoreboard ps = new PlayerScoreboard(player, sb.getTitle(), sb.getContent(), sb.getFixedLength());
        scoreboardRenderer.setScoreboard(ps);
    }

    @Override
    public boolean isPremium() {
        return premiumConnector.isSuccess();
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
    public @Nullable String getLocalizedMessage(@NotNull String path) {
        Object o = localeConfigManager.getSettings().get(path);
        if(o == null) return null;
        return o instanceof Collection ? ((Collection<?>) o).stream().map(Object::toString).collect(Collectors.joining(", ")) : o.toString();
    }

    @Override
    public @NotNull String getLocalizedMessage(@NotNull String path, @NotNull String def) {
        Object o = localeConfigManager.getSettings().get(path);
        if(o == null) return def;
        return o instanceof Collection ? ((Collection<?>) o).stream().map(Object::toString).collect(Collectors.joining(", ")) : o.toString();
    }

    @Override
    public @Nullable List<String> getLocalizedMessages(@NotNull String path) {
        Object o = localeConfigManager.getSettings().get(path);
        if(o == null) return null;
        return o instanceof Collection ? ((Collection<?>) o).stream().map(Object::toString).collect(Collectors.toList()) : Collections.singletonList(o.toString());
    }

    @Override
    public @NotNull List<String> getLocalizedMessages(@NotNull String path, @NotNull String def) {
        Object o = localeConfigManager.getSettings().get(path);
        if(o == null) return Collections.singletonList(def);
        return o instanceof Collection ? ((Collection<?>) o).stream().map(Object::toString).collect(Collectors.toList()) : Collections.singletonList(o.toString());
    }

    @Override
    public @NotNull List<String> getLocalizedMessages(@NotNull String path, @NotNull List<String> def) {
        Object o = localeConfigManager.getSettings().get(path);
        if(o == null) return def;
        return o instanceof Collection ? ((Collection<?>) o).stream().map(Object::toString).collect(Collectors.toList()) : Collections.singletonList(o.toString());
    }

    @Override
    @NotNull
    public Map<String, String> mapInfo(@NotNull InfoHolder holder){
        Condition.argNotNull("holder", holder);
        return holder.getMap().entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    Object data = entry.getValue();
                    if(data instanceof State){
                        String s = localeConfigManager.getSettings().getString(((State) data).getLocalePath());
                        return s == null ? "" : s;
                    }
                    else if(data instanceof Double)
                        return MathUtil.formatRound((Double) data, 3);
                    else if(data instanceof Float)
                        return MathUtil.formatRound((Float) data, 3);
                    else if(data instanceof Integer)
                        return Integer.toString((Integer) data);
                    else if(data instanceof Long)
                        return Long.toString((Long) data);
                    else if(data instanceof String)
                        return data.toString();
                    return "Error! (data class="+data.getClass().getSimpleName()+")";
                }
        ));
    }

    public PapiExpansion getPapiExpansion() {
        return papiExpansion;
    }

    @NotNull
    @Override
    public String formatLongFormDate(@NotNull Date date){
        Condition.argNotNull("date", date);
        return longFormDate.format(date);
    }

    @NotNull
    @Override
    public String formatShortFormTime(long time){
        final long x = 1000;
        if(time < 60 * x) return formatShortFormDateSeconds(new Date(time));
        else if(time < 60 * 60 * x) return formatShortFormDateMinutes(new Date(time));
        else return formatShortFormDateHours(new Date(time));
    }

    @NotNull
    @Override
    public String formatShortFormDateHours(@NotNull Date date){
        Condition.argNotNull("date", date);
        return shortFormDate1.format(date);
    }

    @NotNull
    @Override
    public String formatShortFormDateMinutes(@NotNull Date date){
        Condition.argNotNull("date", date);
        return shortFormDate2.format(date);
    }

    @NotNull
    @Override
    public String formatShortFormDateSeconds(@NotNull Date date){
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
    public Arena getArena(@Nullable String id) {
        return arenaConfigManager.ARENA_MAP.get(id);
    }

    @Override
    public @Nullable Gui getGui(@Nullable String id) {
        return guiManager.GUI.get(id);
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
    @NotNull
    public List<String> getLobbyServers() {
        return generalConf.getBungeeLobbies();
    }

    @Override
    public int getMaxReconnectionTries() {
        return generalConf.getBungeeReconnectTries();
    }

    @Override
    public long getConnectionDelay() {
        return generalConf.getBungeeConnectDelay();
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
        id.set(extension.getTaskHelper().newAsyncTimerTask(new Runnable() {
            private int counter;

            @Override
            public void run() {
                if(counter++ == maxRepeat){
                    extension.getTaskHelper().cancelTask(id.get());
                    return;
                }
                consumer.accept(location, effect);
            }
        }, 0, delayTime.longValue()));
    }

    @Override
    public @NotNull Market getMarket() {
        return market;
    }

    private void exit(String msg) {
        getLogger().warning("Plugin is now shutting down...");
        getLogger().info("Reason: "+msg);
        getServer().getPluginManager().disablePlugin(this);
    }
}
