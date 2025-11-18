import java.util.ArrayList;
import java.util.List;

public class CompanyRepLoader {

    public static List<CompanyRepresentative> load(String path) {
        List<CompanyRepresentative> reps = new ArrayList<>();

        for (String[] row : CsvReader.read(path)) {
            // CSV columns:
            // 0 = CompanyRepID
            // 1 = CompanyRepName
            // 2 = CompanyRepEmail
            // 3 = CompanyName

            String id        = row[0].trim();
            String name      = row[1].trim();
            String email     = row[2].trim();
            String company   = row[3].trim();

            // Defaults for missing fields
            String department = "General";
            String position   = "Representative";

            CompanyRepresentative rep = new CompanyRepresentative(
                    id, name, email, company, department, position
            );

            reps.add(rep);
        }

        return reps;
    }
}
