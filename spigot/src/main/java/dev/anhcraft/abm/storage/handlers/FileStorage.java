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
package dev.anhcraft.abm.storage.handlers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import dev.anhcraft.abm.api.storage.tags.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@SuppressWarnings("ALL")
public class FileStorage extends StorageProvider {
    private File file;

    public FileStorage(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    private DataTag readTag(int type, ByteArrayDataInput input) {
        switch (type){
            case DataTag.INT: return new IntTag(input.readInt());
            case DataTag.BOOL: return new BoolTag(input.readBoolean());
            case DataTag.DOUBLE: return new DoubleTag(input.readDouble());
            case DataTag.STRING: return new StringTag(input.readUTF());
            case DataTag.LONG: return new LongTag(input.readLong());
            case DataTag.FLOAT: return new FloatTag(input.readFloat());
            case DataTag.LIST: {
                int size = input.readInt();
                if(size > 0) {
                    int elemtype = input.readInt();
                    List<DataTag> e = new ArrayList<>();
                    for(int i = 0; i < size; i++) e.add(readTag(elemtype, input));
                    return new ListTag<>(e);
                }
                return new ListTag(new ArrayList<>());
            }
            default: throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    private void writeTag(int type, DataTag tag, ByteArrayDataOutput output) {
        switch (type){
            case DataTag.INT: {
                output.writeInt((Integer) tag.getValue());
                break;
            }
            case DataTag.BOOL: {
                output.writeBoolean((Boolean) tag.getValue());
                break;
            }
            case DataTag.DOUBLE: {
                output.writeDouble((Double) tag.getValue());
                break;
            }
            case DataTag.STRING: {
                output.writeUTF((String) tag.getValue());
                break;
            }
            case DataTag.LONG: {
                output.writeLong((Long) tag.getValue());
                break;
            }
            case DataTag.FLOAT: {
                output.writeFloat((Float) tag.getValue());
                break;
            }
            case DataTag.LIST: {
                List<DataTag> t = (List<DataTag>) tag.getValue();
                output.writeInt(t.size());
                if(!t.isEmpty()){
                    int typeId = t.get(0).getId();
                    output.writeInt(typeId);
                    t.forEach(e -> writeTag(typeId, e, output));
                }
                break;
            }
            default: throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    @Override
    public boolean load() {
        if(file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                byte[] bytes = ByteStreams.toByteArray(fis);
                fis.close();
                if(bytes.length > 0) {
                    getData().clear();

                    ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
                    int size = in.readInt();
                    for (int i = 0; i < size; i++) {
                        String name = in.readUTF();
                        int type = in.readInt();
                        getData().put(name, readTag(type, in));
                    }
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void save() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(getData().size());
        getData().forEach(new BiConsumer<String, DataTag>() {
            @Override
            public void accept(String s, DataTag dataTag) {
                out.writeUTF(s);
                int type = dataTag.getId();
                out.writeInt(type);
                writeTag(type, dataTag, out);
            }
        });
        try {
            file.createNewFile();
            Files.write(out.toByteArray(), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
