import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class CompanyRepController extends UserController{

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CompanyRepController(Scanner scanner,
                            UserManager userManager,
                            OpportunityManager opportunityManager,
                            ApplicationManager applicationManager) {
    super(scanner, userManager, opportunityManager, applicationManager);
    }

    // --------- LOGIN / REGISTER MENU ----------
    public void showLoginOrRegisterMenu() {
        while (true) {
            System.out.println("\n===== Company Representative LOG IN/SIGN UP MENU=====");
            System.out.println("1. Login");
            System.out.println("2. Register (For 1st time users ONLY!)");
            System.out.println("3. Back");
            System.out.print("Enter choice: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> companyRepLogin();
                case 2 -> registerCompanyRep();
                case 3 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // --------- LOGIN FLOW ----------
    private void companyRepLogin() {
        System.out.print("\nEnter Company Representative ID (Company Email Address): ");
        String id = scanner.nextLine().trim().toLowerCase();

        User user = userManager.findUserByID(id);

        if (!(user instanceof CompanyRepresentative rep)) {
            System.out.println("Invalid Company Representative ID.");
            return;
        }

        if (!rep.isApproved()) {
            System.out.println("Your registration is still pending approval by Career Center Staff.");
            return;
        }

        if (!authenticate(rep)) {
            System.out.println("Login failed.");
            return;
        }

        rep.login();
        handleCompanyRepMenu(rep);
        rep.logout();
    }

    private void registerCompanyRep() {
        System.out.println("\n===== Register as Company Representative =====");

        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter your company email (this WILL be your login ID): ");
        String email = scanner.nextLine().trim().toLowerCase();

        System.out.print("Enter your company name: ");
        String company = scanner.nextLine().trim();

        System.out.print("Enter your department: ");
        String department = scanner.nextLine().trim();

        System.out.print("Enter your position: ");
        String position = scanner.nextLine().trim();

        String newId = email;

        User existing = userManager.findUserByID(newId);
        if (existing != null) {
            System.out.println("\nA user with this email/User ID already exists.");
            System.out.println("If you forgot your password, please contact the Career Center Staff.");
            return;
        }

        CompanyRepresentative rep =
                new CompanyRepresentative(newId, name, email, company, department, position);

        rep.setApproved(false);
        userManager.addRepresentative(rep);

        System.out.println("\nRegistration submitted!");
        System.out.println("Your User ID: " + newId);
        System.out.println("Your Default Password: password");
        System.out.println("You can change your password after logging in.");
        System.out.println("Please wait for Career Center Staff approval before logging in.");
    }

    // --------- MENU ----------
    private void handleCompanyRepMenu(CompanyRepresentative rep) {
        int choice;
        do {
            rep.displayMenu();
            System.out.print("Enter choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> createInternshipFlow(rep);
                case 2 -> rep.viewMyInternships();
                case 3 -> viewApplicationsForRepInternship(rep);
                case 4 -> toggleInternshipVisibility(rep);
                case 5 -> editCompanyRepProfile(rep);
                case 6 -> changePassword(rep);
                case 7 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 7);
    }

    // --------- ACTIONS ----------
    private void createInternshipFlow(CompanyRepresentative rep) {
        if (!rep.isApproved()) {
            System.out.println("Your account is not approved by Career Center Staff yet.");
            return;
        }

        System.out.println("\n===== Create Internship Opportunity =====");
        System.out.print("Enter internship ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("Enter title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter description: ");
        String desc = scanner.nextLine().trim();

        System.out.print("Enter preferred major (e.g. CSC): ");
        String major = scanner.nextLine().trim();

        System.out.print("Enter level (BASIC / INTERMEDIATE / ADVANCED): ");
        String levelStr = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter slots (1-10): ");
        int slots = readInt();

        if (slots > 10) {
            System.out.println("You entered " + slots + " slots, but the maximum allowed is 10.");
            System.out.println("Slots will be set to 10.");
            slots = 10;
        } else if (slots < 1) {
            System.out.println("You entered " + slots + " slots, but the minimum allowed is 1.");
            System.out.println("Slots will be set to 1.");
            slots = 1;
        }

        LocalDate openingDate = readDate("Enter application opening date (yyyy-MM-dd): ");

        LocalDate closingDate;
        while (true) {
            closingDate = readDate("Enter application closing date (yyyy-MM-dd): ");
            if (!closingDate.isBefore(openingDate)) {
                break;
            }
            System.out.println("Closing date cannot be before opening date. Please re-enter closing date.");
        }

        InternshipLevel level;
        try {
            level = InternshipLevel.valueOf(levelStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid level. Defaulting to BASIC.");
            level = InternshipLevel.BASIC;
        }

        InternshipOpportunity opp = new InternshipOpportunity(
                id,
                title,
                desc,
                level,
                major,
                rep.getCompanyName(),
                rep.getUserID(),
                slots,
                openingDate,
                closingDate
        );

        boolean created = rep.createInternship(opp);
        if (created) {
            opportunityManager.addOpportunity(opp);
        }
    }

    private void viewApplicationsForRepInternship(CompanyRepresentative rep) {
        System.out.println("\n===== Your Internships =====");
        rep.viewMyInternships();

        System.out.print("Enter Internship ID to view applications: ");
        String id = scanner.nextLine().trim();

        InternshipOpportunity opp = opportunityManager.findByID(id);
        if (opp == null) {
            System.out.println("No internship found with that ID.");
            return;
        }

        if (!opp.getRepID().equalsIgnoreCase(rep.getUserID())) {
            System.out.println("You are not the owner of this internship.");
            return;
        }
   
        List<InternshipApplication> apps = opp.getApplications();
        if (apps.isEmpty()) {
            System.out.println("No applications yet.");
            return;
        }

        while (true) {
            System.out.println("\n===== Applications for " + opp.getTitle() + " =====");
            for (int i = 0; i < apps.size(); i++) {
                InternshipApplication app = apps.get(i);
                System.out.printf("%d. %s | Status: %s%n", i + 1, app.getApplicationID(), app.getStatus());
                System.out.println("   " + app);
            }

            System.out.print("Enter application number to approve/reject (0 to go back): ");
            int choice = readInt();
            if (choice == 0) {
                return;
            }
            if (choice < 1 || choice > apps.size()) {
                System.out.println("Invalid choice.");
                continue;
            }

            InternshipApplication selected = apps.get(choice - 1);

            ApplicationStatus st = selected.getStatus(); 

            if (st == ApplicationStatus.WITHDRAWN) {   
                System.out.println("This application has been withdrawn by the student and can no longer be approved or rejected.");
                continue;
            }

            if (st == ApplicationStatus.WITHDRAW_REQUESTED) { 
                System.out.println("The student has requested to withdraw this application. "
                                 + "It is pending review by Career Center Staff and cannot be approved or rejected.");
                continue;
            }
            
            System.out.println("1. Approve application");
            System.out.println("2. Reject application");
            System.out.println("3. Back");
            System.out.print("Enter choice: ");
            int action = readInt();

            if (action == 1) {
                selected.setStatus(ApplicationStatus.SUCCESSFUL);
                System.out.print("Optional remarks (leave blank to skip): ");
                String remarks = scanner.nextLine();
                if (!remarks.trim().isEmpty()) {
                    selected.setRemarks(remarks);
                }
                System.out.println("Application " + selected.getApplicationID() + " approved.");
            } else if (action == 2) {
                selected.setStatus(ApplicationStatus.UNSUCCESSFUL);
                System.out.print("Optional remarks (leave blank to skip): ");
                String remarks = scanner.nextLine();
                if (!remarks.trim().isEmpty()) {
                    selected.setRemarks(remarks);
                }
                System.out.println("Application " + selected.getApplicationID() + " rejected.");
            } else if (action == 3) {
                // Just loop again
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    private void toggleInternshipVisibility(CompanyRepresentative rep) {
        System.out.println("\n===== Manage Your Internships =====");
        rep.viewMyInternships();

        System.out.print("Enter Internship ID to manage: ");
        String id = scanner.nextLine().trim();

        InternshipOpportunity opp = opportunityManager.findByID(id);
        if (opp == null) {
            System.out.println("No internship found with that ID.");
            return;
        }

        if (!opp.getRepID().equalsIgnoreCase(rep.getUserID())) {
            System.out.println("You are not the owner of this internship.");
            return;
        }

        while (true) {
            System.out.println("\nManaging: " + opp);
            System.out.println("1. Toggle visibility");
            System.out.println("2. Edit opportunity (only if pending approval)");
            System.out.println("3. Delete opportunity (only if pending approval)");
            System.out.println("4. Back");
            System.out.print("Enter choice: ");
            int choice = readInt();

            switch (choice) {
                case 1 -> rep.toggleVisibility(opp);
                case 2 -> editOpportunityIfPending(rep, opp);
                case 3 -> {
                    deleteOpportunityIfPending(rep, opp);
                    return;
                }
                case 4 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
    
    private void editOpportunityIfPending(CompanyRepresentative rep, InternshipOpportunity opp) {
        if (opp.getStatus() != OpportunityStatus.PENDING_APPROVAL) {
            System.out.println("You can only edit opportunities that are pending approval.");
            return;
        }

        System.out.println("\n===== Edit Internship Opportunity (Pending Approval Only) =====");
        System.out.println("Leave a field blank to keep the current value.");

        System.out.println("Current title       : " + opp.getTitle());
        System.out.print("New title           : ");
        String newTitle = scanner.nextLine().trim();

        System.out.println("Current description : " + opp.getDescription());
        System.out.print("New description     : ");
        String newDesc = scanner.nextLine().trim();

        System.out.println("Current preferred major: " + opp.getPreferredMajor());
        System.out.print("New preferred major    : ");
        String newMajor = scanner.nextLine().trim();

        System.out.println("Current level: " + opp.getLevel());
        System.out.print("New level (BASIC / INTERMEDIATE / ADVANCED): ");
        String levelStr = scanner.nextLine().trim().toUpperCase();

        System.out.println("Current slots: " + opp.getSlotsAvailable());
        System.out.print("New slots (1-10): ");
        String slotsLine = scanner.nextLine().trim();
        Integer newSlots = null;
        if (!slotsLine.isEmpty()) {
            try {
                newSlots = Integer.parseInt(slotsLine);
            } catch (NumberFormatException e) {
                System.out.println("Invalid slots value, keeping existing.");
            }
        }

        String finalTitle = newTitle.isEmpty() ? opp.getTitle() : newTitle;
        String finalDesc  = newDesc.isEmpty() ? opp.getDescription() : newDesc;
        String finalMajor = newMajor.isEmpty() ? opp.getPreferredMajor() : newMajor;

        InternshipLevel finalLevel = opp.getLevel();
        if (!levelStr.isEmpty()) {
            try {
                finalLevel = InternshipLevel.valueOf(levelStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid level, keeping existing.");
            }
        }

        int finalSlots = (newSlots == null) ? opp.getSlotsAvailable() : newSlots;

        opportunityManager.removeOpportunity(opp);
        rep.removeInternship(opp);

        InternshipOpportunity updated = new InternshipOpportunity(
                opp.getInternshipID(),
                finalTitle,
                finalDesc,
                finalLevel,
                finalMajor,
                opp.getCompanyName(),
                opp.getRepID(),
                finalSlots,
                opp.getOpeningDate(),
                opp.getClosingDate()
        );

        boolean created = rep.createInternship(updated);
        if (created) {
            opportunityManager.addOpportunity(updated);
            System.out.println("Internship opportunity updated.");
        } else {
            System.out.println("Updated opportunity could not be added (limit or approval issue).");
        }
    }
    
    private void deleteOpportunityIfPending(CompanyRepresentative rep, InternshipOpportunity opp) {
        if (opp.getStatus() != OpportunityStatus.PENDING_APPROVAL) {
            System.out.println("You can only delete opportunities that are pending approval.");
            return;
        }

        System.out.print("Are you sure you want to delete this opportunity? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        opportunityManager.removeOpportunity(opp);
        rep.removeInternship(opp);
        System.out.println("Internship opportunity deleted.");
    }
    
    private void editCompanyRepProfile(CompanyRepresentative rep) {
        System.out.println("\n===== Edit Profile =====");
        System.out.println("Current name   : " + rep.getName());
        System.out.println("Current email  : " + rep.getEmail());
        System.out.println("Current company: " + rep.getCompanyName());

        System.out.print("\nEnter new name (or leave blank to keep current): ");
        String newName = scanner.nextLine().trim();

        if (!newName.isEmpty()) {
            rep.setName(newName);
        }

        System.out.print("Enter new email (or leave blank to keep current): ");
        String newEmail = scanner.nextLine().trim();

        if (!newEmail.isEmpty()) {
            rep.setEmail(newEmail);
        }

        System.out.print("Enter new company name (or leave blank to keep current): ");
        String newCompany = scanner.nextLine().trim();

        if (!newCompany.isEmpty()) {
            rep.setCompanyName(newCompany);
        }

        System.out.println("\nProfile updated:");
        System.out.println(rep.getName() + " @ " + rep.getCompanyName());
    }

    @Override
    public void login() {
        showLoginOrRegisterMenu();
    }

    // --------- DATE & INPUT UTIL ----------
    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd (e.g. 2025-03-15).");
            }
        }
    }
}
