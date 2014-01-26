package ru.tgb.fs.commands;

import ru.tgb.fs.FileSystem;
import ru.tgb.fs.Locker;

import java.io.IOException;

public class TouchCommand implements Command {

    @Override
    public void execute(FileSystem fs, String[] parts) throws IOException {
        if (parts.length > 2) {
            throw new IllegalArgumentException("too much arguments, use only file name");
        }
        String name = parts[1];
        Locker.lock(Thread.currentThread().getId());
        fs.createFile(name, false);
        Locker.unlock(Thread.currentThread().getId());
    }
}
