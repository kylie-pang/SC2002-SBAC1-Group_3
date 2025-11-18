import java.util.List;
import java.util.Scanner;

public class SystemController {

    private final UserManager userManager = new UserManager();
    private final OpportunityManager opportunityManager = new OpportunityManager();
    private final ApplicationManager applicationManager = new ApplicationManager();
    private final Scanner scanner = new Scanner(System.in);

    // Role-specific controllers
    private final StudentController studentController;
    private final StaffController staffController;
    private final CompanyRepController companyRepController;

    // ================== CONSTRUCTOR ==================
    public SystemController() {
        loadCsvData();

        // Inject shared dependencies into role controllers
        this.studentController =
                new StudentController(scanner, userManager, opportunityManager, applicationManager);
        this.staffController =
                new StaffController(scanner, userManager, opportunityManager, applicationManager);
        this.companyRepController =
                new CompanyRepController(scanner, userManager, opportunityManager, applicationManager);
    }

    // ================== CSV LOADING ==================
    private void loadCsvData() {
        String studentCsv = "sample_student_list.csv";
        String staffCsv   = "sample_staff_list.csv";

        List<Student> students = StudentLoader.load(studentCsv);
        for (Student s : students) {
            userManager.addStudent(s);
        }

        List<CareerCenterStaff> staff = StaffLoader.load(staffCsv);
        for (CareerCenterStaff c : staff) {
            userManager.addStaff(c);
        }

        System.out.println("===== CSV LOAD COMPLETE =====");
        System.out.println("Students           : " + students.size());
        System.out.println("Company Reps       : " + userManager.getAllRepresentatives().size());
        System.out.println("Career Center Staff: " + staff.size());
        System.out.println("================================\n");
    }

    //----------------------------------MAIN MENU----------------------------------
    public void run() {
        System.out.println("===== Internship Management System =====");
        System.out.println("Welcome! For first-time login, please use the default password: password");
        System.out.println("You can change your password after logging in.");

        while (true) {
            System.out.println("\nI am a:");
            System.out.println("1. Student");
            System.out.println("2. Staff");
            System.out.println("3. Company Representative");
            System.out.println("4. Quit");
            System.out.print("Enter choice: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> studentController.login();
                case 2 -> staffController.login();
                case 3 -> companyRepController.showLoginOrRegisterMenu();
                case 4 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    // ================== UTIL ==================
    private int readInt() {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid integer: ");
            }
        }
    }
}