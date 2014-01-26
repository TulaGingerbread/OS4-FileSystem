package ru.tgb.fs.commands;

import ru.tgb.fs.Block;
import ru.tgb.fs.Descriptor;
import ru.tgb.fs.FileSystem;
import ru.tgb.fs.Locker;

import java.io.IOException;

public class TypeCommand implements Command {

    @Override
    public void execute(FileSystem fs, String[] parts) {
        if (parts.length > 2) {
            throw new IllegalArgumentException("too much arguments, use only file name");
        }
        String name = parts[1];
        boolean nonexistent = true;
        Locker.lock(Thread.currentThread().getId());
        Descriptor[] list = fs.getCurrentDirList();
        for (Descriptor d : list) {
            if (d.getName().trim().equals(name)) {
                if (d.isDirectory()) throw new IllegalArgumentException("can't be directory");
                try {
                    Block b = fs.readBlock(d.getFirstBlock());
                    do {
                        System.out.print(new String(b.getData()).trim());
                        b = b.nextBlock();
                    } while (b != null);
                    System.out.println();
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
