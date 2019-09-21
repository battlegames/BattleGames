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
package dev.anhcraft.abm;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import dev.anhcraft.abm.api.*;
import dev.anhcraft.abm.api.game.ABTeam;
import dev.anhcraft.abm.api.game.Arena;
import dev.anhcraft.abm.api.game.LocalGame;
import dev.anhcraft.abm.api.game.Mode;
import dev.anhcraft.abm.api.gui.Gui;
import dev.anhcraft.abm.api.inventory.items.*;
import dev.anhcraft.abm.api.misc.Kit;
import dev.anhcraft.abm.api.misc.info.*;
import dev.anhcraft.abm.api.storage.StorageType;
import dev.anhcraft.abm.api.storage.data.PlayerData;
import dev.anhcraft.abm.api.storage.data.ServerData;
import dev.anhcraft.abm.cmd.BattleCommand;
import dev.anhcraft.abm.gui.*;
import dev.anhcraft.abm.system.handlers.GunHandler;
import dev.anhcraft.abm.system.handlers.Handler;
import dev.anhcraft.abm.system.integrations.PapiExpansion;
import dev.anhcraft.abm.system.integrations.VaultApi;
import dev.anhcraft.abm.system.listeners.BlockListener;
import dev.anhcraft.abm.system.listeners.GameListener;
import dev.anhcraft.abm.system.listeners.PlayerListener;
import dev.anhcraft.abm.system.managers.*;
import dev.anhcraft.abm.system.messengers.BungeeMessenger;
import dev.anhcraft.abm.system.renderers.bossbar.BossbarRenderer;
import dev.anhcraft.abm.system.renderers.scoreboard.PlayerScoreboard;
import dev.anhcraft.abm.system.renderers.scoreboard.ScoreboardRenderer;
import dev.anhcraft.abm.tasks.*;
import dev.anhcraft.craftkit.cb_common.lang.enumeration.NMSVersion;
import dev.anhcraft.craftkit.helpers.TaskHelper;
import dev.anhcraft.craftkit.utils.ServerUtil;
import dev.anhcraft.jvmkit.helpers.HTTPConnectionHelper;
import dev.anhcraft.jvmkit.utils.*;
import net.md_5.bungee.api.ChatColor;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class BattlePlugin extends JavaPlugin implements BattleAPI {
    public static final long BOSSBAR_UPDATE_INTERVAL = 10;
    public static final long SCOREBOARD_UPDATE_INTERVAL = 10;
    private static final String[] CONFIG_FILES = new String[]{
            "system.yml",
            "general.yml",
            "locale/en_us.yml", // PUT DEFAULT LOCALE HERE
            "modes.yml",
            "arenas.yml",
            // START: ATTACHMENTS
            "items/ammo.yml",
            "items/magazines.yml",
            "items/scopes.yml",
            // END: ATTACHMENTS
            "items/guns.yml",
            "items/items.yml",
            "gui.yml " + (NMSVersion.getNMSVersion().isNewerOrSame(NMSVersion.v1_13_R1) ? "gui.yml" : "gui.legacy.yml"),
            "kits.yml"
    };
    private static final FileConfiguration[] CONFIG = new FileConfiguration[CONFIG_FILES.length];
    public final Map<OfflinePlayer, PlayerData> PLAYER_MAP = new ConcurrentHashMap<>();
    private final Map<String, Arena> ARENA_MAP = new HashMap<>();
    private final Map<String, AmmoModel>  AMMO_MAP = new HashMap<>();
    private final Map<String, GunModel> GUN_MAP = new HashMap<>();
    private final Map<String, Kit> KIT_MAP = new HashMap<>();
    private final Map<String, MagazineModel> MAGAZINE_MAP = new HashMap<>();
    private final Map<String, ScopeModel> SCOPE_MAP = new HashMap<>();
    private final Map<Class<? extends Handler>, Handler> HANDLERS = new HashMap<>();
    private final ServerData SERVER_DATA = new ServerData();
    private File localeDir;
    public ChatManager chatManager;
    public TitleManager titleProvider;
    public GameManager gameManager;
    public DataManager dataManager;
    public TaskHelper taskHelper;
    public ItemManager itemManager;
    public GuiManager guiManager;
    public ScoreboardRenderer scoreboardRenderer;
    public BossbarRenderer bossbarRenderer;
    public BungeeMessenger bungeeMessenger;
    public GameTask gameTask;
    public QueueTitleTask queueTitleTask;
    public QueueServerTask queueServerTask;
    private Expression toExpConverter;
    private Expression toLevelConverter;
    private PapiExpansion papiExpansion;
    private SimpleDateFormat longFormDate;
    private SimpleDateFormat shortFormDate1;
    private SimpleDateFormat shortFormDate2;
    private SimpleDateFormat shortFormDate3;
    private boolean syncDataTaskNeed;
    private File configFolder = getDataFolder();
    private String remoteConfigUrl;
    private boolean spigotBungeeSupport;
    private boolean supportBungee;
    private List<String> lobbyList;

    @Override
    public void onEnable() {
        try {
            Class<?> sc = Class.forName("org.spigotmc.SpigotConfig");
            spigotBungeeSupport = (boolean) ReflectionUtil.getStaticField(sc, "bungee");
        } catch (ClassNotFoundException e) {
            exit("ABM can only work on Spigot-based servers.");
            return;
        }
        if (!VaultApi.init()) exit("Failed to hook to Vault");
        getLogger().info("Consider to donate me if you think ABM is awesome <3");
        getDataFolder().mkdir();

        initConfigFiles();
        injectApiProvider();

        papiExpansion = new PapiExpansion(this);
        papiExpansion.register();
        taskHelper = new TaskHelper(this);
        chatManager = new ChatManager(this);
        titleProvider = new TitleManager(this);
        itemManager = new ItemManager(this);
        guiManager = new GuiManager(this);
        gameManager = new GameManager(this);
        HANDLERS.put(GunHandler.class, new GunHandler(this));

        initGeneral(CONFIG[1]);
        initLocale(CONFIG[2]);
        initMode(CONFIG[3]);
        initArena(CONFIG[4]);
        initAmmo(CONFIG[5]);
        initMagazine(CONFIG[6]);
        initScope(CONFIG[7]);
        initGun(CONFIG[8]);
        //initItem(CONFIG[9]);
        initGui(CONFIG[10]);
        initKits(CONFIG[11]);

        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        PlayerListener pl = new PlayerListener(this);
        getServer().getPluginManager().registerEvents(pl, this);
        getServer().getOnlinePlayers().forEach(pl::handleJoin);

        taskHelper.newAsyncTimerTask(scoreboardRenderer = new ScoreboardRenderer(), 0, SCOREBOARD_UPDATE_INTERVAL);
        taskHelper.newAsyncTimerTask(bossbarRenderer = new BossbarRenderer(), 0, BOSSBAR_UPDATE_INTERVAL);
        taskHelper.newAsyncTimerTask(new DataSavingTask(this), 0, 60);
        if(syncDataTaskNeed)
            taskHelper.newAsyncTimerTask(new DataLoadingTask(this), 0, 60);
        taskHelper.newAsyncTimerTask(queueTitleTask = new QueueTitleTask(), 0, 20);
        taskHelper.newTimerTask(gameTask = new GameTask(this), 0, 1);
        if(supportBungee){
            taskHelper.newAsyncTimerTask(queueServerTask = new QueueServerTask(this), 0, 20);
            getServer().getMessenger().registerIncomingPluginChannel(this, BungeeMessenger.BATTLE_CHANNEL, bungeeMessenger = new BungeeMessenger(this));
            getServer().getMessenger().registerOutgoingPluginChannel(this, BungeeMessenger.BATTLE_CHANNEL);
        }

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.enableUnstableAPI("help");
        manager.registerCommand(new BattleCommand(this));
        manager.getCommandCompletions().registerAsyncCompletion("ammo", new CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext>() {
            @Override
            public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
                return AMMO_MAP.keySet();
            }
        });
        manager.getCommandCompletions().registerAsyncCompletion("gun", new CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext>() {
            @Override
            public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
                return GUN_MAP.keySet();
            }
        });
        manager.getCommandCompletions().registerAsyncCompletion("magazine", new CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext>() {
            @Override
            public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
                return MAGAZINE_MAP.keySet();
            }
        });
        manager.getCommandCompletions().registerAsyncCompletion("scope", new CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext>() {
            @Override
            public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
                return SCOPE_MAP.keySet();
            }
        });
        manager.getCommandCompletions().registerAsyncCompletion("arena", new CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext>() {
            @Override
            public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
                return ARENA_MAP.keySet();
            }
        });
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
        gameManager.cleaner.destroy();
        gameManager.listGames(game -> {
            if(game instanceof LocalGame) {
                ((LocalGame) game).getPlayers().values().forEach(player -> {
                    if (player.getBackupInventory() != null)
                        player.getPlayer().getInventory().setContents(player.getBackupInventory());
                });
            }
        });
        dataManager.saveServerData();
        PLAYER_MAP.keySet().forEach(dataManager::savePlayerData);
        ServerUtil.getAllEntities(entity -> {
            if(entity instanceof HumanEntity) return;
            if(entity.hasMetadata("abm_temp_entity")) entity.remove();
        });
        dataManager.destroy();
    }

    public FileConfiguration getSystemConf(){
        return CONFIG[0];
    }

    public FileConfiguration getGeneralConf(){
        return CONFIG[1];
    }

    public FileConfiguration getLocaleConf(){
        return CONFIG[2];
    }

    public FileConfiguration getModeConf(){
        return CONFIG[3];
    }

    public FileConfiguration getArenaConf(){
        return CONFIG[4];
    }

    public FileConfiguration getAmmoConf(){
        return CONFIG[5];
    }

    public FileConfiguration getMagazineConf(){
        return CONFIG[6];
    }

    public FileConfiguration getScopeConf(){
        return CONFIG[7];
    }

    public FileConfiguration getGunConf(){
        return CONFIG[8];
    }

    public FileConfiguration getItemConf(){
        return CONFIG[9];
    }

    public FileConfiguration getGuiConf(){
        return CONFIG[10];
    }

    public FileConfiguration getKitConf(){
        return CONFIG[11];
    }

    private YamlConfiguration loadConfigFile(String fp, String cp){
        if(remoteConfigUrl != null){
            String url = String.format(remoteConfigUrl, fp);
            getLogger().info("Downloading config from "+url);
            HTTPConnectionHelper c = new HTTPConnectionHelper(url)
                    .setProperty("User-Agent", HTTPConnectionHelper.USER_AGENT_CHROME)
                    .connect();
            byte[] bytes = c.read();
            try {
                c.getInput().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Reader reader = new StringReader(new String(bytes, StandardCharsets.UTF_8));
            return YamlConfiguration.loadConfiguration(reader);
        }
        File f = new File(configFolder, fp);
        getLogger().info("Loading config file from "+f.getAbsolutePath());
        try {
            if(f.exists()) return YamlConfiguration.loadConfiguration(f);
            else if(f.createNewFile()) {
                getLogger().info("Creating new file at "+f.getAbsolutePath());
                InputStream in = getResource("config/"+cp);
                byte[] bytes = IOUtil.toByteArray(in, FileUtil.DEFAULT_BUFF_SIZE);
                in.close();
                FileUtil.write(f, bytes);
                Reader reader = new StringReader(new String(bytes, StandardCharsets.UTF_8));
                return YamlConfiguration.loadConfiguration(reader);
            } else exit("Failed to create file: "+f.getAbsolutePath());
        } catch (IOException e) {
            exit("Failed to load file: "+f.getAbsolutePath());
            e.printStackTrace();
        }
        return null;
    }

    private void initConfigFiles(){
        for(int i = 0; i < CONFIG_FILES.length; i++){
            String[] s = CONFIG_FILES[i].split(" ");
            String fp = s[0];
            String cp = s[0];
            if(s.length == 2) cp = s[1];
            CONFIG[i] = loadConfigFile(fp, cp);
            if(i == 0) initSystem(CONFIG[0]);
        }
    }

    private void initSystem(FileConfiguration c) {
        boolean remoteConfig = c.getBoolean("remote_config.enabled");
        if(remoteConfig){
            remoteConfigUrl = c.getString("remote_config.url");
            if(remoteConfigUrl == null) getLogger().warning("Remove config url is not defined");
            else return;
        }
        String cf = c.getString("config_folder");
        if(cf != null && !cf.isEmpty()){
            File file = new File(cf);
            if(file.exists()){
                if(file.isDirectory()) {
                    configFolder = file;
                    getLogger().info("Now using defined config folder: "+file.getAbsoluteFile());
                }
                else getLogger().warning("Config folder is not an directory");
            } else file.mkdir();
            new File(file, "locale").mkdir();
            new File(file, "items").mkdir();
        }
        // TODO check and upgrade db here
    }

    private void initGeneral(FileConfiguration c) {
        StorageType storageType = StorageType.valueOf(c.getString("storage.type").toUpperCase());
        dataManager = new DataManager(this, storageType);
        switch (storageType){
            case FILE: {
                File f = new File(c.getString("storage.file.data_path"));
                f.mkdir();
                dataManager.initFileStorage(f);
                break;
            }
            case MYSQL: {
                String host = c.getString("storage.mysql.hostname");
                int port = c.getInt("storage.mysql.port");
                String database = c.getString("storage.mysql.database");
                String username = c.getString("storage.mysql.username");
                String password = c.getString("storage.mysql.password");
                ConfigurationSection dsp = c.getConfigurationSection("storage.mysql.datasource_properties");
                String url = new StringBuilder("jdbc:mysql://").append(host).append(':').append(port).append('/').append(database).toString();
                dataManager.initMySQLStorage(url, username, password, dsp);
                syncDataTaskNeed = true;
                break;
            }
        }
        dataManager.loadServerData();

        toLevelConverter = new ExpressionBuilder(CONFIG[1].getString("level_system.exp_to_level_formula")).variables("x").build();
        toExpConverter = new ExpressionBuilder(CONFIG[1].getString("level_system.level_to_exp_formula")).variables("x").build();
        longFormDate = new SimpleDateFormat(getGeneralConf().getString("date_format.long_form"));
        shortFormDate1 = new SimpleDateFormat(getGeneralConf().getString("date_format.short_form.hours"));
        shortFormDate2 = new SimpleDateFormat(getGeneralConf().getString("date_format.short_form.minutes"));
        shortFormDate3 = new SimpleDateFormat(getGeneralConf().getString("date_format.short_form.seconds"));

        if(getGeneralConf().getBoolean("bungeecord.enabled")){
            if(spigotBungeeSupport) {
                supportBungee = true;
                lobbyList = Collections.unmodifiableList(getGeneralConf().getStringList("bungeecord.lobby_servers"));
            }
            else getLogger().warning("Looks like you have enabled Bungeecord support. But please also enable it in spigot.yml too. The option is now skipped for safe!");
        }
    }

    private void initLocale(FileConfiguration cache) {
        String path = getGeneralConf().getString("locale");
        YamlConfiguration local = loadConfigFile("locale/"+path, "locale/"+path);
        if(local != null) {
            Set<String> keys = cache.getKeys(true);
            for (String k : keys) {
                Object v;
                if (local.contains(k)) v = local.get(k);
                else {
                    getLogger().warning("The locale file is outdated. Missing path: " + k);
                    v = cache.get(k);
                }
                if (v instanceof String)
                    cache.set(k, ChatColor.translateAlternateColorCodes('&', (String) v));
                else cache.set(k, v);
            }
        } else getLogger().warning("Locale file not found.");

        ConfigurationSection itemTypeSec = cache.getConfigurationSection("item_type");
        if(itemTypeSec != null){
            for(ItemType t : ItemType.values()){
                String n = itemTypeSec.getString(t.name().toLowerCase());
                if(n != null) t.setLocalizedName(n);
            }
        }

        ABTeam.TEAM_A.setLocalizedName(cache.getString("ab_team.team_a"));
        ABTeam.TEAM_B.setLocalizedName(cache.getString("ab_team.team_b"));
    }

    private void initMode(FileConfiguration c) {
        c.getKeys(false).forEach(s -> {
            Mode.getMode(s, m -> m.init(c.getConfigurationSection(s)));
        });
    }

    private void initArena(FileConfiguration c) {
        c.getKeys(false).forEach(s -> ARENA_MAP.put(s, new Arena(s, c.getConfigurationSection(s))));
    }

    private void initAmmo(FileConfiguration c) {
        c.getKeys(false).forEach(s -> AMMO_MAP.put(s, new AmmoModel(s, c.getConfigurationSection(s))));
    }

    private void initMagazine(FileConfiguration c) {
        c.getKeys(false).forEach(s -> MAGAZINE_MAP.put(s, new MagazineModel(s, c.getConfigurationSection(s))));
    }

    private void initGun(FileConfiguration c) {
        c.getKeys(false).forEach(s -> GUN_MAP.put(s, new GunModel(s, c.getConfigurationSection(s))));
    }

    private void initScope(FileConfiguration c) {
        c.getKeys(false).forEach(s -> SCOPE_MAP.put(s, new ScopeModel(s, c.getConfigurationSection(s))));
    }

    private void initGui(FileConfiguration c) {
        guiManager.registerGuiHandler("core", new CoreHandler());
        guiManager.registerGuiHandler("inventory_menu", new MainInventoryHandler());
        guiManager.registerGuiHandler("inventory_gun", new GunInventory());
        guiManager.registerGuiHandler("inventory_magazine", new MagazineInventory());
        guiManager.registerGuiHandler("inventory_ammo", new AmmoInventory());
        guiManager.registerGuiHandler("inventory_scope", new ScopeInventory());
        guiManager.registerGuiHandler("kit_menu", new KitMenuHandler());
        guiManager.registerGuiHandler("arena_chooser", new ArenaChooserHandler());

        c.getKeys(false).forEach(s -> {
            if(s.length() > 0 && s.charAt(0) != '$')
                guiManager.registerGui(s, new Gui(c.getConfigurationSection(s)));
        });
    }

    private void initKits(FileConfiguration c) {
        c.getKeys(false).forEach(s -> KIT_MAP.put(s, new Kit(s, c.getConfigurationSection(s))));
    }

    public void resetScoreboard(Player player) {
        if(getGeneralConf().getBoolean("default_scoreboard.enabled")) {
            String title = getGeneralConf().getString("default_scoreboard.title");
            List<String> content = getGeneralConf().getStringList("default_scoreboard.content");
            int len = getGeneralConf().getInt("default_scoreboard.fixed_length");
            scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, title, content, len));
        } else scoreboardRenderer.removeScoreboard(player);
    }

    @Override
    @NotNull
    public Map<String, String> mapInfo(@NotNull InfoHolder holder){
        Condition.argNotNull("holder", holder);
        return holder.read().entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    InfoData data = entry.getValue();
                    if(data instanceof InfoBooleanData){
                        if(((InfoBooleanData) data).getValue())
                            return getLocaleConf().getString("state.enabled");
                        else
                            return getLocaleConf().getString("state.disabled");
                    }
                    else if(data instanceof InfoDoubleData)
                        return MathUtil.formatRound(((InfoDoubleData) data).getValue());
                    else if(data instanceof InfoIntData)
                        return Integer.toString(((InfoIntData) data).getValue());
                    else if(data instanceof InfoLongData)
                        return Long.toString(((InfoLongData) data).getValue());
                    else if(data instanceof InfoStringData)
                        return ((InfoStringData) data).getValue();
                    return "Error! (data class="+data.getClass().getSimpleName()+")";
                }
        ));
    }

    public <T extends Handler> T getHandler(Class<T> clazz){
        return (T) HANDLERS.get(clazz);
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
    public String formatShortForm(long time){
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
        return (float) getGeneralConf().getDouble("misc.default_speed.walk", 0.2);
    }

    @Override
    public float getDefaultFlyingSpeed() {
        return (float) getGeneralConf().getDouble("misc.default_speed.fly", 0.2);
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
        return PLAYER_MAP.get(player);
    }

    @NotNull
    @Override
    public ServerData getServerData() {
        return SERVER_DATA;
    }

    @Override
    public Arena getArena(@Nullable String id) {
        return ARENA_MAP.get(id);
    }

    @Override
    public Optional<AmmoModel> getAmmoModel(@Nullable String id) {
        return Optional.ofNullable(AMMO_MAP.get(id));
    }

    @Override
    public Optional<GunModel> getGunModel(@Nullable String id) {
        return Optional.ofNullable(GUN_MAP.get(id));
    }

    @Override
    public Optional<MagazineModel> getMagazineModel(@Nullable String id) {
        return Optional.ofNullable(MAGAZINE_MAP.get(id));
    }

    @Override
    public Optional<ScopeModel> getScopeModel(@Nullable String id) {
        return Optional.ofNullable(SCOPE_MAP.get(id));
    }

    @Override
    public Optional<Kit> getKit(@Nullable String id) {
        return Optional.ofNullable(KIT_MAP.get(id));
    }

    @NotNull
    @Override
    public List<Arena> listArenas() {
        return ImmutableList.copyOf(ARENA_MAP.values());
    }

    @Override
    public void listArenas(@NotNull Consumer<Arena> consumer) {
        Condition.argNotNull("consumer", consumer);
        ARENA_MAP.values().forEach(consumer);
    }

    @NotNull
    @Override
    public List<AmmoModel> listAmmoModels() {
        return ImmutableList.copyOf(AMMO_MAP.values());
    }

    @Override
    public void listAmmoModels(@NotNull Consumer<AmmoModel> consumer) {
        Condition.argNotNull("consumer", consumer);
        AMMO_MAP.values().forEach(consumer);
    }

    @NotNull
    @Override
    public List<GunModel> listGunModels() {
        return ImmutableList.copyOf(GUN_MAP.values());
    }

    @Override
    public void listGunModels(@NotNull Consumer<GunModel> consumer) {
        Condition.argNotNull("consumer", consumer);
        GUN_MAP.values().forEach(consumer);
    }

    @NotNull
    @Override
    public List<MagazineModel> listMagazineModels() {
        return ImmutableList.copyOf(MAGAZINE_MAP.values());
    }

    @Override
    public void listMagazineModels(@NotNull Consumer<MagazineModel> consumer) {
        Condition.argNotNull("consumer", consumer);
        MAGAZINE_MAP.values().forEach(consumer);
    }

    @Override
    public @NotNull List<ScopeModel> listScopes() {
        return ImmutableList.copyOf(SCOPE_MAP.values());
    }

    @Override
    public void listScopes(@NotNull Consumer<ScopeModel> consumer) {
        Condition.argNotNull("consumer", consumer);
        SCOPE_MAP.values().forEach(consumer);
    }

    @NotNull
    @Override
    public List<Kit> listKits() {
        return ImmutableList.copyOf(KIT_MAP.values());
    }

    @Override
    public void listKits(@NotNull Consumer<Kit> consumer) {
        Condition.argNotNull("consumer", consumer);
        KIT_MAP.values().forEach(consumer);
    }

    @Override
    public @NotNull BattleGameManager getGameManager() {
        return gameManager;
    }

    @Override
    public @NotNull BattleItemManager getItemManager() {
        return itemManager;
    }

    @Override
    public @NotNull BattleGuiManager getGuiManager() {
        return guiManager;
    }

    @Override
    public @NotNull BattleChatManager getChatManager() {
        return chatManager;
    }

    @Override
    public boolean hasBungeecordSupport() {
        return supportBungee;
    }

    @Override
    public List<String> getLobbyServers() {
        return lobbyList;
    }

    @Override
    public int getMaxReconnectionTries() {
        return getGeneralConf().getInt("bungeecord.reconnect_tries_per_server");
    }

    @Override
    public long getConnectionDelay() {
        return getGeneralConf().getLong("bungeecord.connection_delay");
    }

    private void exit(String msg) {
        getLogger().warning("Plugin is now shutting down...");
        getLogger().info("Reason: "+msg);
        getServer().getPluginManager().disablePlugin(this);
    }
}
