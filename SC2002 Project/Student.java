import java.util.ArrayList;
import java.util.List;

public class Student extends User implements HasMenu {
    private final int yearOfStudy;
    private final String major;
    private final List<InternshipApplication> applications = new ArrayList<>();
    private InternshipApplication acceptedPlacement;

    // Per-student saved filters
    private final InternshipFilterSettings filterSettings = new InternshipFilterSettings();

    public Student(String userID, String name, String major, int yearOfStudy, String email) {
        super(userID, name, email);
        if (major == null || major.trim().isEmpty()) {
            throw new IllegalArgumentException("Major required");
        }
        if (yearOfStudy < 1 || yearOfStudy > 4) {
            throw new IllegalArgumentException("Year of study must be between 1 and 4");
        }
        this.major = major.trim();
        this.yearOfStudy = yearOfStudy;
    }

    @Override
    public void displayMenu() {
        System.out.println("\n===== STUDENT MENU =====");
        System.out.println("1. View Internship Opportunities");
        System.out.println("2. Apply for Internship");
        System.out.println("3. View Application Status");
        System.out.println("4. Request Withdrawal of Application");
        System.out.println("5. Confirm Internship Offer");
        System.out.println("6. Change Password");
        System.out.println("7. Logout");
    }

    /**
     * Just displays the list given by the controller.
     * Filtering is done in StudentController to avoid duplication.
     */
    public void viewInternshipOpportunities(List<InternshipOpportunity> visible) {
        System.out.println("\n===== Internships for " + getName() + " (" + major + ", Y" + yearOfStudy + ") =====");
        if (visible == null || visible.isEmpty()) {
            System.out.println("No internship opportunities match your profile and filters right now.");
            return;
        }
        // toString() of InternshipOpportunity already prints Closing Date
        visible.forEach(System.out::println);
    }

    public boolean applyForInternship(InternshipOpportunity opportunity) {
        if (opportunity == null) {
            System.out.println("Invalid internship.");
            return false;
        }
        if (!opportunity.isVisible() || opportunity.getStatus() != OpportunityStatus.APPROVED) {
            System.out.println("This internship is not open for applications.");
            return false;
        }
        if (!opportunity.getPreferredMajor().equalsIgnoreCase(major)) {
            System.out.println("Your major does not match the preferred major.");
            return false;
        }
        if (!canApplyForLevel(opportunity.getLevel())) {
            System.out.println("You are not eligible to apply for this internship level.");
            return false;
        }

        if (hasReachedApplicationLimit()) {
            System.out.println("You already have 3 active applications (PENDING or SUCCESSFUL).");
            return false;
        }

        // prevent duplicate
        for (InternshipApplication app : applications) {
            if (app.getOpportunity() == opportunity &&
                app.getStatus() != ApplicationStatus.WITHDRAWN) {
                System.out.println("You have already applied for this internship.");
                return false;
            }
        }

        InternshipApplication newApp = new InternshipApplication(this, opportunity);
        applications.add(newApp);
        opportunity.addApplication(newApp);
        System.out.println("Applied successfully for: " + opportunity.getTitle());
        return true;
    }

    public void viewApplicationStatus() {
        System.out.println("\n===== Application Status for " + getName() + " =====");

        if (applications.isEmpty()) {
            System.out.println("You have not applied for any internships yet.");
            return;
        }

        for (InternshipApplication app : applications) {
            InternshipOpportunity opp = app.getOpportunity();

            System.out.println("--------------------------------------------");
            System.out.println("Application ID : " + app.getApplicationID());
            System.out.println("Internship     : " + opp.getTitle());
            System.out.println("Company        : " + opp.getCompanyName());
            System.out.println("Status         : " + app.getStatus());
            System.out.println("Date Applied   : " + app.getDateApplied());

            if (app.getRemarks() != null && !app.getRemarks().isBlank()) {
                System.out.println("Remarks        : " + app.getRemarks());
            }

            if (opp.getStatus() == OpportunityStatus.REJECTED) {
                System.out.println("This internship opportunity was REJECTED by Career Center Staff.");
            }
        }

        System.out.println("--------------------------------------------");

        if (acceptedPlacement != null) {
            System.out.println("Accepted placement: " +
                    acceptedPlacement.getOpportunity().getTitle());
        }
    }

    public boolean confirmApplication(InternshipApplication app) {
        if (app == null || app.getStudent() != this) {
            System.out.println("Invalid application.");
            return false;
        }

        if (app.getStatus() != ApplicationStatus.SUCCESSFUL) {
            System.out.println("You can only confirm applications that have been approved by the company.");
            return false;
        }

        app.setStatus(ApplicationStatus.CONFIRMED);
        setAcceptedPlacement(app);

        for (InternshipApplication other : applications) {
            if (other == app) continue;
            ApplicationStatus st = other.getStatus();
            if (st == ApplicationStatus.PENDING ||
                st == ApplicationStatus.SUCCESSFUL ||
                st == ApplicationStatus.WITHDRAW_REQUESTED) {
                other.setStatus(ApplicationStatus.WITHDRAWN);
            }
        }

        InternshipOpportunity opp = app.getOpportunity();
        opp.recalculateSlotsAndStatus();

        System.out.println("You have confirmed the internship: " +
                app.getOpportunity().getTitle() +
                ". All your other active applications have been withdrawn.");
        return true;
    }

    public boolean requestWithdrawal(InternshipApplication app) {
        if (app == null || app.getStudent() != this) {
            System.out.println("Invalid application.");
            return false;
        }

        ApplicationStatus st = app.getStatus();
        if (st == ApplicationStatus.WITHDRAWN || st == ApplicationStatus.WITHDRAW_REQUESTED) {
            System.out.println("This application is already withdrawn or has a pending withdrawal request.");
            return false;
        }

        // Remember what the status was before requesting withdrawal
        app.setPreviousStatus(st);

        app.setStatus(ApplicationStatus.WITHDRAW_REQUESTED);
        System.out.println("Withdrawal request submitted for application: " + app.getApplicationID());
        return true;
    }

    private boolean hasReachedApplicationLimit() {
        int active = 0;
        for (InternshipApplication app : applications) {
            ApplicationStatus st = app.getStatus();
            if (st == ApplicationStatus.PENDING ||
                st == ApplicationStatus.SUCCESSFUL) {
                active++;
            }
        }
        return active >= 3;
    }

    boolean canApplyForLevel(InternshipLevel level) {
        if (yearOfStudy <= 2) {
            return level == InternshipLevel.BASIC;
        }
        return true; // Y3 and above can apply for any level
    }

    public int getYearOfStudy() { return yearOfStudy; }
    public String getMajor()    { return major; }

    public List<InternshipApplication> getApplications() {
        return applications;
    }

    public InternshipApplication getAcceptedPlacement() {
        return acceptedPlacement;
    }

    public void setAcceptedPlacement(InternshipApplication app) {
        this.acceptedPlacement = app;
    }

    public InternshipFilterSettings getFilterSettings() {
        return filterSettings;
    }
}
