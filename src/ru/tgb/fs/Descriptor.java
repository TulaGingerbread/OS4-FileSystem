package ru.tgb.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

public class Descriptor {
    public final static int size = 64;
    public final static int nameLength = size - 21;
    public static final int ROOT_ID = 0;
    public static final int NO_FIRST_BLOCK = 0;
    private byte[] name; // 43 B
    private int id; // 4B
    private int creationTime; // 4B
    private int changeTime; // 4B
    private byte permission; // 1B = ****drwx
    private int parent; // 4B
    private int firstBlock; // 4B

    private Descriptor() {}

    private Descriptor(byte[] raw) {
        ByteBuffer buffer = ByteBuffer.wrap(raw);
        name = new byte[nameLength];
        buffer.get(name);
        id = buffer.getInt();
        creationTime = buffer.getInt();
        changeTime = buffer.getInt();
        permission = buffer.get();
        parent = buffer.getInt();
        firstBlock = buffer.getInt();
    }

    public boolean isDirectory() {
        return (permission & 0x8) == 8;
    }

    public boolean canRead() {
        return (permission & 0x4) == 4;
    }

    public boolean canWrite() {
        return (permission & 0x2) == 2;
    }

    public boolean canExecute() {
        return (permission & 0x1) == 1;
    }

    public static Descriptor getRoot() {
        Descriptor root = new Descriptor();
        root.name = new byte[nameLength];
        System.arraycopy("".getBytes(), 0, root.name, 0, 0);
        root.id = ROOT_ID;
        root.creationTime = (int) (System.currentTimeMillis() / 1000L);
        root.changeTime = root.creationTime;
        root.permission = 0x0C; // +dr-wx
        root.parent = root.id;
        root.firstBlock = NO_FIRST_BLOCK;
        return root;
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.put(name);
        buffer.putInt(id);
        buffer.putInt(creationTime);
        buffer.putInt(changeTime);
        buffer.put(permission);
        buffer.putInt(parent);
        buffer.putInt(firstBlock);
        return buffer.array();
    }

    public static Descriptor fromBytes(byte[] raw) {
        return new Descriptor(raw);
    }

    public static Descriptor fromName(String name, boolean isDir) throws IOException {
        Descriptor result = new Descriptor();
        result.name = new byte[nameLength];
        System.arraycopy(name.getBytes(), 0, result.name, 0, name.length());
        result.id = FileSystem.getFS().nextId();
        result.creationTime = (int) (System.currentTimeMillis() / 1000L);
        result.changeTime = result.creationTime;
        result.permission = 0x06;
        if (isDir) result.permission |= 0x8;
        result.parent = ROOT_ID;
        result.firstBlock = NO_FIRST_BLOCK;
        return result;
    }

    public void setFirstBlock(int firstBlock) {
        this.firstBlock = firstBlock;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getParent() {
        return parent;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return new String(name);
    }

    public int getFirstBlock() {
        return firstBlock;
    }

    @Override
    public String toString() {
        byte[] nameBytes = new byte[20];
        System.arraycopy(name, 0, nameBytes, 0, 20);
        String result = isDirectory() ? "D" : "F";
        result += "|" + new String(nameBytes);
        result += "|" + (canRead() ? "+" : "-");
        result += (canWrite() ? "+" : "-");
        result += (canExecute() ? "+" : "-");
        result += "|" + new Date(creationTime * 1000L).toString();
        result += "|" + new Date(changeTime * 1000L).toString();
        return result;
    }
}
