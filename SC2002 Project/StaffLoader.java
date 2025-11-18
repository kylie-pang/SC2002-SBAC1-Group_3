import java.util.ArrayList;
import java.util.List;

public class StaffLoader {

    public static List<CareerCenterStaff> load(String path) {
        List<CareerCenterStaff> staffList = new ArrayList<>();

        for (String[] row : CsvReader.read(path)) {
            // CSV columns:
            // 0 = StaffID
            // 1 = StaffName
            // 2 = StaffEmail
            // 3 = StaffDepartment

            String id    = row[0].trim();
            String name  = row[1].trim();
            String email = row[2].trim();
            String dept  = row.length > 3 ? row[3].trim() : "Career Center";

            staffList.add(new CareerCenterStaff(id, name, email, dept));
        }

        return staffList;
    }
}
