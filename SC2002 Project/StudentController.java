import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

public class StudentController extends UserController {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public StudentController(Scanner scanner,
                         UserManager userManager,
                         OpportunityManager opportunityManager,
                         ApplicationManager applicationManager) {
    super(scanner, userManager, opportunityManager, applicationManager);
    }

    // --------- LOGIN FLOW ----------
    public void login() {
        System.out.print("\nEnter Student ID (e.g. U0000000A): ");
        String id = scanner.nextLine().trim();

        User user = userManager.findUserByID(id);

        if (!(user instanceof Student s)) {
            System.out.println("Invalid Student ID.");
            return;
        }

        if (!authenticate(s)) {
            System.out.println("Login failed.");
            return;
        }

        s.login();
        handleStudentMenu(s);
        s.logout();
    }

    // --------- MENU ----------
    private void handleStudentMenu(Student s) {
        int choice;
        do {
            s.displayMenu();
            System.out.print("Enter choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> {
                    System.out.print("Do you want to adjust your filters first? (y/n): ");
                    String ans = scanner.nextLine().trim().toLowerCase();
                    if (ans.equals("y")) {
                        configureStudentFilters(s);
                    }
                    List<InternshipOpportunity> visible = findEligibleInternshipsForStudent(s);
                    s.viewInternshipOpportunities(visible);
                }
                case 2 -> {
                    System.out.print("Do you want to adjust your filters first? (y/n): ");
                    String ans = scanner.nextLine().trim().toLowerCase();
                    if (ans.equals("y")) {
                        configureStudentFilters(s);
                    }
                    studentApplyForInternship(s);
                }
                case 3 -> s.viewApplicationStatus();
                case 4 -> studentRequestWithdrawal(s);
                case 5 -> studentConfirmApplication(s);
                case 6 -> changePassword(s);
                case 7 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 7);
    }

    // --------- FILTER CONFIG ----------
    private void configureStudentFilters(Student s) {
        InternshipFilterSettings fs = s.getFilterSettings();

        System.out.println("\n===== Configure Internship Filters =====");
        System.out.println("Current filters: " + fs.toSummaryString());
        System.out.println("Leave any field blank to keep it as ANY / unchanged.");

        // Status
        System.out.print("Filter by status (PENDING_APPROVAL / APPROVED / REJECTED / FILLED, blank for ANY): ");
        String statusInput = scanner.nextLine().trim().toUpperCase();
        if (!statusInput.isEmpty()) {
            try {
                fs.setStatusFilter(OpportunityStatus.valueOf(statusInput));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status. Keeping previous value.");
            }
        }

        // Preferred major
        System.out.print("Filter by preferred major (e.g. Computer Science, blank for ANY): ");
        String majorInput = scanner.nextLine().trim();
        if (!majorInput.isEmpty()) {
            fs.setPreferredMajorFilter(majorInput);
        }

        // Level
        System.out.print("Filter by level (BASIC / INTERMEDIATE / ADVANCED, blank for ANY): ");
        String levelInput = scanner.nextLine().trim().toUpperCase();
        if (!levelInput.isEmpty()) {
            try {
                fs.setLevelFilter(InternshipLevel.valueOf(levelInput));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid level. Keeping previous value.");
            }
        }

        // Closing date upper bound
        LocalDate closingBefore = readDateOrNull(
                "Filter by closing date (latest allowed yyyy-MM-dd, blank for ANY): ");
        if (closingBefore != null) {
            fs.setClosingDateBeforeFilter(closingBefore);
        }

        System.out.println("\nUpdated filters: " + fs.toSummaryString());
    }

