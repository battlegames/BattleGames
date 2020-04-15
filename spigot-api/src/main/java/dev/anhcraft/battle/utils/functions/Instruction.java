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

package dev.anhcraft.battle.utils.functions;

import com.google.common.base.Joiner;
import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class Instruction {
    private static final Pattern KEY_PATTERN = Pattern.compile("[a-zA-Z0-9_]+");
    private final String namespace;
    private final String target;
    private final String[] args;

    /**
     * Parses the given instruction in {@link String}.
     * @param str the instruction in 'string'
     * @return parsed instruction (or null if the given string was invalid)
     */
    @Nullable
    public static Instruction parse(@NotNull String str){
        Condition.argNotNull("str", str);
        String[] token1 = str.trim().split("::", 2);
        if(token1.length == 2 && !token1[1].isEmpty()){
            if(KEY_PATTERN.matcher(token1[0]).matches()){
                String[] token2 = token1[1].split("\\{", 2);
                if(token2.length == 2){
                    String param = token2[1];
                    if(!param.isEmpty() && param.charAt(param.length()-1) == '}'){
                        token2[0] = token2[0].trim();
                        if(KEY_PATTERN.matcher(token2[0]).matches()) {
                            String[] ag = param.substring(0, param.length()-1).split(";");
                            for(int i = 0; i < ag.length; i++){
                                ag[i] = ag[i].trim();
                            }
                            return new Instruction(token1[0], token2[0], ag);
                        }
                    }
                } else {
                    if(KEY_PATTERN.matcher(token2[0]).matches())
                        return new Instruction(token1[0], token2[0]);
                }
            }
        }
        return null;
    }

    public Instruction(@NotNull String namespace, @NotNull String target, @NotNull String... args) {
        Condition.argNotNull("namespace", namespace);
        Condition.argNotNull("target", target);
        Condition.argNotNull("args", args);
        this.namespace = namespace;
        this.target = target;
        this.args = args;
    }

    @NotNull
    public String getNamespace() {
        return namespace;
    }

    @NotNull
    public String getTarget() {
        return target;
    }

    @NotNull
    public String[] getArgs() {
        return args;
    }

    @NotNull
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append(namespace).append("::").append(target);
        if(args.length > 0){
            s.append(" { ");
            Joiner.on("; ").appendTo(s, args);
            s.append(" }");
        }
        return s.toString();
    }
}
