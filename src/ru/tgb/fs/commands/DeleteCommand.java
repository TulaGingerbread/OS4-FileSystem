package ru.tgb.fs.commands;

import ru.tgb.fs.Descriptor;
import ru.tgb.fs.FileSystem;
import ru.tgb.fs.Locker;

import java.io.IOException;

public class DeleteCommand implements Command {

    @Override
    public void execute(FileSystem fs, String[] parts) throws IOException {
        if (parts.length > 2) {
            throw new IllegalArgumentException("too much arguments, use only file name");
        }
        String name = parts[1];
        Locker.lock(Thread.currentThread().getId());
        Descriptor[] list = fs.getCurrentDirList();
        boolean nonexistent = true;
        for (Descriptor d : list) {
            if (d.getName().trim().equals(name)) {
                fs.deleteFile(d);
                nonexistent = false;
                break;
            }
        }
        if (nonexistent) System.out.println("No such file!");
        Locker.unlock(Thread.currentThread().getId());
    }
}
