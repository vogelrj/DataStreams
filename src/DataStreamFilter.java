import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreamFilter {
    public static List<String> filterFile(Path filePath, String keyword) {
        try {
            System.out.println("Filtering file: " + filePath.toAbsolutePath());
            System.out.println("Search keyword: " + keyword);

            String lowerKeyword = keyword.toLowerCase();

            try (Stream<String> lines = Files.lines(filePath)) {
                return lines
                        .filter(line -> line.toLowerCase().contains(lowerKeyword))
                        .collect(Collectors.toList());
            }

        } catch (IOException e) {
            System.err.println("IOException in filterFile: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception ex) {
            System.err.println("Unexpected error in filterFile: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }
}
