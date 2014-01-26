package ru.tgb.fs.commands;

import ru.tgb.fs.Block;
import ru.tgb.fs.Descriptor;
import ru.tgb.fs.FileSystem;
import ru.tgb.fs.Locker;

import java.io.IOException;
import java.nio.ByteBuffer;

public class WriteCommand implements Command {

    @Override
    public void execute(FileSystem fs, String[] parts) {
        if (parts.length < 3) {
            throw new IllegalArgumentException("use file name and text");
        }
        String name = parts[1];
        String text = parts[2];
        for (int i = 3; i < parts.length; i++) text += parts[i];
        byte[] newText = text.getBytes();
        boolean nonexistent = true;
        Locker.lock(Thread.currentThread().getId());
        Descriptor[] list = fs.getCurrentDirList();
        for (Descriptor d : list) {
            if (d.getName().trim().equals(name)) {
                if (d.isDirectory()) throw new IllegalArgumentException("can't be directory");
                try {
                    Block b = fs.readBlock(d.getFirstBlock());
                    while (b.nextBlock() != null) {
                        b = b.nextBlock();
                    }
                    int remaining = Block.maxDataLength - b.getDataLength();
                    byte[] data = b.getData();
                    int bufferLength = Math.min(Block.maxDataLength, data.length + newText.length);
                    ByteBuffer buffer = ByteBuffer.allocate(bufferLength);
                    buffer.put(data);
                    buffer.put(newText, 0, Math.min(remaining, newText.length));
                    b.setData(buffer.array());
                    fs.writeBlock(b, b.offset);
                    if (newText.length > remaining) {
                        b.setNextBlock(fs.findFreeBlock());
                        b = b.nextBlock();
                        byte[] toAdd = new byte[newText.length - remaining];
                        System.arraycopy(newText, remaining, toAdd, 0, toAdd.length);
                        buffer = ByteBuffer.allocate(toAdd.length);
                        buffer.put(toAdd);
                        b.setData(buffer.array());
                        fs.writeBlock(b, b.offset);
                    }
                }
                catch (IOException e) {
                    System.err.println("Error during reading file!");
                }
                nonexistent = false;
                break;
            }
        }
        Locker.unlock(Thread.currentThread().getId());
        if (nonexistent) System.out.println("No such file!");
    }
}
