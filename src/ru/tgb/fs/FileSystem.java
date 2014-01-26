package ru.tgb.fs;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

public class FileSystem {
    private static FileSystem instance;
    private static File image = new File("c.tdi");
    private static int blockOffset = 4 * 1024 * 1024; // 4 MB for desc. table, resulting in 2^16 files total
    private Descriptor[] descriptorTable = new Descriptor[blockOffset / Descriptor.size];
    private Descriptor currentDir;
    private Queue<Integer> missingIds = new PriorityQueue<Integer>();
    private RandomAccessFile storage;

    public Descriptor createFile(String name, boolean isDir) throws IOException {
        if (name.length() > Descriptor.nameLength) throw new IOException("File name too long!");
        Descriptor d = Descriptor.fromName(name, isDir);
        d.setParent(currentDir.getId());
        if (!isDir) {
            d.setFirstBlock(findFreeBlock());
            writeBlock(Block.fromDescriptor(d), d.getFirstBlock());
        }
        descriptorTable[d.getId()] = d;
        storage.seek(d.getId() * Descriptor.size);
        storage.write(d.toBytes());
        return d;
    }

    public void deleteFile(Descriptor d) throws IOException {
        descriptorTable[d.getId()] = null;
        missingIds.add(d.getId());
        storage.seek(d.getId() * Descriptor.size);
        storage.write(new byte[Descriptor.size]);
    }

    public Block readBlock(int offset) throws IOException {
        byte[] result = new byte[Block.size];
        storage.seek(offset);
        storage.read(result);
        Block b = Block.fromBytes(result);
        b.offset = offset;
        return b;
    }

    public void writeBlock(Block block, int offset) throws IOException {
        storage.seek(offset);
        storage.write(block.toBytes());
    }

    public Descriptor[] getCurrentDirList() {
        List<Descriptor> descriptors = new ArrayList<Descriptor>();
        for (Descriptor d : descriptorTable) {
            if (d == null) continue;
            if (d.getParent() == currentDir.getId()) descriptors.add(d);
        }
        Descriptor[] result = new Descriptor[descriptors.size()];
        Arrays.sort(descriptors.toArray(result), new DescriptorComparator());
        return result;
    }

    public int findFreeBlock() throws IOException {
        int result = -1;
        int offset = blockOffset;
        do {
            Block b = readBlock(offset);
            if (b.getDescriptorId() == 0 || missingIds.contains(b.getDescriptorId())) result = offset;
            else offset += Block.size;
        } while (result == -1);
        return result;
    }

    public int nextId() {
        return missingIds.poll();
    }

    public synchronized static FileSystem getFS() throws IOException {
        if (instance == null) instance = new FileSystem();
        return instance;
    }

    private FileSystem() throws IOException {
        if (image.createNewFile()) {
            byte[] rootBytes = Descriptor.getRoot().toBytes();
            FileOutputStream fos = new FileOutputStream(image);
            fos.write(rootBytes);
            fos.write(new byte[blockOffset - Descriptor.size]);
            fos.close();
        }
        FileInputStream fis = new FileInputStream(image);
        byte[] descTableBytes = new byte[blockOffset];
        if (fis.read(descTableBytes) == -1) {
            System.err.println("Error: missing data in descriptors table!");
        }
        fis.close();
        ByteBuffer tableBuffer = ByteBuffer.wrap(descTableBytes);
        byte[] descBytes = new byte[Descriptor.size];
        for (int i = 0, l = blockOffset / Descriptor.size; i < l; i++) {
            tableBuffer.get(descBytes);
            Descriptor descriptor = Descriptor.fromBytes(descBytes);
            if (descriptor.getId() > Descriptor.ROOT_ID || i == 0) {
                descriptorTable[i] = descriptor;
            }
            else {
                missingIds.add(i);
            }
        }
        currentDir = descriptorTable[Descriptor.ROOT_ID];
        storage = new RandomAccessFile(image, "rw");
    }

    public synchronized void safeClose() {
        try {
            storage.close();
            instance = null;
        }
        catch (IOException e) {
            System.err.println("Error: unsuccessful image closing!");
        }
    }
}
