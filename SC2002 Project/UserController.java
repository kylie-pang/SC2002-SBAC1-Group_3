import java.util.Scanner;

public abstract class UserController {

    protected final Scanner scanner;
    protected final UserManager userManager;
    protected final OpportunityManager opportunityManager;
    protected final ApplicationManager applicationManager;

    protected UserController(Scanner scanner,
                             UserManager userManager,
                             OpportunityManager opportunityManager,
                             ApplicationManager applicationManager) {
        this.scanner = scanner;
        this.userManager = userManager;
        this.opportunityManager = opportunityManager;
        this.applicationManager = applicationManager;
    }

    // Every concrete controller (StudentController, StaffController, CompanyRepController)
    // will define how login works.
    public abstract void login();

    // --------- COMMON AUTH & PASSWORD HELPERS ----------

    protected boolean authenticate(User user) {
        System.out.print("Enter password (case-sensitive!): ");
        String pw = scanner.nextLine();

        if (!user.checkPassword(pw)) {
            System.out.println("Incorrect password.");
            return false;
        }
        return true;
    }

    protected void changePassword(User user) {
        System.out.println("\n===== Change Password =====");
        System.out.print("Enter current password (case-sensitive!): ");
        String current = scanner.nextLine();

        if (!user.checkPassword(current)) {
            System.out.println("Incorrect current password. Password not changed.");
            return;
        }

        System.out.print("Enter new password (case-sensitive!): ");
        String newPw = scanner.nextLine().trim();

        try {
            user.changePassword(newPw);
        } catch (IllegalArgumentException e) {
            System.out.println("Password not changed: " + e.getMessage());
            return;
        }

        System.out.println("Password updated successfully.");
    }

    // --------- COMMON UTIL ----------

    protected int readInt() {
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