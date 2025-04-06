import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreamLoader {
    public static List<String> loadFile(Path filePath) {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error loading file: " + e.getMessage());
            return null;
        }
    }
}
