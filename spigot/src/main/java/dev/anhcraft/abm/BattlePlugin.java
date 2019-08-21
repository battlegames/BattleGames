package dev.anhcraft.abm;

import co.aikar.commands.PaperCommandManager;
import com.google.common.io.ByteStreams;
import dev.anhcraft.abm.api.*;
import dev.anhcraft.abm.api.game.Arena;
import dev.anhcraft.abm.api.game.Game;
import dev.anhcraft.abm.api.game.GamePlayer;
import dev.anhcraft.abm.api.inventory.items.AmmoModel;
import dev.anhcraft.abm.api.inventory.items.GunModel;
import dev.anhcraft.abm.api.inventory.items.ItemType;
import dev.anhcraft.abm.api.game.Mode;
import dev.anhcraft.abm.api.inventory.items.MagazineModel;
import dev.anhcraft.abm.api.misc.Kit;
import dev.anhcraft.abm.api.misc.info.*;
import dev.anhcraft.abm.api.storage.data.PlayerData;
import dev.anhcraft.abm.api.storage.data.ServerData;
import dev.anhcraft.abm.api.storage.StorageType;
import dev.anhcraft.abm.api.gui.Gui;
import dev.anhcraft.abm.cmd.BattleCommand;
import dev.anhcraft.abm.gui.*;
import dev.anhcraft.abm.api.inventory.items.ItemTag;
import dev.anhcraft.abm.system.handlers.GunHandler;
import dev.anhcraft.abm.system.handlers.Handler;
import dev.anhcraft.abm.system.integrations.PapiExpansion;
import dev.anhcraft.abm.system.integrations.VaultApi;
import dev.anhcraft.abm.system.listeners.BlockListener;
import dev.anhcraft.abm.system.listeners.GameListener;
import dev.anhcraft.abm.system.listeners.PlayerListener;
import dev.anhcraft.abm.system.managers.*;
import dev.anhcraft.abm.system.managers.ChatManager;
import dev.anhcraft.abm.system.managers.TitleManager;
import dev.anhcraft.abm.system.renderers.bossbar.BossbarRenderer;
import dev.anhcraft.abm.system.renderers.scoreboard.PlayerScoreboard;
import dev.anhcraft.abm.system.renderers.scoreboard.ScoreboardRenderer;
import dev.anhcraft.abm.tasks.DataSavingTask;
import dev.anhcraft.abm.tasks.GameTask;
import dev.anhcraft.abm.tasks.QueueTitleTask;
import dev.anhcraft.jvmkit.utils.FileUtil;
import dev.anhcraft.jvmkit.utils.MathUtil;
import net.md_5.bungee.api.ChatColor;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class BattlePlugin extends JavaPlugin implements BattleAPI {
    private static final String[] CONFIG_FILES = new String[]{
            "system.yml",
            "general.yml",
            "locale/en_us.yml", // PUT DEFAULT LOCALE HERE
            "modes.yml",
            "arenas.yml",
            "ammo.yml",
            "magazines.yml",
            "guns.yml",
            "items.yml",
            "gui.yml",
            "kits.yml"
    };
    private static final FileConfiguration[] CONFIG = new FileConfiguration[CONFIG_FILES.length];
    public final Map<OfflinePlayer, PlayerData> PLAYER_MAP = new HashMap<>();
    private final Map<String, Arena> ARENA_MAP = new HashMap<>();
    private final Map<String, AmmoModel>  AMMO_MAP = new HashMap<>();
    private final Map<String, GunModel> GUN_MAP = new HashMap<>();
    private final Map<String, Kit> KIT_MAP = new HashMap<>();
    private final Map<String, MagazineModel> MAGAZINE_MAP = new HashMap<>();
    private final Map<Class<? extends Handler>, Handler> HANDLERS = new HashMap<>();
    private final ServerData SERVER_DATA = new ServerData();
    private File localeDir;
    public ChatManager chatManager;
    public TitleManager titleProvider;
    public GameManager gameManager;
    public DataManager dataManager;
    public TaskManager taskManager;
    public ItemManager itemManager;
    public GuiManager guiManager;
    public ScoreboardRenderer scoreboardRenderer;
    public BossbarRenderer bossbarRenderer;
    public GameTask gameTask;
    public QueueTitleTask queueTitleTask;
    private Expression toExpConverter;
    private Expression toLevelConverter;
    private PapiExpansion papiExpansion;
    private SimpleDateFormat longFormDate;
    private SimpleDateFormat shortFormDate1;
    private SimpleDateFormat shortFormDate2;
    private SimpleDateFormat shortFormDate3;

    @Override
    public void onEnable() {
        try {
            Class.forName("org.spigotmc.SpigotConfig");
        } catch (ClassNotFoundException e) {
            exit("ABM can only work on Spigot-based servers.");
            return;
        }
        if (!VaultApi.init()) exit("Failed to hook to Vault");
        getLogger().info("Consider to donate me if you think ABM is awesome <3");
        localeDir = new File(getDataFolder(), "locale");
        localeDir.mkdirs();

        initConfigFiles();
        injectApiProvider();

        papiExpansion = new PapiExpansion(this);
        papiExpansion.register();
        taskManager = new TaskManager(this);
        chatManager = new ChatManager(this);
        titleProvider = new TitleManager(this);
        itemManager = new ItemManager(this);
        guiManager = new GuiManager(this);
        gameManager = new GameManager(this);
        ItemTag.init(this);
        HANDLERS.put(GunHandler.class, new GunHandler(this));

        initSystem(CONFIG[0]);
        initGeneral(CONFIG[1]);
        initLocale(CONFIG[2]);
        initMode(CONFIG[3]);
        initArena(CONFIG[4]);
        initAmmo(CONFIG[5]);
        initMagazine(CONFIG[6]);
        initGun(CONFIG[7]);
        //initItem(CONFIG[8]);
        initGui(CONFIG[9]);
        initKits(CONFIG[10]);

        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        PlayerListener pl = new PlayerListener(this);
        getServer().getPluginManager().registerEvents(pl, this);
        getServer().getOnlinePlayers().forEach(pl::handleJoin);

        taskManager.newAsyncTimerTask(scoreboardRenderer = new ScoreboardRenderer(), 0, 10);
        taskManager.newAsyncTimerTask(bossbarRenderer = new BossbarRenderer(), 0, 10);
        taskManager.newAsyncTimerTask(new DataSavingTask(this), 0, 60);
        taskManager.newAsyncTimerTask(queueTitleTask = new QueueTitleTask(), 0, 20);
        taskManager.newTimerTask(gameTask = new GameTask(this), 0, 1);

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new BattleCommand(this));
    }

    private void injectApiProvider() {
        APIProvider ap = new APIProvider();
        ap.set(this);
        try {
            Class<?> clazz = APIProvider.class;
            Field pf = clazz.getDeclaredField("provider");
            pf.setAccessible(true);
            pf.set(null, ap);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable(){
        gameManager.getGames().forEach(new Consumer<Game>() {
            @Override
            public void accept(Game game) {
                game.getPlayers().values().forEach(new Consumer<GamePlayer>() {
                    @Override
                    public void accept(GamePlayer player) {
                        if(player.getBackupInventory() != null)
                            player.getPlayer().getInventory().setContents(player.getBackupInventory());
                    }
                });
            }
        });
        dataManager.saveServerData();
        PLAYER_MAP.keySet().forEach(dataManager::savePlayerData);
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

    public FileConfiguration getGunConf(){
        return CONFIG[7];
    }

    public FileConfiguration getItemConf(){
        return CONFIG[8];
    }

    public FileConfiguration getGuiConf(){
        return CONFIG[9];
    }

    public FileConfiguration getKitConf(){
        return CONFIG[10];
    }

    private void initConfigFiles(){
        for(int i = 0; i < CONFIG_FILES.length; i++){
            String s = CONFIG_FILES[i];
            File f = new File(getDataFolder(), s);
            try {
                if(f.exists()) CONFIG[i] = YamlConfiguration.loadConfiguration(f);
                else if(f.createNewFile()) {
                    InputStream in = getResource("config/"+s);
                    byte[] bytes = ByteStreams.toByteArray(in);
                    in.close();
                    FileUtil.write(f, bytes);
                    Reader reader = new StringReader(new String(bytes, StandardCharsets.UTF_8));
                    CONFIG[i] = YamlConfiguration.loadConfiguration(reader);
                }
            } catch (IOException e) {
                exit("Failed to load file: "+f.getPath());
                e.printStackTrace();
            }
        }
    }

    private void initSystem(FileConfiguration c) {
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
        }
        dataManager.loadServerData();

        toLevelConverter = new ExpressionBuilder(CONFIG[1].getString("level_system.exp_to_level_formula")).variables("x").build();
        toExpConverter = new ExpressionBuilder(CONFIG[1].getString("level_system.level_to_exp_formula")).variables("x").build();
        longFormDate = new SimpleDateFormat(getGeneralConf().getString("date_format.long_form"));
        shortFormDate1 = new SimpleDateFormat(getGeneralConf().getString("date_format.short_form.hours"));
        shortFormDate2 = new SimpleDateFormat(getGeneralConf().getString("date_format.short_form.minutes"));
        shortFormDate3 = new SimpleDateFormat(getGeneralConf().getString("date_format.short_form.seconds"));
    }

    private void initLocale(FileConfiguration cache) {
        File f = new File(localeDir, getGeneralConf().getString("locale"));
        if(f.exists()){
            YamlConfiguration local = YamlConfiguration.loadConfiguration(f);
            Set<String> keys = cache.getKeys(true);
            for(String k : keys){
                Object v;
                if(local.contains(k)) v = local.get(k);
                else {
                    getLogger().warning("You locale file doesn't have path "+k);
                    v = cache.get(k);
                }
                if(v instanceof String)
                    cache.set(k, ChatColor.translateAlternateColorCodes('&', (String) v));
                else cache.set(k, v);
            }
        } else getLogger().warning("Your locale file didn't exist");

        ConfigurationSection itemTypeSec = cache.getConfigurationSection("item_type");
        if(itemTypeSec != null){
            for(ItemType t : ItemType.values()){
                String n = itemTypeSec.getString(t.name().toLowerCase());
                if(n != null) t.setLocalizedName(n);
            }
        }
    }

    private void initMode(FileConfiguration c) {
        c.getKeys(false).forEach(s -> Mode.valueOf(s.toUpperCase()).init(c.getConfigurationSection(s)));
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

    private void initGui(FileConfiguration c) {
        guiManager.registerGuiHandler("core", new CoreHandler());
        guiManager.registerGuiHandler("inventory_menu", new MainInventoryHandler());
        guiManager.registerGuiHandler("inventory_gun", new GunInventory());
        guiManager.registerGuiHandler("inventory_magazine", new MagazineInventory());
        guiManager.registerGuiHandler("inventory_ammo", new AmmoInventory());
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
            scoreboardRenderer.setScoreboard(new PlayerScoreboard(player, title, content));
        } else scoreboardRenderer.removeScoreboard(player);
    }

    public Map<String, String> mapInfo(InfoHolder holder){
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

    @Override
    public String formatLongFormDate(Date date){
        return longFormDate.format(date);
    }

    @Override
    public String formatShortForm(long time){
        final long x = 1000;
        if(time < 60 * x) return formatShortFormDateSeconds(new Date(time));
        else if(time < 60 * 60 * x) return formatShortFormDateMinutes(new Date(time));
        else return formatShortFormDateHours(new Date(time));
    }

    @Override
    public String formatShortFormDateHours(Date date){
        return shortFormDate1.format(date);
    }

    @Override
    public String formatShortFormDateMinutes(Date date){
        return shortFormDate2.format(date);
    }

    @Override
    public String formatShortFormDateSeconds(Date date){
        return shortFormDate3.format(date);
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
    public Optional<PlayerData> getPlayerData(@Nullable OfflinePlayer player) {
        return Optional.ofNullable(PLAYER_MAP.get(player));
    }

    @Override
    public ServerData getServerData() {
        return SERVER_DATA;
    }

    @Override
    public Optional<Arena> getArena(@Nullable String id) {
        return Optional.ofNullable(ARENA_MAP.get(id));
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
    public Optional<Kit> getKit(@Nullable String id) {
        return Optional.ofNullable(KIT_MAP.get(id));
    }

    @Override
    public List<Arena> listArenas() {
        return new ArrayList<>(ARENA_MAP.values());
    }

    @Override
    public List<AmmoModel> listAmmoModels() {
        return new ArrayList<>(AMMO_MAP.values());
    }

    @Override
    public List<GunModel> listGunModels() {
        return new ArrayList<>(GUN_MAP.values());
    }

    @Override
    public List<MagazineModel> listMagazineModels() {
        return new ArrayList<>(MAGAZINE_MAP.values());
    }

    @Override
    public List<Kit> listKits() {
        return new ArrayList<>(KIT_MAP.values());
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

    private void exit(String msg) {
        getLogger().warning("Plugin is now shutting down...");
        getLogger().info("Reason: "+msg);
        getServer().getPluginManager().disablePlugin(this);
    }
}
