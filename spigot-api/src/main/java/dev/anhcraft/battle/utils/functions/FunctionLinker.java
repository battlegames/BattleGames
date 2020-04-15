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

import dev.anhcraft.jvmkit.utils.Condition;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class FunctionLinker<T> implements FunctionCaller<T> {
    private final Instruction instruction;
    private final Consumer<T> handler;

    public FunctionLinker(@NotNull Instruction instruction, @NotNull Consumer<T> handler) {
        Condition.argNotNull("function", instruction);
        Condition.argNotNull("handler", handler);
        this.instruction = instruction;
        this.handler = handler;
    }

    @NotNull
    public Instruction getInstruction() {
        return instruction;
    }

    @Override
    public void call(@NotNull T target) {
        Condition.argNotNull("target", target);
        handler.accept(target);
    }
}
