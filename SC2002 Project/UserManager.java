import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private final List<Student> students = new ArrayList<>();
    private final List<CompanyRepresentative> representatives = new ArrayList<>();
    private final List<CareerCenterStaff> staffMembers = new ArrayList<>();

    // ========== ADD USERS ==========

    public void addStudent(Student s) {
        students.add(s);
    }

    public void addRepresentative(CompanyRepresentative rep) {
        representatives.add(rep);
    }

    public void addStaff(CareerCenterStaff staff) {
        staffMembers.add(staff);
    }

    // ========== FIND USER BY ID (used for login) ==========

    public User findUserByID(String id) {
        for (Student s : students) {
            if (s.getUserID().equalsIgnoreCase(id)) {
                return s;
            }
        }

        for (CompanyRepresentative rep : representatives) {
            if (rep.getUserID().equalsIgnoreCase(id)) {
                return rep;
            }
        }

        for (CareerCenterStaff staff : staffMembers) {
            if (staff.getUserID().equalsIgnoreCase(id)) {
                return staff;
            }
        }

        return null; // not found
    }

    // ========== GET LISTS (used by staff menu) ==========

    public List<Student> getAllStudents() {
        return students;
    }

    public List<CompanyRepresentative> getAllRepresentatives() {
        return representatives;
    }

    public List<CareerCenterStaff> getAllStaff() {
        return staffMembers;
    }
}