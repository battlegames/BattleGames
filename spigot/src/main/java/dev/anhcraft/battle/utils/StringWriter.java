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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StringWriter {
    private final ByteArrayOutputStream stream = new ByteArrayOutputStream();

    public StringWriter append(String s) throws IOException {
        stream.write(s.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    public StringWriter append(char c) {
        stream.write(c);
        return this;
    }

    public StringWriter append(byte[] data) throws IOException {
        stream.write(data);
        return this;
    }

    public byte[] build() {
        return stream.toByteArray();
    }
}
