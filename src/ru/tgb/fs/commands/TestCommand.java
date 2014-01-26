package ru.tgb.fs.commands;

import ru.tgb.fs.CommandParser;
import ru.tgb.fs.FileSystem;
import ru.tgb.fs.Locker;

import java.io.IOException;

public class TestCommand implements Command {

    @Override
    public void execute(FileSystem fs, String[] parts) {
        CommandParser.execute("rm test.txt");
        CommandParser.execute("touch test.txt");
        for (int i = 0; i < 10; i++) {
            new Thread(new TestThread()).start();
        }
    }

    class TestThread implements Runnable {
        @Override
        public void run() {
            CommandParser.execute("write test.txt 1234-");
            CommandParser.execute("write test.txt 5678+");
            CommandParser.execute("type test.txt");
            CommandParser.execute("write test.txt 90");
            CommandParser.execute("type test.txt");
        }
    }
}
