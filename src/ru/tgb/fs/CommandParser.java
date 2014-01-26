package ru.tgb.fs;

import ru.tgb.fs.commands.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CommandParser {
    public static Map<String, Class> commands;

    static {
        commands = new HashMap<String, Class>();
        commands.put("touch", TouchCommand.class);
        commands.put("mkdir", MakeDirCommand.class);
        commands.put("del", DeleteCommand.class);
        commands.put("rm", DeleteCommand.class);
        commands.put("dir", ListDirCommand.class);
        commands.put("ls", ListDirCommand.class);
        commands.put("cd", ChangeDirCommand.class);
        commands.put("type", TypeCommand.class);
        commands.put("cat", TypeCommand.class);
        commands.put("write", WriteCommand.class);
        commands.put("test", TestCommand.class);
    }

    public static void execute(String line) {
        String[] parts = line.split(" ");
        try {
            ((Command) commands.get(parts[0]).newInstance()).execute(FileSystem.getFS(), parts);
        } catch (IllegalArgumentException e) {
            System.out.println("Wrong command usage: " + e.getMessage());
        } catch (InstantiationException e) {
            System.out.println("No such command!");
        } catch (IllegalAccessException e) {
            System.out.println("No such command!");
        } catch (IOException e) {
            System.err.println("I/O error during command execution!");
        }
    }
}
