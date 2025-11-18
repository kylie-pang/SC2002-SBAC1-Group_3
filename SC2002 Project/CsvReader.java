
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    public static List<String[]> read(String filePath) {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {

                // Remove BOM if present
                if (firstLine && line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }

                // Skip header row
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",", -1); // keep empty columns
                rows.add(parts);
            }

        } catch (IOException e) {
            System.out.println("Error reading: " + filePath);
            e.printStackTrace();
        }

        return rows;
    }
}

