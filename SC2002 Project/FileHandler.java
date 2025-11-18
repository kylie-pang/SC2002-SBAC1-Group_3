import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    // ===================== LOW-LEVEL CSV READER =====================

    private static List<String[]> readCsv(String filePath) {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                // If your file has BOM, strip it (sometimes needed)
                if (firstLine && line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }

                // Skip header line
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                // Split by comma – adjust if your file uses ; instead
                String[] parts = line.split(",", -1); // -1 keeps empty columns
                rows.add(parts);
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + filePath);
            e.printStackTrace();
        }

        return rows;
    }

    // ===================== HIGH-LEVEL LOADERS =====================

    // Adjust the index mapping to match your actual columns

    public static List<Student> loadStudents(String filePath) {
        List<Student> students = new ArrayList<>();

        for (String[] row : readCsv(filePath)) {
            // Example assumption:
            // [0] studentID, [1] name, [2] major, [3] yearOfStudy, [4] email
            String id    = row[0].trim();
            String name  = row[1].trim();
            String major = row[2].trim();
            int year     = Integer.parseInt(row[3].trim());
            String email = row[4].trim();

            Student s = new Student(id, name, major, year, email);
            students.add(s);
        }

        return students;
    }

    public static List<CompanyRepresentative> loadCompanyReps(String filePath) {
        List<CompanyRepresentative> reps = new ArrayList<>();

        for (String[] row : readCsv(filePath)) {
            // Example assumption:
            // [0] repID, [1] name, [2] email, [3] companyName, [4] department, [5] position
            String id        = row[0].trim();
            String name      = row[1].trim();
            String email     = row[2].trim();
            String company   = row[3].trim();
            String dept      = row[4].trim();
            String position  = row[5].trim();

            CompanyRepresentative rep =
                    new CompanyRepresentative(id, name, email, company, dept, position);

            // If your CSV has an "approved" column, you can read it and set here
            // e.g. boolean approved = row[6].trim().equalsIgnoreCase("approved");

            reps.add(rep);
        }

        return reps;
    }

    public static List<CareerCenterStaff> loadStaff(String filePath) {
        List<CareerCenterStaff> staff = new ArrayList<>();

        for (String[] row : readCsv(filePath)) {
            // Example assumption:
            // [0] staffID, [1] name, [2] email
            String id    = row[0].trim();
            String name  = row[1].trim();
            String email = row[2].trim();
            String dept = row[3].trim();

            staff.add(new CareerCenterStaff(id, name, email, dept));
        }

        return staff;
    }

    // OPTIONAL: if you have a CSV of opportunities, you can add this later
    /*
    public static List<InternshipOpportunity> loadOpportunities(String filePath) {
        List<InternshipOpportunity> list = new ArrayList<>();
        for (String[] row : readCsv(filePath)) {
            // map columns to fields here
        }
        return list;
    }
    */
}

