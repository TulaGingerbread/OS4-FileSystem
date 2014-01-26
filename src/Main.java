import ru.tgb.fs.CommandParser;
import ru.tgb.fs.FileSystem;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	    FileSystem fs = null;
        try {
            fs = FileSystem.getFS();
        }
        catch (IOException e) {
            System.err.println("Error during file system initialization!");
            System.exit(1);
        }
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains("exit")) break;
            CommandParser.execute(line);
        }
        fs.safeClose();
    }
}
