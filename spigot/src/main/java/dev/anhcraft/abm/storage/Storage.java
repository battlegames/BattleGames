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
package dev.anhcraft.abm.storage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import dev.anhcraft.abm.api.storage.data.DataMap;
import dev.anhcraft.abm.storage.handlers.StorageProvider;
import dev.anhcraft.abm.api.storage.tags.DataTag;

public class Storage extends StorageProvider {
    private StorageProvider handler;

    public Storage(StorageProvider handler) {
        this.handler = handler;
    }

    @Override
    protected DataTag readTag(int type, ByteArrayDataInput input) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeTag(int type, DataTag tag, ByteArrayDataOutput output) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean load() {
        return handler.load();
    }

    @Override
    public void save() {
        handler.save();
    }

    @Override
    public DataMap<String> getData() {
        return handler.getData();
    }
}
