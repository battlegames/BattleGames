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

package dev.anhcraft.battle.storage.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zaxxer.hikari.HikariDataSource;
import dev.anhcraft.battle.api.storage.tags.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MySQLStorage extends StorageProvider {
    private static final Gson GSON = new Gson();
    private final Object SAFE_LOCK = new Object();
    private final HikariDataSource dataSource;
    private long localSyncTime;
    private long remoteSyncTime;
    private final String tablePre;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean needSync(Connection conn){
        return localSyncTime < getLastSync(conn);
    }

    private long getLastSync(Connection conn){
        try {
            PreparedStatement s = conn.prepareStatement("SELECT `value` FROM `" + tablePre + "var` WHERE `name` = ?");
            s.setString(1, "lastSync");
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                String v = rs.getString("value");
                remoteSyncTime = Long.parseLong(v);
            }
            rs.close();
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return remoteSyncTime;
    }

    public void updateLastSync(Connection conn){
        try {
            long now = System.currentTimeMillis();
            PreparedStatement s = conn.prepareStatement("INSERT INTO `" + tablePre + "var` (`name`, `value`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `value` = ?;");
            s.setString(1, "lastSync");
            s.setLong(2, now);
            s.setLong(3, now);
            if (s.executeUpdate() > 0) {
                remoteSyncTime = now;
                localSyncTime = now;
            }
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MySQLStorage(HikariDataSource dataSource, String tablePre){
        this.dataSource = dataSource;
        this.tablePre = tablePre;

        try {
            Connection conn = dataSource.getConnection();
            Statement s = conn.createStatement();
            if(s.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS `"+tablePre+"data` (" +
                            "  `name` varchar(25) COLLATE utf8_unicode_ci NOT NULL," +
                            "  `value` mediumtext COLLATE utf8_unicode_ci NOT NULL," +
                            "  `type` tinyint(4) NOT NULL" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;"
            ) > 0){
                s.executeUpdate(
                        "ALTER TABLE `"+tablePre+"data` ADD UNIQUE KEY `name` (`name`);"
                );
            }
            if(s.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS `"+tablePre+"var` (" +
                            "  `name` varchar(15) COLLATE utf8_unicode_ci NOT NULL," +
                            "  `value` text COLLATE utf8_unicode_ci NOT NULL" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;"
            ) > 0){
                s.executeUpdate(
                        "ALTER TABLE `"+tablePre+"var` ADD UNIQUE KEY `name` (`name`);"
                );
            }
            s.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DataTag<?> parse(String value, int type){
        switch (type){
            case DataTag.INT: return new IntTag(Integer.parseInt(value));
            case DataTag.BOOL: return new BoolTag(Boolean.parseBoolean(value));
            case DataTag.DOUBLE: return new DoubleTag(Double.parseDouble(value));
            case DataTag.STRING: return new StringTag(value);
            case DataTag.LONG: return new LongTag(Long.parseLong(value));
            case DataTag.FLOAT: return new FloatTag(Float.parseFloat(value));
            case DataTag.LIST: {
                List<DataTag<?>> list = new ArrayList<>();
                JsonObject jo = GSON.fromJson(value, JsonObject.class);
                if(jo.has("data")) {
                    JsonArray array = jo.getAsJsonArray("data");
                    if (array.size() > 0) {
                        int s = jo.getAsJsonPrimitive("type").getAsInt();
                        for (JsonElement e : array) list.add(parse(e.getAsString(), s));
                    }
                }
                return new ListTag<>(list);
            }
        }
        return null;
    }

    private String toStr(DataTag<?> tag){
        if(tag.getId() == DataTag.LIST){
            ListTag<DataTag<?>> listTag = (ListTag<DataTag<?>>) tag;
            JsonObject obj = new JsonObject();
            if(!listTag.getValue().isEmpty()) {
                obj.addProperty("type", listTag.getValue().get(0).getId());
                JsonArray array = new JsonArray();
                listTag.getValue().forEach(dataTag -> array.add(toStr(dataTag)));
                obj.add("data", array);
            }
            return GSON.toJson(obj, JsonObject.class);
        } else return String.valueOf(tag.getValue());
    }

    @Override
    public boolean load() {
        synchronized (SAFE_LOCK) {
            try {
                Connection conn = dataSource.getConnection();
                if (!needSync(conn)) return true; // it doesn't mean the loading is fail
                Statement s = conn.createStatement();
                ResultSet rs = s.executeQuery("SELECT * FROM `" + tablePre + "data`;");
                while (rs.next()) {
                    String name = rs.getString("name");
                    String value = rs.getString("value");
                    int type = rs.getInt("type");
                    getData().fastPut(name, parse(value, type));
                }
                rs.close();
                s.close();
                conn.close();
                localSyncTime = remoteSyncTime;
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public boolean save() {
        synchronized (SAFE_LOCK) {
            try {
                Connection conn = dataSource.getConnection();
                if (needSync(conn)) return true; // it doesn't mean the saving is fail
                boolean f = false;
                Set<Map.Entry<String, DataTag<?>>> entries = getData().entrySet();
                for (Map.Entry<String, DataTag<?>> entry : entries){
                    String name = entry.getKey();
                    DataTag<?> tag = entry.getValue();
                    try {
                        String val = toStr(tag);
                        PreparedStatement s = conn.prepareStatement("INSERT INTO `" + tablePre + "data` (`name`, `value`, `type`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `value` = ?;");
                        s.setString(1, name);
                        s.setString(2, val);
                        s.setInt(3, tag.getId());
                        s.setString(4, val);
                        if (s.executeUpdate() > 0) f = true;
                        s.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                if(f) updateLastSync(conn);
                conn.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void destroy(){
        dataSource.close();
    }
}
