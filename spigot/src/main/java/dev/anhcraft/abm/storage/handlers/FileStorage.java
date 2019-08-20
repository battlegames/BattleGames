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

    @Override
    protected DataTag readTag(int type, ByteArrayDataInput input) {
        switch (type){
            case 1: return new IntTag(input.readInt());
            case 2: return new BoolTag(input.readBoolean());
            case 3: return new DoubleTag(input.readDouble());
            case 4: return new StringTag(input.readUTF());
            case 5: return new LongTag(input.readLong());
            case 6: return new FloatTag(input.readFloat());
            case 7: {
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

    @Override
    protected void writeTag(int type, DataTag tag, ByteArrayDataOutput output) {
        switch (type){
            case 1: {
                output.writeInt((Integer) tag.getValue());
                break;
            }
            case 2: {
                output.writeBoolean((Boolean) tag.getValue());
                break;
            }
            case 3: {
                output.writeDouble((Double) tag.getValue());
                break;
            }
            case 4: {
                output.writeUTF((String) tag.getValue());
                break;
            }
            case 5: {
                output.writeLong((Long) tag.getValue());
                break;
            }
            case 6: {
                output.writeFloat((Float) tag.getValue());
                break;
            }
            case 7: {
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
