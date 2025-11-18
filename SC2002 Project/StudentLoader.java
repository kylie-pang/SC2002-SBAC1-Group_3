import java.util.ArrayList;
import java.util.List;

public class StudentLoader {

    public static List<Student> load(String path) {
        List<Student> students = new ArrayList<>();

        for (String[] row : CsvReader.read(path)) {
            // CSV columns:
            // 0 = StudentID
            // 1 = StudentName
            // 2 = Major
            // 3 = Year
            // 4 = Email

            String id    = row[0].trim();
            String name  = row[1].trim();
            String major = row[2].trim();
            int year     = Integer.parseInt(row[3].trim());
            String email = row[4].trim();

            students.add(new Student(id, name, major, year, email));
        }

        return students;
    }
}
