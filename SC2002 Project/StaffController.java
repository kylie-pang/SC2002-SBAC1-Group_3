import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

public class StaffController extends UserController {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public StaffController(Scanner scanner,
                       UserManager userManager,
                       OpportunityManager opportunityManager,
                       ApplicationManager applicationManager) {
    super(scanner, userManager, opportunityManager, applicationManager);
    }

    // --------- LOGIN FLOW ----------
    public void login() {
        System.out.print("\nEnter Staff ID (NTU account): ");
        String id = scanner.nextLine().trim();

        User user = userManager.findUserByID(id);

        if (!(user instanceof CareerCenterStaff staff)) {
            System.out.println("Invalid Staff ID.");
            return;
        }

        if (!authenticate(staff)) {
            System.out.println("Login failed.");
            return;
        }

        staff.login();
        handleStaffMenu(staff);
        staff.logout();
    }

    // --------- MENU ----------
    private void handleStaffMenu(CareerCenterStaff staff) {
        int choice;
        do {
            staff.displayMenu();
            System.out.print("Enter choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> approveRepsFlow(staff);
                case 2 -> approveOpportunitiesFlow(staff);
                case 3 -> manageWithdrawalRequests(staff);
                case 4 -> generateOpportunityReports(staff);
                case 5 -> changePassword(staff);
                case 6 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 6);
    }

    // --------- ACTIONS ----------
    private void approveRepsFlow(CareerCenterStaff staff) {
        System.out.println("\n===== Company Representatives (PENDING account creation)=====");
        List<CompanyRepresentative> reps = userManager.getAllRepresentatives();

        if (reps.isEmpty()) {
            System.out.println("No company representatives in the system.");
            return;
        }

        for (CompanyRepresentative rep : reps) {
            System.out.println(
                rep.getUserID() + " - " +
                rep.getName() + " @ " + rep.getCompanyName() +
                " | Approved: " + rep.isApproved()
            );
        }

        System.out.print("Enter Rep ID to approve: ");
        String id = scanner.nextLine().trim();

        User u = userManager.findUserByID(id);
        if (u instanceof CompanyRepresentative) {
            CompanyRepresentative rep = (CompanyRepresentative) u;
            staff.approveRepresentative(rep);
        } else {
            System.out.println("No such representative.");
        }
    }

    private void manageWithdrawalRequests(CareerCenterStaff staff) {
        System.out.println("\n===== Withdrawal Requests =====");
        List<InternshipApplication> allApps = applicationManager.getAllApplications();
        List<InternshipApplication> pending = new ArrayList<>();

        for (InternshipApplication app : allApps) {
            if (app.getStatus() == ApplicationStatus.WITHDRAW_REQUESTED) {
                pending.add(app);
            }
        }

        if (pending.isEmpty()) {
            System.out.println("There are no withdrawal requests at the moment.");
            return;
        }

        while (true) {
            System.out.println("\nWithdrawal Requests:");
            for (int i = 0; i < pending.size(); i++) {
                InternshipApplication app = pending.get(i);
                System.out.printf("%d. %s | Student: %s | Internship: %s | Date: %s%n",
                        i + 1,
                        app.getApplicationID(),
                        app.getStudent().getUserID(),
                        app.getOpportunity().getTitle(),
                        app.getDateApplied());
            }

            System.out.print("Enter request number to approve withdrawal (0 to go back): ");
            int choice = readInt();
            if (choice == 0) return;
            if (choice < 1 || choice > pending.size()) {
                System.out.println("Invalid choice.");
                continue;
            }

            InternshipApplication selected = pending.get(choice - 1);
            System.out.print("Approve withdrawal for this application? (y/n): ");
            String ans = scanner.nextLine().trim().toLowerCase();

            if (!ans.equals("y")) {
                System.out.println("Withdrawal not approved.");

                // Restore the previous status (e.g. CONFIRMED)
                ApplicationStatus prev = selected.getPreviousStatus();
                if (prev != null) {
                    selected.setStatus(prev);
                } else if (selected.getStudent().getAcceptedPlacement() == selected) {
                    // Fallback: if no previousStatus stored but this is the accepted placement,
                    // assume it should be CONFIRMED
                    selected.setStatus(ApplicationStatus.CONFIRMED);
                }

                // Add remark about the rejection
                String existingRemarks = selected.getRemarks();
                String extra = "Withdrawal request rejected by Career Center Staff.";
                if (existingRemarks == null || existingRemarks.isBlank()) {
                    selected.setRemarks(extra);
                } else {
                    selected.setRemarks(existingRemarks + " | " + extra);
                }

                continue;
            }

            // If we reach here, staff approved the withdrawal
            selected.setStatus(ApplicationStatus.WITHDRAWN);
            // Clear previous status (no longer needed)
            selected.setPreviousStatus(null);

            Student st = selected.getStudent();
            if (st.getAcceptedPlacement() == selected) {
                st.setAcceptedPlacement(null);
            }

            InternshipOpportunity opp = selected.getOpportunity();
            opp.recalculateSlotsAndStatus();

            System.out.println("Withdrawal approved for application: " + selected.getApplicationID());

            pending.remove(choice - 1);
            if (pending.isEmpty()) {
                System.out.println("No more withdrawal requests.");
                return;
            }
        }
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

    // ====== Modern Java: Stream-based filtering for approval screen ======
    private void approveOpportunitiesFlow(CareerCenterStaff staff) {
        System.out.println("\n===== Internship Opportunities =====");
        List<InternshipOpportunity> all = opportunityManager.getAllOpportunities();

        if (all.isEmpty()) {
            System.out.println("No opportunities in the system.");
            return;
        }

        System.out.println("You can filter the list before approving/rejecting.");
        System.out.println("Leave any field blank for ANY.");

        // ----- STATUS -----
        System.out.print("Filter by status (PENDING_APPROVAL / APPROVED / REJECTED / FILLED, blank for ANY): ");
        String statusInput = scanner.nextLine().trim().toUpperCase();

        OpportunityStatus tempStatusFilter = null;
        if (!statusInput.isEmpty()) {
            try {
                tempStatusFilter = OpportunityStatus.valueOf(statusInput);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status entered. No status filter will be applied.");
            }
        }
        final OpportunityStatus statusFilter = tempStatusFilter;   // effectively final

        // ----- MAJOR -----
        System.out.print("Filter by preferred major (e.g. Data Science & AI, blank for ANY): ");
        String majorInput = scanner.nextLine().trim();
        final String majorFilter = majorInput.isEmpty() ? null : majorInput;  // single assignment

        // ----- LEVEL -----
        System.out.print("Filter by level (BASIC / INTERMEDIATE / ADVANCED, blank for ANY): ");
        String levelInput = scanner.nextLine().trim().toUpperCase();

        InternshipLevel tempLevelFilter = null;
        if (!levelInput.isEmpty()) {
            try {
                tempLevelFilter = InternshipLevel.valueOf(levelInput);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid level entered. No level filter will be applied.");
            }
        }
        final InternshipLevel levelFilter = tempLevelFilter;   // effectively final

        // ----- CLOSING DATE -----
        final LocalDate closingBefore = readDateOrNull(
                "Filter by closing date (latest allowed yyyy-MM-dd, blank for ANY): ");

        // ===== Stream API filtering =====
        List<InternshipOpportunity> filtered = all.stream()
                .filter(opp -> opp != null)
                .filter(opp -> statusFilter == null || opp.getStatus() == statusFilter)
                .filter(opp -> majorFilter == null ||
                            opp.getPreferredMajor().equalsIgnoreCase(majorFilter))
                .filter(opp -> levelFilter == null || opp.getLevel() == levelFilter)
                .filter(opp -> closingBefore == null ||
                            !opp.getClosingDate().isAfter(closingBefore))
                .sorted(Comparator.comparing(InternshipOpportunity::getTitle,
                                            String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            System.out.println("No opportunities match the specified filters.");
            return;
        }

        filtered.forEach(System.out::println);

        System.out.print("Enter Internship ID to approve/reject: ");
        String id = scanner.nextLine().trim();

        InternshipOpportunity opp = filtered.stream()
                .filter(o -> o.getInternshipID().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);

        if (opp == null) {
            System.out.println("No such opportunity in the filtered list.");
            return;
        }

        System.out.println("1. Approve");
        System.out.println("2. Reject");
        System.out.print("Enter choice: ");
        int choice = readInt();

        if (choice == 1) {
            staff.approveOpportunity(opp);
            opp.setVisible(true); // visible to students after approval
        } else if (choice == 2) {
            staff.rejectOpportunity(opp);
        } else {
            System.out.println("Invalid choice.");
        }
    }

    // --------- REPORTS (unchanged logic) ----------
    private void generateOpportunityReports(CareerCenterStaff staff) {
        System.out.println("\n===== Internship Opportunity Reports =====");
        System.out.println("You can leave any field blank to ignore that filter.");

        System.out.print("Filter by status (PENDING_APPROVAL / APPROVED / REJECTED / FILLED, or blank for ANY): ");
        String statusInput = scanner.nextLine().trim().toUpperCase();
        OpportunityStatus statusFilter = null;
        if (!statusInput.isEmpty()) {
            try {
                statusFilter = OpportunityStatus.valueOf(statusInput);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status entered. Status filter will be ignored.");
                statusFilter = null;
            }
        }

        System.out.print("Filter by preferred major (e.g. CSC, blank for ANY): ");
        String majorFilter = scanner.nextLine().trim();
        if (majorFilter.isEmpty()) {
            majorFilter = null;
        }

        System.out.print("Filter by level (BASIC / INTERMEDIATE / ADVANCED, or blank for ANY): ");
        String levelInput = scanner.nextLine().trim().toUpperCase();
        InternshipLevel levelFilter = null;
        if (!levelInput.isEmpty()) {
            try {
                levelFilter = InternshipLevel.valueOf(levelInput);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid level entered. Level filter will be ignored.");
                levelFilter = null;
            }
        }

        System.out.print("Filter by company name (exact match, blank for ANY): ");
        String companyFilter = scanner.nextLine().trim();
        if (companyFilter.isEmpty()) {
            companyFilter = null;
        }

        List<InternshipOpportunity> filtered =
                opportunityManager.filterOpportunities(statusFilter, majorFilter, levelFilter, companyFilter);

        System.out.println("\n===== REPORT RESULTS =====");
        System.out.println("Total matching opportunities: " + filtered.size());

        if (filtered.isEmpty()) {
            System.out.println("No opportunities match the specified filters.");
            return;
        }

        int pendingCount = 0, approvedCount = 0, rejectedCount = 0, filledCount = 0;

        for (InternshipOpportunity opp : filtered) {
            OpportunityStatus st = opp.getStatus();
            switch (st) {
                case PENDING_APPROVAL -> pendingCount++;
                case APPROVED         -> approvedCount++;
                case REJECTED         -> rejectedCount++;
                case FILLED           -> filledCount++;
            }

            int totalApps = opp.getApplications().size();
            int confirmed = 0;
            int successful = 0;
            int pending = 0;
            for (InternshipApplication app : opp.getApplications()) {
                switch (app.getStatus()) {
                    case CONFIRMED    -> confirmed++;
                    case SUCCESSFUL   -> successful++;
                    case PENDING      -> pending++;
                    default -> { }
                }
            }

            System.out.println("\n" + opp);
            System.out.println("  Applications: total=" + totalApps +
                    ", PENDING=" + pending +
                    ", SUCCESSFUL=" + successful +
                    ", CONFIRMED=" + confirmed);
        }

        System.out.println("\n===== SUMMARY BY STATUS =====");
        System.out.println("PENDING_APPROVAL: " + pendingCount);
        System.out.println("APPROVED        : " + approvedCount);
        System.out.println("REJECTED        : " + rejectedCount);
        System.out.println("FILLED          : " + filledCount);
    }

}