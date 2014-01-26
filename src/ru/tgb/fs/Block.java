package ru.tgb.fs;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Block {
    public static int size = 1024;
    public static int maxDataLength = size - 4 * 3;
    public int offset;
    private int descriptorId;
    private int dataLength;
    private int nextBlock;
    private byte[] data;

    private Block() {}

    private Block(byte[] raw) {
        ByteBuffer buffer = ByteBuffer.wrap(raw);
        descriptorId = buffer.getInt();
        dataLength = buffer.getInt();
        nextBlock = buffer.getInt();
        data = new byte[dataLength];
        buffer.get(data);
    }

    public Block nextBlock() throws IOException {
        return (nextBlock == Descriptor.NO_FIRST_BLOCK) ? null : FileSystem.getFS().readBlock(nextBlock);
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.putInt(descriptorId);
        buffer.putInt(dataLength);
        buffer.putInt(nextBlock);
        buffer.put(data);
        return buffer.array();
    }

    public static Block fromBytes(byte[] raw) {
        return new Block(raw);
    }

    public static Block fromDescriptor(Descriptor d) {
        Block result = new Block();
        result.descriptorId = d.getId();
        result.dataLength = 0;
        result.nextBlock = Descriptor.NO_FIRST_BLOCK;
        result.data = new byte[0];
        return result;
    }

    public int getDataLength() {
        return dataLength;
    }

    public byte[] getData() {
        return data;
    }

    public int getDescriptorId() {
        return descriptorId;
    }

    public void setNextBlock(int nextBlock) {
        this.nextBlock = nextBlock;
    }

    public void setData(byte[] data) {
        this.data = data;
        this.dataLength = data.length;
    }
}
