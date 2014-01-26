package ru.tgb.fs.commands;

import ru.tgb.fs.FileSystem;

import java.io.IOException;

public interface Command {

    public void execute(FileSystem fs, String[] parts) throws IOException;
}
