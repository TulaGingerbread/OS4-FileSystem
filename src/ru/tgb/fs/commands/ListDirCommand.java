package ru.tgb.fs.commands;

import ru.tgb.fs.Descriptor;
import ru.tgb.fs.FileSystem;
import ru.tgb.fs.Locker;

import java.io.IOException;

public class ListDirCommand implements Command {

    @Override
    public void execute(FileSystem fs, String[] parts) throws IOException {
        if (parts.length > 1) {
            throw new IllegalArgumentException("no args allowed here");
        }
        Locker.lock(Thread.currentThread().getId());
        Descriptor[] list = fs.getCurrentDirList();
        System.out.println("T|Name             |RWX|Time created                 |Time changed");
        for (Descriptor d : list) System.out.println(d);
        Locker.unlock(Thread.currentThread().getId());
    }
}
