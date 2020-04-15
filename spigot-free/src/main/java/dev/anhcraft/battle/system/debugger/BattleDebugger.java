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

package dev.anhcraft.battle.system.debugger;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.util.concurrent.AtomicDouble;
import dev.anhcraft.battle.BattlePlugin;
import dev.anhcraft.battle.api.BattleApi;
import dev.anhcraft.battle.utils.StringWriter;
import dev.anhcraft.craftkit.utils.ServerUtil;
import dev.anhcraft.jvmkit.utils.PresentPair;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BattleDebugger {
    private static BattleDebugger activeDebugger;
    private static final Object SYNC_LOCK = new Object();
    private static final int MAX_RECORDS = 50;
    private static final int MAX_STACK_TRACES = 200;
    private static final int TPS_DELAY_TICKS = 60;
    private static final long STACK_DELAY_MS = 1500;

    public static synchronized boolean create(Consumer<String> callback, long time){
        if(activeDebugger == null){
            activeDebugger = new BattleDebugger(callback, time);
            return true;
        }
        return false;
    }

    public static void startTiming(String id){
        if(activeDebugger != null){
            synchronized (SYNC_LOCK) {
                List<TimingStack> x = activeDebugger.timings.get(id);
                long current = System.currentTimeMillis();
                if(x.isEmpty()){
                    x.add(0, new TimingStack(current));
                } else {
                    TimingStack ts = x.get(0);
                    if (ts.inProgress()) {
                        throw new IllegalStateException("Another timing record is in progress #" + id);
                    } else if((current - ts.getEnd()) > STACK_DELAY_MS) {
                        x.add(0, new TimingStack(current));
                    }
                }
            }
        }
    }

    public static void endTiming(String id){
        if(activeDebugger != null){
            synchronized (SYNC_LOCK) {
                List<TimingStack> x = activeDebugger.timings.get(id);
                if (x.isEmpty()) {
                    throw new IllegalStateException("No timing record found for #" + id);
                } else {
                    TimingStack ts = x.get(0);
                    if (ts.inProgress()) {
                        ts.setEnd(System.currentTimeMillis());
                    }
                    /* Ignore this code pls! (As records can be ignored, the old ones may trigger this exception)
                    else {
                        throw new IllegalStateException("Timing stack has finished: " + id);
                    }*/
                }
            }
        }
    }

    public static void reportTps(){
        if(activeDebugger != null){
            if(activeDebugger.tpsTickDelay == TPS_DELAY_TICKS) {
                synchronized (SYNC_LOCK) {
                    activeDebugger.tps.add(new PresentPair<>(System.currentTimeMillis(), ServerUtil.getTPS()[0]));
                }
                activeDebugger.tpsTickDelay = 0;
            } else activeDebugger.tpsTickDelay++;
        }
    }

    private final ListMultimap<String, TimingStack> timings = MultimapBuilder.hashKeys().linkedListValues().build();
    private final List<PresentPair<Long, Double>> tps = new ArrayList<>();
    private int tpsTickDelay;

    private BattleDebugger(Consumer<String> callback, long time){
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    callback.accept(end());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLater((Plugin) BattleApi.getInstance(), time);
    }

    private String formatTime(long date){
        return BattleApi.getInstance().formatLongFormDate(new Date(date)) + " ("+date+")";
    }

    public String end() throws IOException {
        activeDebugger = null;
        synchronized (SYNC_LOCK) {
            BattlePlugin bp = (BattlePlugin) BattleApi.getInstance();
            StringWriter w = new StringWriter();
            w.append("I. Basic info\n")
                    .append("- Plugin version: ").append(bp.getDescription().getVersion()).append('\n')
                    .append("- License type: ").append(bp.premiumConnector.isSuccess() ? "premium" : "free").append('\n')
                    .append("- Server version: ").append(Bukkit.getBukkitVersion()).append('\n')
                    .append("- Java version: ").append(System.getProperty("java.version")).append('\n')
                    .append("- OS: ").append(System.getProperty("os.name")).append("\n\n");
            AtomicDouble avgTps = new AtomicDouble();
            w.append("II. Timings\n")
                    .append("- Tps\n")
                    .append(tps.stream().map(s -> {
                        avgTps.addAndGet(s.getSecond());
                        return "  + At " + formatTime(s.getFirst()) + ": " + s.getSecond();
                    }).collect(Collectors.joining("\n"))).append('\n');
            w.append("  (avg = ").append(Double.toString(avgTps.get() / tps.size())).append(")\n");
            for (String s : timings.keySet()) {
                AtomicDouble ms = new AtomicDouble();
                List<TimingStack> stacks = timings.get(s);
                String r = stacks.stream().limit(MAX_RECORDS).map(v -> {
                    ms.addAndGet(v.delta());
                    return "  + From " + formatTime(v.getStart()) + " to " + formatTime(v.getEnd()) + ": " + v.delta() + " ms (" + v.delta(TimeUnit.SECONDS) + " s)";
                }).collect(Collectors.joining("\n"));
                w.append("- #").append(s).append(":").append('\n').append(r).append('\n');
                if(stacks.size() > MAX_RECORDS){
                    w.append("  + ...[").append(String.valueOf(stacks.size() - MAX_RECORDS)).append(" records hided]\n");
                }
                w.append("  (avg = ").append(Double.toString(ms.get()/stacks.size())).append(" ms)\n");
            }
            w.append("III. Threads & Stack traces\n");
            for(Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()){
                Thread k = entry.getKey();
                w.append("  - #").append(String.valueOf(k.getId())).append(' ').append(k.getName()).append(" | Status: ").append(k.getState().name()).append('\n');
                if(entry.getValue().length > 0) {
                    w.append(Arrays.stream(entry.getValue()).limit(MAX_STACK_TRACES).map(s -> "    " + s.toString()).collect(Collectors.joining("\n"))).append('\n');
                    if (entry.getValue().length > MAX_STACK_TRACES) {
                        w.append("    ...[").append(String.valueOf(entry.getValue().length - MAX_STACK_TRACES)).append(" elements hided]\n");
                    }
                }
            }
            File folder = new File(bp.configFolder, "debug");
            folder.mkdir();
            File f = new File(folder, System.currentTimeMillis() + ".zip");
            f.createNewFile();
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
            out.putNextEntry(new ZipEntry("info.txt"));
            out.write(w.build());
            out.closeEntry();
            for (int i = 0; i < BattlePlugin.CONFIG_FILES.length; i++) {
                String[] s = BattlePlugin.CONFIG_FILES[i].split(" ");
                out.putNextEntry(new ZipEntry("config/"+(s.length == 2 ? s[1] : s[0])));
                out.write(bp.CONFIG[i].saveToString().getBytes(StandardCharsets.UTF_8));
                out.closeEntry();
            }
            out.close();
            timings.clear();
            tps.clear();
            return f.getAbsolutePath();
        }
    }
}