    private LocalDate readDateOrNull(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;  // ANY
            }
            try {
                return LocalDate.parse(input, DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd (e.g. 2025-11-30), or leave blank for ANY.");
            }
        }
    }

    // --------- SHARED STREAM FILTER ----------
    private List<InternshipOpportunity> findEligibleInternshipsForStudent(Student s) {
        List<InternshipOpportunity> all = opportunityManager.getAllOpportunities();
        if (all.isEmpty()) {
            return List.of();
        }

        LocalDate today = LocalDate.now();
        InternshipFilterSettings fs = s.getFilterSettings();

        return all.stream()
                .filter(opp -> opp != null)
                .filter(InternshipOpportunity::isVisible)
                .filter(opp -> opp.getStatus() == OpportunityStatus.APPROVED)
                // requirement: cannot apply AFTER closing date
                .filter(opp -> !today.isAfter(opp.getClosingDate()))
                // basic eligibility
                .filter(opp -> opp.getPreferredMajor().equalsIgnoreCase(s.getMajor()))
                .filter(opp -> s.canApplyForLevel(opp.getLevel()))
                // apply saved filters
                .filter(opp -> {
                    OpportunityStatus f = fs.getStatusFilter();
                    return f == null || opp.getStatus() == f;
                })
                .filter(opp -> {
                    String m = fs.getPreferredMajorFilter();
                    return m == null || opp.getPreferredMajor().equalsIgnoreCase(m);
                })
                .filter(opp -> {
                    InternshipLevel lvl = fs.getLevelFilter();
                    return lvl == null || opp.getLevel() == lvl;
                })
                .filter(opp -> {
                    LocalDate cb = fs.getClosingDateBeforeFilter();
                    return cb == null || !opp.getClosingDate().isAfter(cb);
                })
                .sorted(Comparator.comparing(InternshipOpportunity::getTitle,
                                             String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    // --------- ACTIONS ----------
    private void studentApplyForInternship(Student s) {
        System.out.println("\n===== Available Internship Opportunities =====");

        List<InternshipOpportunity> eligible = findEligibleInternshipsForStudent(s);

        if (eligible.isEmpty()) {
            System.out.println("No internship opportunities available that match your profile and filters.");
            return;
        }

        // Just print once; toString already includes closing date
        eligible.forEach(System.out::println);

        System.out.print("\nEnter Internship ID to apply: ");
        String id = scanner.nextLine().trim();

        InternshipOpportunity selected = eligible.stream()
                .filter(opp -> opp.getInternshipID().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);

        if (selected == null) {
            System.out.println("You cannot apply for this internship (invalid ID or not in current filtered list).");
            return;
        }

        boolean success = s.applyForInternship(selected);
        if (success) {
            List<InternshipApplication> apps = s.getApplications();
            if (!apps.isEmpty()) {
                InternshipApplication latest = apps.get(apps.size() - 1);
                applicationManager.addApplication(latest);
            }
        }
    }

    private void studentRequestWithdrawal(Student s) {
        List<InternshipApplication> apps = s.getApplications();
        if (apps.isEmpty()) {
            System.out.println("\nYou have no applications to withdraw.");
            return;
        }

        List<InternshipApplication> eligible = new ArrayList<>();
        System.out.println("\n===== Your Applications (Eligible for Withdrawal Request) =====");

        int index = 1;
        for (InternshipApplication app : apps) {
            ApplicationStatus st = app.getStatus();
            if (st == ApplicationStatus.PENDING ||
                st == ApplicationStatus.SUCCESSFUL ||
                st == ApplicationStatus.CONFIRMED) {
                System.out.printf("%d. %s | Internship: %s | Status: %s%n",
                        index,
                        app.getApplicationID(),
                        app.getOpportunity().getTitle(),
                        st);
                eligible.add(app);
                index++;
            }
        }

        if (eligible.isEmpty()) {
            System.out.println("No applications are eligible for withdrawal request.");
            return;
        }

        System.out.print("Enter application number to request withdrawal (0 to cancel): ");
        int choice = readInt();
        if (choice == 0) return;
        if (choice < 1 || choice > eligible.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        InternshipApplication selected = eligible.get(choice - 1);
        s.requestWithdrawal(selected);
    }

    private void studentConfirmApplication(Student s) {
        List<InternshipApplication> apps = s.getApplications();
        if (apps.isEmpty()) {
            System.out.println("\nYou have no applications to confirm.");
            return;
        }

        List<InternshipApplication> eligible = new ArrayList<>();
        System.out.println("\n===== Your SUCCESSFUL Applications (Offers to Confirm) =====");

        int index = 1;
        for (InternshipApplication app : apps) {
            if (app.getStatus() == ApplicationStatus.SUCCESSFUL) {
                System.out.printf("%d. %s | Internship: %s | Status: %s%n",
                        index,
                        app.getApplicationID(),
                        app.getOpportunity().getTitle(),
                        app.getStatus());
                eligible.add(app);
                index++;
            }
        }

        if (eligible.isEmpty()) {
            System.out.println("You have no SUCCESSFUL applications to confirm.");
            return;
        }

        System.out.print("Enter application number to confirm (0 to cancel): ");
        int choice = readInt();
        if (choice == 0) return;
        if (choice < 1 || choice > eligible.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        InternshipApplication selected = eligible.get(choice - 1);
        s.confirmApplication(selected);
    }

}
